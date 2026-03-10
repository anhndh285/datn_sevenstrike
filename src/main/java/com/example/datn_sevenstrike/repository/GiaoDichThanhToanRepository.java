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
}