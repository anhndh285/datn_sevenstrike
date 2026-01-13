package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.DiaChiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang, Integer> {

    java.util.Optional<DiaChiKhachHang> findByIdAndXoaMemFalse(Integer id);
    java.util.List<DiaChiKhachHang> findAllByXoaMemFalseOrderByIdDesc();

}
