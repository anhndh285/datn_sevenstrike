package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    java.util.Optional<PhieuGiamGia> findByIdAndXoaMemFalse(Integer id);
    java.util.List<PhieuGiamGia> findAllByXoaMemFalseOrderByIdDesc();

}
