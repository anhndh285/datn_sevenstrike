package com.example.datn_sevenstrike.statistics.repository;

import com.example.datn_sevenstrike.entity.HoaDon;
import com.example.datn_sevenstrike.statistics.response.OrderStatusResponse;
import com.example.datn_sevenstrike.statistics.response.RevenueChartResponse;
import com.example.datn_sevenstrike.statistics.response.TopProductResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticRepository extends JpaRepository<HoaDon, Integer> {

    // -------- TỔNG SỐ ĐƠN --------
    @Query("""
        SELECT COUNT(h)
        FROM HoaDon h
        WHERE h.xoaMem = false
        AND (:fromDate IS NULL OR cast(h.ngayTao as date) >= :fromDate)
        AND (:toDate IS NULL OR cast(h.ngayTao as date) <= :toDate)
    """)
    Long countTotalOrders(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // -------- DOANH THU GỐC --------
    @Query("""
        SELECT COALESCE(SUM(h.tongTien), 0)
        FROM HoaDon h
        WHERE h.xoaMem = false
        AND (:fromDate IS NULL OR cast(h.ngayTao as date) >= :fromDate)
        AND (:toDate IS NULL OR cast(h.ngayTao as date) <= :toDate)
    """)
    Double totalRevenue(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // -------- DOANH THU SAU GIẢM --------
    @Query("""
        SELECT COALESCE(SUM(h.tongTienSauGiam), 0)
        FROM HoaDon h
        WHERE h.xoaMem = false
        AND (:fromDate IS NULL OR cast(h.ngayTao as date) >= :fromDate)
        AND (:toDate IS NULL OR cast(h.ngayTao as date) <= :toDate)
    """)
    Double realRevenue(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // -------- TOP SẢN PHẨM --------
    @Query("""
        SELECT new com.example.datn_sevenstrike.statistics.response.TopProductResponse(
            sp.tenSanPham,
            cts.giaBan,
            SUM(ct.soLuong)
        )
        FROM HoaDonChiTiet ct
        JOIN ct.chiTietSanPham cts
        JOIN cts.sanPham sp
        JOIN ct.hoaDon h
        WHERE ct.xoaMem = false
        AND (:fromDate IS NULL OR cast(h.ngayTao as date) >= :fromDate)
        AND (:toDate IS NULL OR cast(h.ngayTao as date) <= :toDate)
        GROUP BY sp.tenSanPham, cts.giaBan
        ORDER BY SUM(ct.soLuong) DESC
    """)
    List<TopProductResponse> topProducts(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // -------- THỐNG KÊ TRẠNG THÁI --------
    @Query("""
        SELECT new com.example.datn_sevenstrike.statistics.response.OrderStatusResponse(
            h.trangThaiHienTai, COUNT(h)
        )
        FROM HoaDon h
        WHERE h.xoaMem = false
        AND (:fromDate IS NULL OR cast(h.ngayTao as date) >= :fromDate)
        AND (:toDate IS NULL OR cast(h.ngayTao as date) <= :toDate)
        GROUP BY h.trangThaiHienTai
    """)
    List<OrderStatusResponse> orderStatus(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // -------- DOANH THU THEO NGÀY (CHART) --------
    @Query("""
        SELECT new com.example.datn_sevenstrike.statistics.response.RevenueChartResponse(
            cast(h.ngayTao as date),
            SUM(h.tongTienSauGiam)
        )
        FROM HoaDon h
        WHERE h.xoaMem = false
        AND (:fromDate IS NULL OR cast(h.ngayTao as date) >= :fromDate)
        AND (:toDate IS NULL OR cast(h.ngayTao as date) <= :toDate)
        GROUP BY cast(h.ngayTao as date)
        ORDER BY cast(h.ngayTao as date)
    """)
    List<RevenueChartResponse> revenueByDay(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // -------- DOANH THU THEO RANGE --------
    @Query("""
        SELECT COALESCE(SUM(h.tongTienSauGiam), 0)
        FROM HoaDon h
        WHERE h.xoaMem = false
        AND cast(h.ngayTao as date) >= :from
        AND cast(h.ngayTao as date) <= :to
    """)
    Double revenueByRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // -------- TỔNG ĐƠN THEO RANGE --------
    @Query("""
        SELECT COUNT(h)
        FROM HoaDon h
        WHERE h.xoaMem = false
        AND cast(h.ngayTao as date) >= :from
        AND cast(h.ngayTao as date) <= :to
    """)
    Long totalOrdersByRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
        SELECT COUNT(DISTINCT ct.chiTietSanPham.id)
        FROM HoaDonChiTiet ct
        JOIN ct.hoaDon h
        WHERE h.xoaMem = false
        AND cast(h.ngayTao as date) >= :from
        AND cast(h.ngayTao as date) <= :to
    """)
    Long countTotalProductsByRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // ✅ SỬA LỖI: loai_don tinyint (0/1/2) nên không so sánh true/false
    // 0 tại quầy | 1 giao hàng | 2 online
    @Query("""
        SELECT COALESCE(SUM(hd.tongTienSauGiam), 0)
        FROM HoaDon hd
        WHERE hd.ngayTao >= :fromDate
          AND hd.ngayTao < :toDate
          AND hd.xoaMem = false
          AND (
                hd.loaiDon = 1
                OR hd.ngayThanhToan IS NOT NULL
          )
    """)
    BigDecimal getExpectedRevenue(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("""
        SELECT COALESCE(SUM(h.tongTien), 0)
        FROM HoaDon h
        WHERE h.ngayTao BETWEEN :start AND :end
          AND h.trangThaiHienTai = 1
          AND h.xoaMem = false
    """)
    BigDecimal sumRevenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """

SELECT
    sp.ma_san_pham AS productCode,

    ctsp.ma_chi_tiet_san_pham AS productDetailCode,   -- ✅ MÃ CTSP

    sp.ten_san_pham AS productName,

    ms.ten_mau_sac AS color,

    kt.ten_kich_thuoc AS size,

    ls.ten_loai_san AS surface,

    ctsp.gia_ban AS price,

    COALESCE(ctsp.so_luong,0) AS importQuantity,      -- ✅ NHẬP

    COALESCE(sold.sold_quantity,0) AS soldQuantity,   -- ✅ BÁN

    CASE
        WHEN ctsp.so_luong = 0 THEN 0
        ELSE ROUND((COALESCE(sold.sold_quantity,0) * 100.0) / ctsp.so_luong,2)
    END AS sellRate

FROM chi_tiet_san_pham ctsp

JOIN san_pham sp
    ON ctsp.id_san_pham = sp.id

LEFT JOIN mau_sac ms
    ON ctsp.id_mau_sac = ms.id

LEFT JOIN kich_thuoc kt
    ON ctsp.id_kich_thuoc = kt.id

LEFT JOIN loai_san ls
    ON ctsp.id_loai_san = ls.id

LEFT JOIN (

    SELECT
        ct.id_chi_tiet_san_pham,
        SUM(ct.so_luong) AS sold_quantity

    FROM hoa_don_chi_tiet ct

    JOIN hoa_don hd
        ON hd.id = ct.id_hoa_don

    WHERE hd.xoa_mem = 0
      AND DATEPART(YEAR, hd.ngay_tao) = DATEPART(YEAR, GETDATE())
      AND DATEPART(QUARTER, hd.ngay_tao) = DATEPART(QUARTER, GETDATE())

    GROUP BY ct.id_chi_tiet_san_pham

) sold
    ON ctsp.id = sold.id_chi_tiet_san_pham

WHERE sp.xoa_mem = 0
  AND ctsp.xoa_mem = 0

ORDER BY sp.ma_san_pham

""", nativeQuery = true)
    List<Object[]> getProductInventoryQuarter();
}