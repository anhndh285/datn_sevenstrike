package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.QuyenHan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuyenHanRepository extends JpaRepository<QuyenHan, Integer> {

    List<QuyenHan> findAllByXoaMemFalseOrderByIdDesc();

    List<QuyenHan> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<QuyenHan> findByIdAndXoaMemFalse(Integer id);
}
