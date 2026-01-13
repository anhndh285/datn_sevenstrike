package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.AnhChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnhChiTietSanPhamRepository extends JpaRepository<AnhChiTietSanPham, Integer> {

    java.util.Optional<AnhChiTietSanPham> findByIdAndXoaMemFalse(Integer id);
    java.util.List<AnhChiTietSanPham> findAllByXoaMemFalseOrderByIdDesc();

}