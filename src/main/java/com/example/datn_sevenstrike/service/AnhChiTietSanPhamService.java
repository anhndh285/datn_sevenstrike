package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.AnhChiTietSanPhamRequest;
import com.example.datn_sevenstrike.dto.response.AnhChiTietSanPhamResponse;
import com.example.datn_sevenstrike.entity.AnhChiTietSanPham;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.AnhChiTietSanPhamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnhChiTietSanPhamService {

    private final AnhChiTietSanPhamRepository repo;
    private final ModelMapper mapper;

    public List<AnhChiTietSanPhamResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public AnhChiTietSanPhamResponse one(Integer id) {
        AnhChiTietSanPham e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy AnhChiTietSanPham id=" + id));
        return toResponse(e);
    }

    @Transactional
    public AnhChiTietSanPhamResponse create(AnhChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        AnhChiTietSanPham e = mapper.map(req, AnhChiTietSanPham.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getLaAnhDaiDien() == null) e.setLaAnhDaiDien(false);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public AnhChiTietSanPhamResponse update(Integer id, AnhChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        AnhChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy AnhChiTietSanPham id=" + id));


        if (req.getIdChiTietSanPham() != null) db.setIdChiTietSanPham(req.getIdChiTietSanPham());
        if (req.getDuongDanAnh() != null) db.setDuongDanAnh(req.getDuongDanAnh());
        if (req.getLaAnhDaiDien() != null) db.setLaAnhDaiDien(req.getLaAnhDaiDien());
        if (req.getMoTa() != null) db.setMoTa(req.getMoTa());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        AnhChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy AnhChiTietSanPham id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(AnhChiTietSanPham e) {
        if (e.getIdChiTietSanPham() == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham");
        if (e.getDuongDanAnh() == null || e.getDuongDanAnh().isBlank()) throw new BadRequestEx("Thiếu duong_dan_anh");
    }

    private AnhChiTietSanPhamResponse toResponse(AnhChiTietSanPham e) {
        return mapper.map(e, AnhChiTietSanPhamResponse.class);
    }
}
