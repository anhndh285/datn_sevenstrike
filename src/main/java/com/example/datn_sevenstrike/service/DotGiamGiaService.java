package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.DotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.DotGiamGiaResponse;
import com.example.datn_sevenstrike.entity.DotGiamGia;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.DotGiamGiaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DotGiamGiaService {

    private final DotGiamGiaRepository repo;
    private final ModelMapper mapper;

    public List<DotGiamGiaResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public DotGiamGiaResponse one(Integer id) {
        DotGiamGia e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DotGiamGia id=" + id));
        return toResponse(e);
    }

    @Transactional
    public DotGiamGiaResponse create(DotGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        DotGiamGia e = mapper.map(req, DotGiamGia.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getLoaiGiamGia() == null) e.setLoaiGiamGia(false);
        if (e.getMucUuTien() == null) e.setMucUuTien(0);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public DotGiamGiaResponse update(Integer id, DotGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        DotGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DotGiamGia id=" + id));


        if (req.getTenDotGiamGia() != null) db.setTenDotGiamGia(req.getTenDotGiamGia());
        if (req.getLoaiGiamGia() != null) db.setLoaiGiamGia(req.getLoaiGiamGia());
        if (req.getGiaTriGiamGia() != null) db.setGiaTriGiamGia(req.getGiaTriGiamGia());
        if (req.getNgayBatDau() != null) db.setNgayBatDau(req.getNgayBatDau());
        if (req.getNgayKetThuc() != null) db.setNgayKetThuc(req.getNgayKetThuc());
        if (req.getMucUuTien() != null) db.setMucUuTien(req.getMucUuTien());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        DotGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DotGiamGia id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(DotGiamGia e) {
        if (e.getTenDotGiamGia() == null || e.getTenDotGiamGia().isBlank()) throw new BadRequestEx("Thiếu ten_dot_giam_gia");
        if (e.getNgayBatDau() == null || e.getNgayKetThuc() == null) throw new BadRequestEx("Thiếu ngày_bat_dau/ngày_ket_thuc");
        if (e.getNgayKetThuc().isBefore(e.getNgayBatDau())) throw new BadRequestEx("Ngày kết thúc phải >= ngày bắt đầu");
        if (Boolean.FALSE.equals(e.getLoaiGiamGia())) { if (e.getGiaTriGiamGia() == null || e.getGiaTriGiamGia().doubleValue() < 0 || e.getGiaTriGiamGia().doubleValue() > 100) throw new BadRequestEx("Giảm % phải nằm trong 0..100"); }
        if (e.getGiaTriGiamGia() == null) throw new BadRequestEx("Thiếu gia_tri_giam_gia");
        if (e.getGiaTriGiamGia().signum() < 0) throw new BadRequestEx("Giá trị giảm phải >= 0");
    }

    private DotGiamGiaResponse toResponse(DotGiamGia e) {
        return mapper.map(e, DotGiamGiaResponse.class);
    }
}
