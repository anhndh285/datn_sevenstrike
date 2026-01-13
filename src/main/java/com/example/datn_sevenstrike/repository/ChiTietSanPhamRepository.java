package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    java.util.Optional<ChiTietSanPham> findByIdAndXoaMemFalse(Integer id);
    java.util.List<ChiTietSanPham> findAllByXoaMemFalseOrderByIdDesc();

}