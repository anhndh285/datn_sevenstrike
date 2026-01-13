package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    java.util.Optional<SanPham> findByIdAndXoaMemFalse(Integer id);
    java.util.List<SanPham> findAllByXoaMemFalseOrderByIdDesc();

}
