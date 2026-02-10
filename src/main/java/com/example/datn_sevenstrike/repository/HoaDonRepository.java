package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    List<HoaDon> findAllByXoaMemFalseOrderByIdDesc();

    Page<HoaDon> findAllByXoaMemFalse(Pageable pageable);

    Optional<HoaDon> findByIdAndXoaMemFalse(Integer id);
    @Query("""
        SELECT new com.example.datn_sevenstrike.dto.response.HoaDonResponse(
            hd.id,
            hd.idKhachHang,
            hd.idNhanVien,
            nv.tenNhanVien,
            hd.idPhieuGiamGia,
            hd.idPhieuGiamGiaCaNhan,
            hd.maHoaDon,
            hd.loaiDon,
            hd.phiVanChuyen,
            hd.tongTien,
            hd.tongTienSauGiam,
            hd.tongTienGiam,
            hd.tenKhachHang,
            hd.diaChiKhachHang,
            hd.soDienThoaiKhachHang,
            hd.emailKhachHang,
            hd.trangThaiHienTai,
            null,
            hd.ngayTao,
            hd.ngayThanhToan,
            hd.ghiChu,
            hd.xoaMem,
            hd.nguoiTao,
            hd.ngayCapNhat,
            hd.nguoiCapNhat
        )
        FROM HoaDon hd
        LEFT JOIN NhanVien nv ON hd.idNhanVien = nv.id
        WHERE hd.xoaMem = false
        ORDER BY hd.id DESC
    """)
    List<HoaDonResponse> getDanhSachHoaDon();
}
