package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.SanPhamRequest;
import com.example.datn_sevenstrike.dto.response.SanPhamResponse;
import com.example.datn_sevenstrike.entity.SanPham;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.SanPhamRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository repo;
    private final ModelMapper mapper;

    public List<SanPhamResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public Page<SanPhamResponse> page(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);
        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));
        return repo.findAllByXoaMemFalse(pageable).map(this::toResponse);
    }

    public List<SanPhamResponse> allKinhDoanh() {
        return repo.findAllByXoaMemFalseAndTrangThaiKinhDoanhTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public Page<SanPhamResponse> pageKinhDoanh(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);
        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));
        return repo.findAllByXoaMemFalseAndTrangThaiKinhDoanhTrue(pageable).map(this::toResponse);
    }

    public SanPhamResponse one(Integer id) {
        SanPham e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy SanPham id=" + id));
        return toResponse(e);
    }

    @Transactional
    public SanPhamResponse create(SanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        SanPham e = mapper.map(req, SanPham.class);
        e.setId(null);

        applyDefaults(e, true);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public SanPhamResponse update(Integer id, SanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        SanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy SanPham id=" + id));

        if (req.getIdThuongHieu() != null) db.setIdThuongHieu(req.getIdThuongHieu());
        if (req.getIdXuatXu() != null) db.setIdXuatXu(req.getIdXuatXu());
        if (req.getIdViTriThiDau() != null) db.setIdViTriThiDau(req.getIdViTriThiDau());
        if (req.getIdPhongCachChoi() != null) db.setIdPhongCachChoi(req.getIdPhongCachChoi());
        if (req.getIdCoGiay() != null) db.setIdCoGiay(req.getIdCoGiay());
        if (req.getIdChatLieu() != null) db.setIdChatLieu(req.getIdChatLieu());
        if (req.getTenSanPham() != null) db.setTenSanPham(req.getTenSanPham());
        if (req.getMoTaNgan() != null) db.setMoTaNgan(req.getMoTaNgan());
        if (req.getMoTaChiTiet() != null) db.setMoTaChiTiet(req.getMoTaChiTiet());
        if (req.getTrangThaiKinhDoanh() != null) db.setTrangThaiKinhDoanh(req.getTrangThaiKinhDoanh());
        if (req.getNguoiCapNhat() != null) db.setNguoiCapNhat(req.getNguoiCapNhat());

        applyDefaults(db, false);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        SanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy SanPham id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void applyDefaults(SanPham e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThaiKinhDoanh() == null) e.setTrangThaiKinhDoanh(true);

        if (e.getTenSanPham() != null) e.setTenSanPham(e.getTenSanPham().trim());
        if (e.getMoTaNgan() != null) e.setMoTaNgan(e.getMoTaNgan().trim());
        if (e.getMoTaChiTiet() != null) e.setMoTaChiTiet(e.getMoTaChiTiet().trim());

        LocalDateTime now = LocalDateTime.now();
        if (createMode && e.getNgayTao() == null) e.setNgayTao(now);
        e.setNgayCapNhat(now);
    }

    private void validate(SanPham e) {
        if (e.getIdThuongHieu() == null) throw new BadRequestEx("Thiếu id_thuong_hieu");
        if (e.getTenSanPham() == null || e.getTenSanPham().isBlank()) throw new BadRequestEx("Thiếu ten_san_pham");
    }

    private SanPhamResponse toResponse(SanPham e) {
        return mapper.map(e, SanPhamResponse.class);
    }
}
