package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.CaLam;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaLamRepository extends JpaRepository<CaLam, Integer> {
    List<CaLam> findByXoaMemFalseOrderByIdDesc();
}

