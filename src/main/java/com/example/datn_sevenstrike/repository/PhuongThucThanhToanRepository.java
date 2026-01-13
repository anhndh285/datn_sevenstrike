package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Integer> {

    java.util.Optional<PhuongThucThanhToan> findByIdAndXoaMemFalse(Integer id);
    java.util.List<PhuongThucThanhToan> findAllByXoaMemFalseOrderByIdDesc();

}
