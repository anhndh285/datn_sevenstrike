package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {

    List<LichSuHoaDon> findAllByXoaMemFalseOrderByIdDesc();

    Optional<LichSuHoaDon> findByIdAndXoaMemFalse(Integer id);

    List<LichSuHoaDon> findAllByIdHoaDonAndXoaMemFalseOrderByThoiGianAsc(Integer idHoaDon);
}
