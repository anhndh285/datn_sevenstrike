package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhongCachChoi;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhongCachChoiRepository extends JpaRepository<PhongCachChoi, Integer> {

    List<PhongCachChoi> findAllByXoaMemFalseOrderByIdDesc();

    List<PhongCachChoi> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<PhongCachChoi> findByIdAndXoaMemFalse(Integer id);
}
