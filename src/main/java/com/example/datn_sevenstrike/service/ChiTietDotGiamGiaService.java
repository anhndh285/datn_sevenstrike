// File: src/main/java/com/example/datn_sevenstrike/service/ChiTietDotGiamGiaService.java
package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ChiTietDotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.ChiTietDotGiamGiaResponse;
import com.example.datn_sevenstrike.entity.ChiTietDotGiamGia;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietDotGiamGiaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChiTietDotGiamGiaService {

    private final ChiTietDotGiamGiaRepository repo;
    private final ModelMapper mapper;

    public List<ChiTietDotGiamGiaResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<ChiTietDotGiamGiaResponse> byDotGiamGia(Integer idDotGiamGia) {
        if (idDotGiamGia == null) throw new BadRequestEx("Thiếu id_dot_giam_gia");
        return repo.findAllByIdDotGiamGiaAndXoaMemFalseOrderByIdDesc(idDotGiamGia)
                .stream().map(this::toResponse).toList();
    }

    public ChiTietDotGiamGiaResponse one(Integer id) {
        ChiTietDotGiamGia e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChiTietDotGiamGia id=" + id));
        return toResponse(e);
    }

    @Transactional
    public ChiTietDotGiamGiaResponse create(ChiTietDotGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        ChiTietDotGiamGia e = mapper.map(req, ChiTietDotGiamGia.class);
        e.setId(null);

        applyDefaults(e, true);
        validateRequired(e);
        validateBusiness(e);
        validateDuplicateCreate(e);

        try {
            return toResponse(repo.save(e));
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestEx("Không thể tạo chi tiết đợt giảm giá: dữ liệu không hợp lệ hoặc CTSP đã có trong đợt.");
        }
    }

    @Transactional
    public ChiTietDotGiamGiaResponse update(Integer id, ChiTietDotGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        ChiTietDotGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChiTietDotGiamGia id=" + id));

        if (req.getIdDotGiamGia() != null) db.setIdDotGiamGia(req.getIdDotGiamGia());
        if (req.getIdChiTietSanPham() != null) db.setIdChiTietSanPham(req.getIdChiTietSanPham());

        if (req.getSoLuongApDung() != null) db.setSoLuongApDung(req.getSoLuongApDung());
        if (req.getGiaTriGiamRieng() != null) db.setGiaTriGiamRieng(req.getGiaTriGiamRieng());
        if (req.getSoTienGiamToiDaRieng() != null) db.setSoTienGiamToiDaRieng(req.getSoTienGiamToiDaRieng());

        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu());

        if (req.getNguoiCapNhat() != null) db.setNguoiCapNhat(req.getNguoiCapNhat());

        applyDefaults(db, false);
        validateRequired(db);
        validateBusiness(db);
        validateDuplicateUpdate(db);

        try {
            return toResponse(repo.save(db));
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestEx("Không thể cập nhật chi tiết đợt giảm giá: dữ liệu không hợp lệ hoặc CTSP đã có trong đợt.");
        }
    }

    @Transactional
    public void delete(Integer id) {
        ChiTietDotGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChiTietDotGiamGia id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    // =========================
    // POS: chọn đợt giảm tốt nhất đang active theo CTSP
    // =========================

    @Transactional(readOnly = true)
    public ChiTietDotGiamGiaResponse bestForCtspBanHang(Integer idChiTietSanPham) {
        if (idChiTietSanPham == null || idChiTietSanPham <= 0) return null;

        LocalDate today = LocalDate.now();
        return repo.findBestActiveDotByCtspId(idChiTietSanPham, today)
                .map(this::toPosResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ChiTietDotGiamGiaResponse> bestForCtspIdsBanHang(List<Integer> idChiTietSanPhams) {
        List<Integer> ids = normalizeIds(idChiTietSanPhams);
        if (ids.isEmpty()) return new ArrayList<>();

        LocalDate today = LocalDate.now();
        List<ChiTietDotGiamGiaRepository.BestDotGiamGiaView> views = repo.findBestActiveDotsByCtspIds(ids, today);
        if (views == null || views.isEmpty()) return new ArrayList<>();

        Map<Integer, ChiTietDotGiamGiaRepository.BestDotGiamGiaView> bestByCtsp = new HashMap<>();
        for (ChiTietDotGiamGiaRepository.BestDotGiamGiaView v : views) {
            if (v == null || v.getCtspId() == null) continue;
            bestByCtsp.put(v.getCtspId(), v);
        }

        List<ChiTietDotGiamGiaResponse> out = new ArrayList<>();
        for (Integer ctspId : ids) {
            ChiTietDotGiamGiaRepository.BestDotGiamGiaView v = bestByCtsp.get(ctspId);
            if (v == null) continue;
            out.add(toPosResponse(v));
        }
        return out;
    }

    private ChiTietDotGiamGiaResponse toPosResponse(ChiTietDotGiamGiaRepository.BestDotGiamGiaView v) {
        BigDecimal pct = clampPct(v.getGiaTriGiam());
        BigDecimal maxTien = v.getSoTienGiamToiDa();
        if (maxTien != null && maxTien.signum() < 0) maxTien = BigDecimal.ZERO;

        return ChiTietDotGiamGiaResponse.builder()
                .id(null) // query POS không trả về id chi_tiet_dot_giam_gia
                .idChiTietSanPham(v.getCtspId())
                .idDotGiamGia(v.getIdDotGiamGia())

                .maDotGiamGia(v.getMaDotGiamGia())
                .tenDotGiamGia(v.getTenDotGiamGia())

                .giaTriGiamGiaDot(pct)
                .mucUuTienDot(v.getMucUuTien())
                .ngayBatDauDot(null)
                .ngayKetThucDot(null)

                .giaTriGiamRieng(null)
                .soTienGiamToiDaRieng(maxTien)

                .giaTriGiamApDung(pct)
                .soTienGiamToiDaApDung(maxTien)

                .trangThai(true)
                .xoaMem(false)
                .build();
    }

    private List<Integer> normalizeIds(List<Integer> ids) {
        if (ids == null) return new ArrayList<>();
        Set<Integer> set = new LinkedHashSet<>();
        for (Integer x : ids) {
            if (x == null) continue;
            if (x <= 0) continue;
            set.add(x);
        }
        return new ArrayList<>(set);
    }

    private BigDecimal clampPct(BigDecimal x) {
        if (x == null) return BigDecimal.ZERO;
        if (x.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        if (x.compareTo(new BigDecimal("100")) > 0) return new BigDecimal("100");
        return x;
    }

    // =========================
    // Existing validate/default
    // =========================

    private void applyDefaults(ChiTietDotGiamGia e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getGhiChu() == null) e.setGhiChu("");

        LocalDateTime now = LocalDateTime.now();
        if (createMode && e.getNgayTao() == null) e.setNgayTao(now);
        e.setNgayCapNhat(now);

        if (createMode && e.getNguoiTao() == null) e.setNguoiTao(1);
        if (e.getNguoiCapNhat() == null) e.setNguoiCapNhat(1);
    }

    private void validateRequired(ChiTietDotGiamGia e) {
        if (e.getIdDotGiamGia() == null) throw new BadRequestEx("Thiếu id_dot_giam_gia");
        if (e.getIdChiTietSanPham() == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham");

        if (e.getSoLuongApDung() != null && e.getSoLuongApDung() < 0)
            throw new BadRequestEx("so_luong_ap_dung không hợp lệ");

        if (e.getGiaTriGiamRieng() != null && e.getGiaTriGiamRieng().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestEx("gia_tri_giam_rieng phải >= 0");

        if (e.getSoTienGiamToiDaRieng() != null && e.getSoTienGiamToiDaRieng().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestEx("so_tien_giam_toi_da_rieng phải >= 0");
    }

    private void validateBusiness(ChiTietDotGiamGia e) {
        if (e.getGiaTriGiamRieng() != null) {
            if (e.getGiaTriGiamRieng().compareTo(BigDecimal.ZERO) < 0
                    || e.getGiaTriGiamRieng().compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestEx("Giá trị giảm riêng (%) phải trong 0..100");
            }
        }
    }

    private void validateDuplicateCreate(ChiTietDotGiamGia e) {
        boolean exists = repo.existsByIdDotGiamGiaAndIdChiTietSanPhamAndXoaMemFalse(
                e.getIdDotGiamGia(), e.getIdChiTietSanPham()
        );
        if (exists) throw new BadRequestEx("CTSP này đã được thêm vào đợt giảm giá.");
    }

    private void validateDuplicateUpdate(ChiTietDotGiamGia e) {
        boolean exists = repo.existsByIdDotGiamGiaAndIdChiTietSanPhamAndXoaMemFalseAndIdNot(
                e.getIdDotGiamGia(), e.getIdChiTietSanPham(), e.getId()
        );
        if (exists) throw new BadRequestEx("CTSP này đã được thêm vào đợt giảm giá.");
    }

    private ChiTietDotGiamGiaResponse toResponse(ChiTietDotGiamGia e) {
        return mapper.map(e, ChiTietDotGiamGiaResponse.class);
    }
}
