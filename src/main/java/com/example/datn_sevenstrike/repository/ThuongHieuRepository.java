package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ThuongHieu;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    List<ThuongHieu> findAllByXoaMemFalseOrderByIdDesc();

    List<ThuongHieu> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<ThuongHieu> findByIdAndXoaMemFalse(Integer id);
}
