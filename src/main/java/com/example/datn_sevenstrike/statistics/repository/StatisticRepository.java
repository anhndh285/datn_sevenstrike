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
        AND h.trangThaiHienTai = 5
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
        AND h.trangThaiHienTai = 5
        AND (:fromDate IS NULL OR cast(h.ngayTao as date) >= :fromDate)
        AND (:toDate IS NULL OR cast(h.ngayTao as date) <= :toDate)
    """)
    Double realRevenue(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // -------- TOP SẢN PHẨM (BẢN FIX LỖI CÚ PHÁP) --------
    @Query("""
    SELECT new com.example.datn_sevenstrike.statistics.response.TopProductResponse(
        cts.maChiTietSanPham,
        sp.tenSanPham,
        cts.giaBan,
        SUM(ct.soLuong),
        ms.tenMauSac,
        kt.tenKichThuoc,
        ls.tenLoaiSan,
        a.duongDanAnh,
        cts.soLuong
    )
    FROM HoaDonChiTiet ct
    JOIN ct.chiTietSanPham cts
    JOIN cts.sanPham sp
    LEFT JOIN cts.mauSac ms
    LEFT JOIN cts.kichThuoc kt
    LEFT JOIN cts.loaiSan ls
    LEFT JOIN AnhChiTietSanPham a ON a.chiTietSanPham.id = cts.id 
         AND a.laAnhDaiDien = true 
         AND a.xoaMem = false
    WHERE ct.xoaMem = false
      AND ct.hoaDon.xoaMem = false
      AND (:fromDate IS NULL OR cast(ct.hoaDon.ngayTao as date) >= :fromDate)
      AND (:toDate IS NULL OR cast(ct.hoaDon.ngayTao as date) <= :toDate)
    GROUP BY 
        cts.maChiTietSanPham, 
        sp.tenSanPham, 
        cts.giaBan, 
        ms.tenMauSac, 
        kt.tenKichThuoc, 
        ls.tenLoaiSan, 
        a.duongDanAnh, 
        cts.soLuong
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
        AND h.trangThaiHienTai = 5
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
        AND h.trangThaiHienTai = 5
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

    @Query("""
        SELECT COALESCE(SUM(hd.tongTienSauGiam), 0)
        FROM HoaDon hd
        WHERE hd.ngayTao >= :fromDate
          AND hd.ngayTao < :toDate
          AND hd.xoaMem = false
          AND hd.trangThaiHienTai = 5
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


    //Thong ke ton kho
    @Query(value = """
SELECT
    sp.ma_san_pham AS productCode,
    ctsp.ma_chi_tiet_san_pham AS productDetailCode,
    sp.ten_san_pham AS productName,
    ms.ten_mau_sac AS color,
    kt.ten_kich_thuoc AS size,
    ls.ten_loai_san AS surface,
    ctsp.gia_ban AS price,
    COALESCE(sold.sold_quantity,0) + COALESCE(ctsp.so_luong,0) AS importQuantity,
    COALESCE(sold.sold_quantity,0) AS soldQuantity,
    COALESCE(ctsp.so_luong,0) AS stockQuantity,
    CASE
        WHEN (COALESCE(sold.sold_quantity,0) + COALESCE(ctsp.so_luong,0)) = 0
        THEN 0
        ELSE ROUND(
            (COALESCE(sold.sold_quantity,0) * 100.0) /
            (COALESCE(sold.sold_quantity,0) + COALESCE(ctsp.so_luong,0))
        ,2)
    END AS sellRate,
    -- Thêm cột lấy ảnh đại diện (Index 11)
    (SELECT TOP 1 img.duong_dan_anh 
     FROM anh_chi_tiet_san_pham img 
     WHERE img.id_chi_tiet_san_pham = ctsp.id 
     AND img.la_anh_dai_dien = 1 
     AND img.xoa_mem = 0) AS imageUrl
FROM chi_tiet_san_pham ctsp
JOIN san_pham sp ON ctsp.id_san_pham = sp.id
LEFT JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id
LEFT JOIN kich_thuoc kt ON ctsp.id_kich_thuoc = kt.id
LEFT JOIN loai_san ls ON ctsp.id_loai_san = ls.id
LEFT JOIN (
    SELECT
        ct.id_chi_tiet_san_pham,
        SUM(ct.so_luong) AS sold_quantity
    FROM hoa_don_chi_tiet ct
    JOIN hoa_don hd ON hd.id = ct.id_hoa_don
    WHERE hd.xoa_mem = 0
    GROUP BY ct.id_chi_tiet_san_pham
) sold ON ctsp.id = sold.id_chi_tiet_san_pham
WHERE sp.xoa_mem = 0 AND ctsp.xoa_mem = 0
ORDER BY sp.ma_san_pham
""", nativeQuery = true)
    List<Object[]> getProductInventoryQuarter();
}