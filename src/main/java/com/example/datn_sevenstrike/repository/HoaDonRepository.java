// File: src/main/java/com/example/datn_sevenstrike/repository/HoaDonRepository.java
package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.entity.HoaDon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    List<HoaDon> findAllByXoaMemFalseOrderByIdDesc();

    Page<HoaDon> findAllByXoaMemFalse(Pageable pageable);

    Optional<HoaDon> findByIdAndXoaMemFalse(Integer id);

    // ✅ Theo file Duy: tìm theo mã hóa đơn
    Optional<HoaDon> findByMaHoaDonAndXoaMemFalse(String maHoaDon);

    // ✅ Theo file Duy: lấy danh sách hóa đơn theo khách hàng
    List<HoaDon> findAllByIdKhachHangAndXoaMemFalseOrderByIdDesc(Integer customerId);

    // ✅ PAGE cho màn quản lý hóa đơn: ẨN hóa đơn tại quầy đang chờ (loai_don=0 & trang_thai=1)
    @Query("""
        select hd
          from HoaDon hd
         where hd.xoaMem = false
           and not (hd.loaiDon = 0 and hd.trangThaiHienTai = 1)
    """)
    Page<HoaDon> pageQuanLyHoaDon(Pageable pageable);

    // ✅ PAGE Response (có tenNhanVien) cho màn quản lý hóa đơn
    @Query(
            value = """
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
                  and not (hd.loaiDon = 0 and hd.trangThaiHienTai = 1)
            """,
            countQuery = """
                select count(hd)
                  from HoaDon hd
                 where hd.xoaMem = false
                   and not (hd.loaiDon = 0 and hd.trangThaiHienTai = 1)
            """
    )
    Page<HoaDonResponse> pageQuanLyHoaDonResponse(Pageable pageable);

    // ✅ LIST cho màn quản lý hóa đơn: ẨN hóa đơn tại quầy đang chờ (loai_don=0 & trang_thai=1)
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
          and not (hd.loaiDon = 0 and hd.trangThaiHienTai = 1)
        ORDER BY hd.id DESC
    """)
    List<HoaDonResponse> getDanhSachHoaDon();

    // ✅ Dùng cho one(): KHÔNG ẩn hóa đơn chờ tại quầy (POS vẫn xem được)
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
          and hd.id = :id
    """)
    Optional<HoaDonResponse> getHoaDonResponseById(@Param("id") Integer id);

    // ✅ Reset 0h: lấy đúng ID hóa đơn tại quầy đang chờ và cũ ngày
    @Query("""
        select hd.id
          from HoaDon hd
         where hd.xoaMem = false
           and hd.loaiDon = :loaiDon
           and hd.trangThaiHienTai = :trangThai
           and hd.ngayTao < :cutoff
         order by hd.id desc
    """)
    List<Integer> findIdsChoTaiQuayCanReset(
            @Param("loaiDon") Integer loaiDon,
            @Param("trangThai") Integer trangThai,
            @Param("cutoff") LocalDateTime cutoff
    );

    // ✅ Reset = xóa cứng hóa đơn (service đã xóa gdtt/hdct/lshd trước)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        delete from dbo.hoa_don
         where id = :idHoaDon
    """, nativeQuery = true)
    int deleteHardById(@Param("idHoaDon") Integer idHoaDon);
}