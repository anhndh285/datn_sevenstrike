package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.DiaChiKhachHang;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
           and (:idKeep is null or d.id <> :idKeep)
    """)
    int unsetDefaultOthers(@Param("idKhachHang") Integer idKhachHang,
                           @Param("idKeep") Integer idKeep);
}