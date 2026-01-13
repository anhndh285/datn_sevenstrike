package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ChiTietSanPhamRequest;
import com.example.datn_sevenstrike.dto.response.ChiTietSanPhamResponse;
import com.example.datn_sevenstrike.entity.ChiTietSanPham;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChiTietSanPhamService {

    private final ChiTietSanPhamRepository repo;
    private final ModelMapper mapper;

    public List<ChiTietSanPhamResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public ChiTietSanPhamResponse one(Integer id) {
        ChiTietSanPham e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChiTietSanPham id=" + id));
        return toResponse(e);
    }

    @Transactional
    public ChiTietSanPhamResponse create(ChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        ChiTietSanPham e = mapper.map(req, ChiTietSanPham.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getSoLuong() == null) e.setSoLuong(0);
        if (e.getNgayTao() == null) e.setNgayTao(LocalDateTime.now());


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public ChiTietSanPhamResponse update(Integer id, ChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        ChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChiTietSanPham id=" + id));


        if (req.getIdSanPham() != null) db.setIdSanPham(req.getIdSanPham());
        if (req.getIdMauSac() != null) db.setIdMauSac(req.getIdMauSac());
        if (req.getIdKichThuoc() != null) db.setIdKichThuoc(req.getIdKichThuoc());
        if (req.getIdLoaiSan() != null) db.setIdLoaiSan(req.getIdLoaiSan());
        if (req.getIdFormChan() != null) db.setIdFormChan(req.getIdFormChan());
        if (req.getSoLuong() != null) db.setSoLuong(req.getSoLuong());
        if (req.getGiaNiemYet() != null) db.setGiaNiemYet(req.getGiaNiemYet());
        if (req.getGiaBan() != null) db.setGiaBan(req.getGiaBan());
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
        ChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChiTietSanPham id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void validate(ChiTietSanPham e) {
        if (e.getIdSanPham() == null) throw new BadRequestEx("Thiếu id_san_pham");
        if (e.getIdMauSac() == null) throw new BadRequestEx("Thiếu id_mau_sac");
        if (e.getIdKichThuoc() == null) throw new BadRequestEx("Thiếu id_kich_thuoc");
        if (e.getIdLoaiSan() == null) throw new BadRequestEx("Thiếu id_loai_san");
        if (e.getIdFormChan() == null) throw new BadRequestEx("Thiếu id_form_chan");
    }

    private ChiTietSanPhamResponse toResponse(ChiTietSanPham e) {
        return mapper.map(e, ChiTietSanPhamResponse.class);
    }
}

