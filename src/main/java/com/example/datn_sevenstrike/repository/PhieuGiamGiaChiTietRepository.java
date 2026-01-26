package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import com.example.datn_sevenstrike.entity.PhieuGiamGiaChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuGiamGiaChiTietRepository extends JpaRepository<PhieuGiamGiaChiTiet, Integer> {
    void deleteByPhieuGiamGia(PhieuGiamGia pgg);
    List<PhieuGiamGiaChiTiet> findAllByPhieuGiamGia(PhieuGiamGia pgg);
}