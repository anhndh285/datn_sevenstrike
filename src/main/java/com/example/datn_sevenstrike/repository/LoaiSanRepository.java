package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LoaiSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiSanRepository extends JpaRepository<LoaiSan, Integer> {

    java.util.Optional<LoaiSan> findByIdAndXoaMemFalse(Integer id);
    java.util.List<LoaiSan> findAllByXoaMemFalseOrderByIdDesc();

}
