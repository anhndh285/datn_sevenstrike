package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhuongThucThanhToan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Integer> {

    List<PhuongThucThanhToan> findAllByXoaMemFalseOrderByIdDesc();

    List<PhuongThucThanhToan> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<PhuongThucThanhToan> findByIdAndXoaMemFalse(Integer id);

    Optional<PhuongThucThanhToan> findFirstByTenPhuongThucThanhToanIgnoreCaseAndTrangThaiTrueAndXoaMemFalse(String ten);
}
