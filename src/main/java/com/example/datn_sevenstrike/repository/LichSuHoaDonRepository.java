package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {

    java.util.Optional<LichSuHoaDon> findByIdAndXoaMemFalse(Integer id);
    java.util.List<LichSuHoaDon> findAllByXoaMemFalseOrderByIdDesc();

}
