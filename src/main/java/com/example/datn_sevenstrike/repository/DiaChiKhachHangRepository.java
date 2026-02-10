package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.DiaChiKhachHang;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang, Integer> {

    List<DiaChiKhachHang> findAllByXoaMemFalseOrderByIdDesc();

    Optional<DiaChiKhachHang> findByIdAndXoaMemFalse(Integer id);

    List<DiaChiKhachHang> findAllByIdKhachHangAndXoaMemFalseOrderByMacDinhDescIdDesc(Integer idKhachHang);

    Optional<DiaChiKhachHang> findFirstByIdKhachHangAndMacDinhTrueAndXoaMemFalse(Integer idKhachHang);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update DiaChiKhachHang d
           set d.macDinh = false
         where d.idKhachHang = :idKhachHang
           and d.xoaMem = false
           and d.macDinh = true
           and d.id <> :idKeep
    """)
    int unsetDefaultOthers(@Param("idKhachHang") Integer idKhachHang, @Param("idKeep") Integer idKeep);
}
