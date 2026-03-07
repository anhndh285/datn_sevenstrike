package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.GiaoCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface GiaoCaRepository extends JpaRepository<GiaoCa, Integer> {

    @Query("SELECT g FROM GiaoCa g WHERE g.nhanVien.id = :idNv AND g.trangThai = 0 AND g.xoaMem = false AND g.thoiGianKetCa IS NULL")
    Optional<GiaoCa> findCaDangHoatDong(@Param("idNv") Integer idNv);

    @Query(value = "SELECT TOP 1 * FROM giao_ca WHERE trang_thai = 1 AND xoa_mem = 0 ORDER BY id DESC", nativeQuery = true)
    Optional<GiaoCa> findCaLamViecLienKeTruocDo();

    // 1. DOANH THU TỔNG
    @Query(value = """
        SELECT COALESCE(SUM(h.tong_tien_sau_giam), 0) 
        FROM hoa_don h 
        WHERE h.id_giao_ca = :idGiaoCa 
          AND h.trang_thai_hien_tai = 5 
          AND h.xoa_mem = 0
    """, nativeQuery = true)
    BigDecimal tinhDoanhThuCa(@Param("idGiaoCa") Integer idGiaoCa);

    // 2. TỔNG TIỀN MẶT
    @Query(value = """
        SELECT COALESCE(SUM(gd.so_tien), 0) 
        FROM giao_dich_thanh_toan gd
        JOIN hoa_don h ON gd.id_hoa_don = h.id
        JOIN phuong_thuc_thanh_toan p ON gd.id_phuong_thuc_thanh_toan = p.id
        WHERE h.id_giao_ca = :idGiaoCa 
          AND h.trang_thai_hien_tai = 5 
          AND h.xoa_mem = 0
          AND (p.ten_phuong_thuc_thanh_toan LIKE N'%Tiền mặt%' OR p.ten_phuong_thuc_thanh_toan LIKE '%Tien mat%')
    """, nativeQuery = true)
    BigDecimal tinhDoanhThuTienMatCa(@Param("idGiaoCa") Integer idGiaoCa);

    // 3. TỔNG TIỀN CHUYỂN KHOẢN
    @Query(value = """
        SELECT COALESCE(SUM(gd.so_tien), 0) 
        FROM giao_dich_thanh_toan gd
        JOIN hoa_don h ON gd.id_hoa_don = h.id
        JOIN phuong_thuc_thanh_toan p ON gd.id_phuong_thuc_thanh_toan = p.id
        WHERE h.id_giao_ca = :idGiaoCa 
          AND h.trang_thai_hien_tai = 5 
          AND h.xoa_mem = 0
          AND (p.ten_phuong_thuc_thanh_toan LIKE N'%Chuyển khoản%' OR p.ten_phuong_thuc_thanh_toan LIKE '%Chuyen khoan%')
    """, nativeQuery = true)
    BigDecimal tinhDoanhThuChuyenKhoanCa(@Param("idGiaoCa") Integer idGiaoCa);

    @Query(value = "SELECT COUNT(*) FROM hoa_don WHERE trang_thai_hien_tai IN (1, 2, 3, 4) AND xoa_mem = 0", nativeQuery = true)
    Integer countDonHangChoXuLy();
}