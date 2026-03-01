// File: src/main/java/com/example/datn_sevenstrike/repository/HoaDonChiTietRepository.java
package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.HoaDonChiTiet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    List<HoaDonChiTiet> findAllByXoaMemFalseOrderByIdDesc();

    Optional<HoaDonChiTiet> findByIdAndXoaMemFalse(Integer id);

    List<HoaDonChiTiet> findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(Integer idHoaDon);

    // (Tuỳ nhu cầu) Lấy tất cả chi tiết theo hóa đơn, gồm cả đã xóa mềm (ít dùng, tránh dùng nhầm)
    List<HoaDonChiTiet> findAllByIdHoaDonOrderByIdAsc(Integer idHoaDon);

    // Lấy chi tiết theo hóa đơn (không filter xóa mềm) - theo file Duy
    List<HoaDonChiTiet> findByIdHoaDon(Integer idHoaDon);

    // Lấy chi tiết theo hóa đơn + join sản phẩm (để load kèm thông tin sản phẩm) - theo file Duy
    @Query(value = """
        select hdct.*
          from dbo.hoa_don_chi_tiet hdct
          join dbo.chi_tiet_san_pham ctsp
            on ctsp.id = hdct.id_chi_tiet_san_pham
           and ctsp.xoa_mem = 0
          join dbo.san_pham sp
            on sp.id = ctsp.id_san_pham
           and sp.xoa_mem = 0
         where hdct.id_hoa_don = :idHoaDon
           and hdct.xoa_mem = 0
         order by hdct.id asc
        """, nativeQuery = true)
    List<HoaDonChiTiet> findAllWithProductByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    // Trả về idSanPham bán chạy nhất trong tháng hiện tại (đơn hoàn thành - trạng thái 5) - theo file Duy
    @Query(value = """
        select ctsp.id_san_pham
          from dbo.hoa_don_chi_tiet hdct
          join dbo.hoa_don hd on hd.id = hdct.id_hoa_don
          join dbo.chi_tiet_san_pham ctsp on ctsp.id = hdct.id_chi_tiet_san_pham
         where hd.trang_thai_hien_tai = 5
           and hd.xoa_mem = 0
           and hdct.xoa_mem = 0
           and ctsp.xoa_mem = 0
           and hd.ngay_tao >= :thangNay
         group by ctsp.id_san_pham
         order by sum(hdct.so_luong) desc
        """, nativeQuery = true)
    List<Integer> findBestSellingProductIds(@Param("thangNay") LocalDateTime thangNay, Pageable pageable);

    // Xóa mềm toàn bộ chi tiết theo hóa đơn
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update dbo.hoa_don_chi_tiet
           set xoa_mem = 1
         where id_hoa_don = :idHoaDon
           and xoa_mem = 0
        """, nativeQuery = true)
    int softDeleteByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    // Reset hóa đơn chờ / xóa cứng toàn bộ chi tiết theo hóa đơn
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        delete from dbo.hoa_don_chi_tiet
         where id_hoa_don = :idHoaDon
        """, nativeQuery = true)
    int deleteHardByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);
}