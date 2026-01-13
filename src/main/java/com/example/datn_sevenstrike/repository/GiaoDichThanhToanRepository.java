package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.GiaoDichThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiaoDichThanhToanRepository extends JpaRepository<GiaoDichThanhToan, Integer> {

    java.util.Optional<GiaoDichThanhToan> findByIdAndXoaMemFalse(Integer id);
    java.util.List<GiaoDichThanhToan> findAllByXoaMemFalseOrderByIdDesc();

}
