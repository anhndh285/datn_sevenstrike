package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.DotGiamGia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Integer> {

    List<DotGiamGia> findAllByXoaMemFalse();

    List<DotGiamGia> findAllByXoaMemFalseAndTrangThaiTrue();

    List<DotGiamGia> findAllByXoaMemFalseOrderByIdDesc();

    List<DotGiamGia> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<DotGiamGia> findByIdAndXoaMemFalse(Integer id);
}
