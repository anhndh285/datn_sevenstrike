package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.HoaDon;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonThongKeRepository extends JpaRepository<HoaDon, Integer> {

    interface KhachHangTongQuanProjection {
        Integer getIdKhachHang();
        BigDecimal getTongChiTieu();
        Long getSoDonHang();
        LocalDateTime getDonHangGanNhat();
    }

    /**
     * ✅ 3 cột theo bảng hoa_don:
     * - Tổng chi tiêu: SUM(tong_tien_sau_giam)
     * - Số đơn: COUNT(*)
     * - Đơn gần nhất: MAX(ngay_tao)
     *
     * Điều kiện an toàn:
     * - xoa_mem = 0
     * - ngay_thanh_toan IS NOT NULL (coi là đã thanh toán)
     * - trang_thai_hien_tai <> 7 (loại giao thất bại)
     */
    @Query(value = """
        select
            id_khach_hang as idKhachHang,
            coalesce(sum(tong_tien_sau_giam), 0) as tongChiTieu,
            count(1) as soDonHang,
            max(ngay_tao) as donHangGanNhat
        from hoa_don
        where xoa_mem = 0
          and id_khach_hang = :khachHangId
          and ngay_thanh_toan is not null
          and trang_thai_hien_tai <> 7
        group by id_khach_hang
    """, nativeQuery = true)
    Optional<KhachHangTongQuanProjection> findTongQuanByKhachHangId(@Param("khachHangId") Integer khachHangId);
}
