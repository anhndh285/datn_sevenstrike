package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.entity.HoaDon;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.HoaDonRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HoaDonService {

    private final HoaDonRepository repo;
    private final ModelMapper mapper;

    public List<HoaDonResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public HoaDonResponse one(Integer id) {
        HoaDon e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + id));
        return toResponse(e);
    }

    @Transactional
    public HoaDonResponse create(HoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        HoaDon e = mapper.map(req, HoaDon.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getLoaiDon() == null) e.setLoaiDon(false);
        if (e.getPhiVanChuyen() == null) e.setPhiVanChuyen(BigDecimal.ZERO);
        if (e.getNgayTao() == null) e.setNgayTao(LocalDateTime.now());


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public HoaDonResponse update(Integer id, HoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        HoaDon db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + id));


        if (req.getIdKhachHang() != null) db.setIdKhachHang(req.getIdKhachHang());
        if (req.getIdNhanVien() != null) db.setIdNhanVien(req.getIdNhanVien());
        if (req.getIdPhieuGiamGia() != null) db.setIdPhieuGiamGia(req.getIdPhieuGiamGia());
        if (req.getIdPhieuGiamGiaCaNhan() != null) db.setIdPhieuGiamGiaCaNhan(req.getIdPhieuGiamGiaCaNhan());
        if (req.getLoaiDon() != null) db.setLoaiDon(req.getLoaiDon());
        if (req.getPhiVanChuyen() != null) db.setPhiVanChuyen(req.getPhiVanChuyen());
        if (req.getTongTien() != null) db.setTongTien(req.getTongTien());
        if (req.getTongTienSauGiam() != null) db.setTongTienSauGiam(req.getTongTienSauGiam());
        if (req.getTenKhachHang() != null) db.setTenKhachHang(req.getTenKhachHang());
        if (req.getDiaChiKhachHang() != null) db.setDiaChiKhachHang(req.getDiaChiKhachHang());
        if (req.getSoDienThoaiKhachHang() != null) db.setSoDienThoaiKhachHang(req.getSoDienThoaiKhachHang());
        if (req.getEmailKhachHang() != null) db.setEmailKhachHang(req.getEmailKhachHang());
        if (req.getTrangThaiHienTai() != null) db.setTrangThaiHienTai(req.getTrangThaiHienTai());
        if (req.getNgayThanhToan() != null) db.setNgayThanhToan(req.getNgayThanhToan());
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
        HoaDon db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void validate(HoaDon e) {
        if (e.getTenKhachHang() == null || e.getTenKhachHang().isBlank()) throw new BadRequestEx("Thiếu ten_khach_hang");
        if (e.getDiaChiKhachHang() == null || e.getDiaChiKhachHang().isBlank()) throw new BadRequestEx("Thiếu dia_chi_khach_hang");
        if (e.getSoDienThoaiKhachHang() == null || e.getSoDienThoaiKhachHang().isBlank()) throw new BadRequestEx("Thiếu so_dien_thoai_khach_hang");
        if (e.getTongTien() == null || e.getTongTienSauGiam() == null) throw new BadRequestEx("Thiếu tong_tien/tong_tien_sau_giam");
        if (e.getTongTienSauGiam().compareTo(e.getTongTien()) > 0) throw new BadRequestEx("tong_tien_sau_giam phải <= tong_tien");
    }

    private HoaDonResponse toResponse(HoaDon e) {
        return mapper.map(e, HoaDonResponse.class);
    }
}
