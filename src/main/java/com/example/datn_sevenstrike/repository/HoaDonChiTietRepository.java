package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    java.util.Optional<HoaDonChiTiet> findByIdAndXoaMemFalse(Integer id);
    java.util.List<HoaDonChiTiet> findAllByXoaMemFalseOrderByIdDesc();

}
