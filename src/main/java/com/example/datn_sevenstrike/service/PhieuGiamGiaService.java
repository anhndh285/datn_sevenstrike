package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaResponse;
import com.example.datn_sevenstrike.entity.KhachHang;
import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import com.example.datn_sevenstrike.entity.PhieuGiamGiaCaNhan;
import com.example.datn_sevenstrike.entity.PhieuGiamGiaChiTiet;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.KhachHangRepository;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaCaNhanRepository;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaChiTietRepository;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class PhieuGiamGiaService {

    private final PhieuGiamGiaRepository repo;
    private final KhachHangRepository khachHangRepo;
    private final PhieuGiamGiaChiTietRepository chiTietRepo;
    private final PhieuGiamGiaCaNhanRepository caNhanRepo;
    private final ModelMapper mapper;

    private final VoucherEmailService voucherEmailService;

    public List<PhieuGiamGiaResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PhieuGiamGiaResponse one(Integer id) {
        PhieuGiamGia e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));
        return toResponse(e);
    }

    @Transactional
    public PhieuGiamGiaResponse create(PhieuGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        PhieuGiamGia e = mapper.map(req, PhieuGiamGia.class);
        e.setId(null);

        applyDefaults(e);

        List<Integer> ids = normalizeCustomerIds(req.getIdKhachHangs());
        boolean caNhan = isCaNhan(req, ids);

        if (caNhan) {
            e.setSoLuongSuDung(ids.size());
        } else {
            if (req.getSoLuongSuDung() != null) e.setSoLuongSuDung(req.getSoLuongSuDung());
        }

        validate(e, caNhan, ids);

        PhieuGiamGia saved = repo.saveAndFlush(e);

        if (caNhan) {
            replaceVoucherCaNhan(saved.getId(), ids, true);
            syncChiTietForUi(saved, ids);
        } else {
            caNhanRepo.softDeleteAliveByVoucherId(saved.getId());
            chiTietRepo.deleteByPhieuGiamGia(saved);
        }

        return toResponse(saved);
    }

    /**
     * UPDATE:
     * - Nếu req.idKhachHangs != null => user muốn cấu hình voucher cá nhân theo list (bắt buộc list có phần tử)
     * - Nếu req.idKhachHangs == null => không động tới danh sách KH (giữ trạng thái cá nhân hiện tại nếu đang là cá nhân)
     * - Không cho update xoaMem qua request.
     */
    @Transactional
    public PhieuGiamGiaResponse update(Integer id, PhieuGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        PhieuGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));

        boolean dbIsCaNhan = hasAnyAliveCaNhan(id);

        // ===== map field =====
        if (req.getTenPhieuGiamGia() != null) db.setTenPhieuGiamGia(req.getTenPhieuGiamGia());
        if (req.getLoaiPhieuGiamGia() != null) db.setLoaiPhieuGiamGia(req.getLoaiPhieuGiamGia());
        if (req.getGiaTriGiamGia() != null) db.setGiaTriGiamGia(req.getGiaTriGiamGia());

        if (req.getSoTienGiamToiDa() != null) db.setSoTienGiamToiDa(req.getSoTienGiamToiDa());
        if (req.getHoaDonToiThieu() != null) db.setHoaDonToiThieu(req.getHoaDonToiThieu());

        if (req.getSoLuongSuDung() != null) db.setSoLuongSuDung(req.getSoLuongSuDung());

        if (req.getNgayBatDau() != null) db.setNgayBatDau(req.getNgayBatDau());
        if (req.getNgayKetThuc() != null) db.setNgayKetThuc(req.getNgayKetThuc());

        // ✅ FIX: cho phép update trạng thái độc lập (FE gạt on/off)
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());

        if (req.getMoTa() != null) db.setMoTa(req.getMoTa());

        applyDefaults(db);

        // ===== xử lý cá nhân theo request =====
        boolean reqHasCustomerListField = (req.getIdKhachHangs() != null);

        List<Integer> idsReq = normalizeCustomerIds(req.getIdKhachHangs());

        boolean finalIsCaNhan = dbIsCaNhan;
        if (reqHasCustomerListField) {
            if (idsReq.isEmpty()) throw new BadRequestEx("Voucher cá nhân: vui lòng chọn ít nhất 1 khách hàng");
            finalIsCaNhan = true;
        }

        // ✅ FIX: voucher cá nhân nhưng request KHÔNG gửi list => lấy list hiện có để validate
        List<Integer> idsValidate = idsReq;
        if (finalIsCaNhan && !reqHasCustomerListField) {
            idsValidate = getCustomerIdsByVoucher(id);
        }

        // ===== chốt so_luong_su_dung (lượt còn lại) =====
        if (finalIsCaNhan) {
            if (reqHasCustomerListField) {
                db.setSoLuongSuDung(idsReq.size());
            } else {
                if (db.getSoLuongSuDung() == null) db.setSoLuongSuDung(0);
            }
        } else {
            if (db.getSoLuongSuDung() == null) db.setSoLuongSuDung(0);
        }

        // ✅ validate bằng idsValidate (đặc biệt khi chỉ đổi trạng thái)
        validate(db, finalIsCaNhan, idsValidate);

        PhieuGiamGia saved = repo.saveAndFlush(db);

        // ===== đồng bộ bảng cá nhân/chi tiết =====
        if (dbIsCaNhan && !finalIsCaNhan) {
            caNhanRepo.softDeleteAliveByVoucherId(saved.getId());
            chiTietRepo.deleteByPhieuGiamGia(saved);
        }

        if (finalIsCaNhan && reqHasCustomerListField) {
            replaceVoucherCaNhan(saved.getId(), idsReq, false);
            syncChiTietForUi(saved, idsReq);
        }

        if (!finalIsCaNhan) {
            caNhanRepo.softDeleteAliveByVoucherId(saved.getId());
            chiTietRepo.deleteByPhieuGiamGia(saved);
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        PhieuGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));

        db.setXoaMem(true);
        repo.save(db);

        caNhanRepo.softDeleteAliveByVoucherId(id);
        chiTietRepo.deleteByPhieuGiamGia(db);
    }

    public List<Integer> getCustomerIdsByVoucher(Integer voucherId) {
        List<PhieuGiamGiaCaNhan> rows = caNhanRepo.findAllByIdPhieuGiamGiaAndXoaMemFalseOrderByIdDesc(voucherId);
        if (rows != null && !rows.isEmpty()) {
            return rows.stream()
                    .map(PhieuGiamGiaCaNhan::getIdKhachHang)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        }

        PhieuGiamGia p = repo.findById(voucherId).orElse(null);
        if (p == null) return new ArrayList<>();

        return chiTietRepo.findAllByPhieuGiamGiaAndXoaMemFalse(p)
                .stream()
                .map(ct -> ct.getKhachHang().getId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    // ========================= Helpers =========================

    private boolean isCaNhan(PhieuGiamGiaRequest req, List<Integer> ids) {
        return req != null && req.getIdKhachHangs() != null && ids != null && !ids.isEmpty();
    }

    private boolean hasAnyAliveCaNhan(Integer voucherId) {
        List<PhieuGiamGiaCaNhan> rows = caNhanRepo.findAllByIdPhieuGiamGiaAndXoaMemFalseOrderByIdDesc(voucherId);
        return rows != null && !rows.isEmpty();
    }

    private List<Integer> normalizeCustomerIds(List<Integer> ids) {
        if (ids == null) return new ArrayList<>();
        return ids.stream()
                .filter(Objects::nonNull)
                .map(Integer::valueOf)
                .filter(x -> x > 0)
                .distinct()
                .collect(Collectors.toList());
    }

    private void applyDefaults(PhieuGiamGia e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);

        // false= % ; true= tiền
        if (e.getLoaiPhieuGiamGia() == null) e.setLoaiPhieuGiamGia(false);

        if (e.getTrangThai() == null) e.setTrangThai(true);

        if (e.getTenPhieuGiamGia() != null) e.setTenPhieuGiamGia(e.getTenPhieuGiamGia().trim());
        if (e.getMoTa() != null) e.setMoTa(e.getMoTa().trim());

        if (e.getGiaTriGiamGia() == null) e.setGiaTriGiamGia(BigDecimal.ZERO);

        if (e.getHoaDonToiThieu() == null) e.setHoaDonToiThieu(BigDecimal.ZERO);

        if (e.getSoLuongSuDung() == null) e.setSoLuongSuDung(0);
    }

    private void validate(PhieuGiamGia e, boolean caNhan, List<Integer> ids) {
        if (e.getTenPhieuGiamGia() == null || e.getTenPhieuGiamGia().isBlank())
            throw new BadRequestEx("Tên phiếu không được để trống");

        if (e.getNgayBatDau() == null || e.getNgayKetThuc() == null)
            throw new BadRequestEx("Thiếu ngày bắt đầu/kết thúc");

        if (e.getNgayKetThuc().isBefore(e.getNgayBatDau()))
            throw new BadRequestEx("Ngày kết thúc phải >= ngày bắt đầu");

        if (e.getHoaDonToiThieu() != null && e.getHoaDonToiThieu().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestEx("Hóa đơn tối thiểu phải >= 0");

        if (e.getSoLuongSuDung() != null && e.getSoLuongSuDung() < 0)
            throw new BadRequestEx("Số lượng sử dụng phải >= 0");

        boolean giamTheoTien = Boolean.TRUE.equals(e.getLoaiPhieuGiamGia());
        if (!giamTheoTien) {
            if (e.getGiaTriGiamGia() == null)
                throw new BadRequestEx("Giảm %: giá trị giảm không được để trống");
            if (e.getGiaTriGiamGia().compareTo(BigDecimal.ZERO) < 0
                    || e.getGiaTriGiamGia().compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestEx("Giảm %: giá trị giảm phải trong khoảng 0..100");
            }
        } else {
            if (e.getGiaTriGiamGia() == null || e.getGiaTriGiamGia().compareTo(BigDecimal.ZERO) <= 0)
                throw new BadRequestEx("Giảm tiền: giá trị giảm phải > 0");
        }

        if (e.getSoTienGiamToiDa() != null && e.getSoTienGiamToiDa().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestEx("Số tiền giảm tối đa phải >= 0");

        if (caNhan) {
            if (ids == null || ids.isEmpty())
                throw new BadRequestEx("Voucher cá nhân: vui lòng chọn ít nhất 1 khách hàng");
        }
    }

    private void replaceVoucherCaNhan(Integer voucherId, List<Integer> idKhachHangs, boolean shouldSendEmail) {
        caNhanRepo.softDeleteAliveByVoucherId(voucherId);

        List<String> emails = new ArrayList<>();
        List<PhieuGiamGiaCaNhan> news = new ArrayList<>();

        for (Integer idKh : idKhachHangs) {
            KhachHang kh = khachHangRepo.findById(idKh)
                    .orElseThrow(() -> new BadRequestEx("Không tìm thấy khách hàng id=" + idKh));

            if (caNhanRepo.existsByIdKhachHangAndIdPhieuGiamGiaAndXoaMemFalse(kh.getId(), voucherId)) {
                continue;
            }

            news.add(PhieuGiamGiaCaNhan.builder()
                    .idKhachHang(kh.getId())
                    .idPhieuGiamGia(voucherId)
                    .ngayNhan(LocalDate.now())
                    .daSuDung(false)
                    .xoaMem(false)
                    .build());

            if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                emails.add(kh.getEmail().trim());
            }
        }

        if (!news.isEmpty()) caNhanRepo.saveAll(news);

        if (shouldSendEmail && !emails.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    voucherEmailService.sendVoucherEmailAsync(emails, voucherId);
                }
            });
        }
    }

    private void syncChiTietForUi(PhieuGiamGia saved, List<Integer> idKhachHangs) {
        chiTietRepo.deleteByPhieuGiamGia(saved);

        List<PhieuGiamGiaChiTiet> listCt = new ArrayList<>();
        for (Integer idKh : idKhachHangs) {
            KhachHang kh = khachHangRepo.findById(idKh)
                    .orElseThrow(() -> new BadRequestEx("Không tìm thấy khách hàng id=" + idKh));

            listCt.add(PhieuGiamGiaChiTiet.builder()
                    .phieuGiamGia(saved)
                    .khachHang(kh)
                    .xoaMem(false)
                    .build());
        }
        if (!listCt.isEmpty()) chiTietRepo.saveAll(listCt);
    }

    private PhieuGiamGiaResponse toResponse(PhieuGiamGia e) {
        return mapper.map(e, PhieuGiamGiaResponse.class);
    }
}
