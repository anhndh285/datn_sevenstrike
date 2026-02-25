package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.CaLam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaLamRepository extends JpaRepository<CaLam, Integer> {
    List<CaLam> findAllByXoaMemFalseOrderByIdDesc();

    Optional<CaLam> findByIdAndXoaMemFalse(Integer id);

    Page<CaLam> findAllByXoaMemFalse(Pageable pageable);
}