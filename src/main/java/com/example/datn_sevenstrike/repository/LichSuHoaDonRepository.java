// File: src/main/java/com/example/datn_sevenstrike/repository/LichSuHoaDonRepository.java
package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LichSuHoaDon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {

    List<LichSuHoaDon> findAllByXoaMemFalseOrderByIdDesc();

    Optional<LichSuHoaDon> findByIdAndXoaMemFalse(Integer id);

    List<LichSuHoaDon> findAllByIdHoaDonAndXoaMemFalseOrderByThoiGianAsc(Integer idHoaDon);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update dbo.lich_su_hoa_don
           set xoa_mem = 1
         where id_hoa_don = :idHoaDon
           and xoa_mem = 0
    """, nativeQuery = true)
    int softDeleteByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        delete from dbo.lich_su_hoa_don
         where id_hoa_don = :idHoaDon
    """, nativeQuery = true)
    int deleteHardByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);
}