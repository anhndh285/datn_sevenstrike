package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import com.example.datn_sevenstrike.entity.PhieuGiamGiaChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhieuGiamGiaChiTietRepository extends JpaRepository<PhieuGiamGiaChiTiet, Integer> {

    List<PhieuGiamGiaChiTiet> findAllByPhieuGiamGiaAndXoaMemFalse(PhieuGiamGia phieuGiamGia);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PhieuGiamGiaChiTiet ct
           set ct.xoaMem = true
         where ct.phieuGiamGia = :pgg
           and ct.xoaMem = false
    """)
    int deleteByPhieuGiamGia(@Param("pgg") PhieuGiamGia phieuGiamGia);
}
