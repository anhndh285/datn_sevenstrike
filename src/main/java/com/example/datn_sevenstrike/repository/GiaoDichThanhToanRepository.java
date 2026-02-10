package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.GiaoDichThanhToan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiaoDichThanhToanRepository extends JpaRepository<GiaoDichThanhToan, Integer> {

    List<GiaoDichThanhToan> findAllByXoaMemFalseOrderByIdDesc();

    Optional<GiaoDichThanhToan> findByIdAndXoaMemFalse(Integer id);

    List<GiaoDichThanhToan> findAllByIdHoaDonAndXoaMemFalseOrderByThoiGianTaoDesc(Integer idHoaDon);
}
