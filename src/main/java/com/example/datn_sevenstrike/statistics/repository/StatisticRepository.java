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
}