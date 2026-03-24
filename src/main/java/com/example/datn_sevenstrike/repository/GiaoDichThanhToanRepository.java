// File: src/main/java/com/example/datn_sevenstrike/repository/GiaoDichThanhToanRepository.java
package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.GiaoDichThanhToan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GiaoDichThanhToanRepository extends JpaRepository<GiaoDichThanhToan, Integer> {

    List<GiaoDichThanhToan> findAllByXoaMemFalseOrderByIdDesc();

    Optional<GiaoDichThanhToan> findByIdAndXoaMemFalse(Integer id);

    List<GiaoDichThanhToan> findAllByIdHoaDonAndXoaMemFalseOrderByThoiGianTaoDesc(Integer idHoaDon);

    List<GiaoDichThanhToan> findAllByIdHoaDon(Integer hoaDonId);

    // Xóa mềm toàn bộ giao dịch theo hóa đơn (nếu cần dùng)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update dbo.giao_dich_thanh_toan
           set xoa_mem = 1,
               thoi_gian_cap_nhat = sysdatetime()
         where id_hoa_don = :idHoaDon
           and xoa_mem = 0
    """, nativeQuery = true)
    int softDeleteByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    // Reset hóa đơn chờ / xóa cứng toàn bộ giao dịch theo hóa đơn
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        delete from dbo.giao_dich_thanh_toan
         where id_hoa_don = :idHoaDon
    """, nativeQuery = true)
    int deleteHardByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    // Lấy lịch sử thanh toán tiền mặt không thuộc giao ca
    @Query(value = """
        select gd.*
        from dbo.giao_dich_thanh_toan gd
        inner join dbo.hoa_don hd
            on hd.id = gd.id_hoa_don
           and hd.xoa_mem = 0
        inner join dbo.phuong_thuc_thanh_toan pt
            on pt.id = gd.id_phuong_thuc_thanh_toan
           and pt.xoa_mem = 0
        where gd.xoa_mem = 0
          and lower(ltrim(rtrim(gd.trang_thai))) = 'thanh_cong'
          and pt.ten_phuong_thuc_thanh_toan = N'Tiền mặt'
          and hd.id_giao_ca is null
          and gd.nguoi_cap_nhat is not null
        order by gd.thoi_gian_tao desc, gd.id desc
    """, nativeQuery = true)
    List<GiaoDichThanhToan> findAdminCashHistory();
}