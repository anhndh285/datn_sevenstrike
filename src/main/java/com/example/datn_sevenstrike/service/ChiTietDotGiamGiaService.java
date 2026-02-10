package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ChiTietDotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.ChiTietDotGiamGiaResponse;
import com.example.datn_sevenstrike.entity.ChiTietDotGiamGia;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietDotGiamGiaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    private void applyDefaults(ChiTietDotGiamGia e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getGhiChu() == null) e.setGhiChu("");

        LocalDateTime now = LocalDateTime.now();
        if (createMode && e.getNgayTao() == null) e.setNgayTao(now);
        e.setNgayCapNhat(now);

        // để demo an toàn nếu entity bạn đang để NOT NULL
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

    /**
     * Nghiệp vụ đơn giản để demo:
     * - Nếu có giaTriGiamRieng (%): nên 0..100 (vì bạn chốt đợt %)
     * - soTienGiamToiDaRieng có thể null hoặc >=0
     */
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
