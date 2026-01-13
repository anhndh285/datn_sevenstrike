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

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getNgayTao() == null) e.setNgayTao(LocalDateTime.now());


        validate(e);
        return toResponse(repo.save(e));
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
        if (req.getNguoiTao() != null) db.setNguoiTao(req.getNguoiTao());
        if (req.getNgayCapNhat() != null) db.setNgayCapNhat(req.getNgayCapNhat());
        if (req.getNguoiCapNhat() != null) db.setNguoiCapNhat(req.getNguoiCapNhat());
        db.setNgayCapNhat(LocalDateTime.now());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        ChiTietDotGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChiTietDotGiamGia id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void validate(ChiTietDotGiamGia e) {
        if (e.getIdDotGiamGia() == null) throw new BadRequestEx("Thiếu id_dot_giam_gia");
        if (e.getIdChiTietSanPham() == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham");
    }

    private ChiTietDotGiamGiaResponse toResponse(ChiTietDotGiamGia e) {
        return mapper.map(e, ChiTietDotGiamGiaResponse.class);
    }
}