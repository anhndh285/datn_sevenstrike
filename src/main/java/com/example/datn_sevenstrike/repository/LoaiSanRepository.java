package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LoaiSan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiSanRepository extends JpaRepository<LoaiSan, Integer> {

    List<LoaiSan> findAllByXoaMemFalseOrderByIdDesc();

    List<LoaiSan> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<LoaiSan> findByIdAndXoaMemFalse(Integer id);
}
