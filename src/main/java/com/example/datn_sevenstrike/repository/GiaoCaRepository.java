package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.GiaoCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface GiaoCaRepository extends JpaRepository<GiaoCa, Integer> {

    // 1. Tìm ca đang hoạt động (Sử dụng trực tiếp id_nhan_vien)
    @Query("SELECT g FROM GiaoCa g WHERE g.nhanVien.id = :idNv AND g.trangThai = 0 AND g.xoaMem = false AND g.thoiGianKetCa IS NULL")
    Optional<GiaoCa> findCaDangHoatDong(@Param("idNv") Integer idNv);

    // 2. Tìm ca làm việc vừa kết thúc gần nhất
    @Query(value = "SELECT TOP 1 * FROM giao_ca WHERE trang_thai = 1 AND xoa_mem = 0 ORDER BY id DESC", nativeQuery = true)
    Optional<GiaoCa> findCaLamViecLienKeTruocDo();

    // 3. [TỐI ƯU] Tính doanh thu sử dụng id_giao_ca trực tiếp trên hóa đơn
    @Query(value = """
        SELECT COALESCE(SUM(h.tong_tien_sau_giam), 0) 
        FROM hoa_don h 
        WHERE h.id_giao_ca = :idGiaoCa 
          AND h.trang_thai_hien_tai = 1 
          AND h.xoa_mem = 0
    """, nativeQuery = true)
    BigDecimal tinhDoanhThuCa(@Param("idGiaoCa") Integer idGiaoCa);

    // 4. Số đơn chờ (Tính realtime toàn hệ thống hoặc theo logic của bạn)
    @Query(value = "SELECT COUNT(*) FROM hoa_don WHERE trang_thai_hien_tai IN (1, 2, 3, 4) AND xoa_mem = 0", nativeQuery = true)
    Integer countDonHangChoXuLy();
}