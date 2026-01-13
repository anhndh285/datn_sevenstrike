package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ChiTietDotGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietDotGiamGiaRepository extends JpaRepository<ChiTietDotGiamGia, Integer> {

    java.util.Optional<ChiTietDotGiamGia> findByIdAndXoaMemFalse(Integer id);
    java.util.List<ChiTietDotGiamGia> findAllByXoaMemFalseOrderByIdDesc();

}
