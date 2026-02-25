package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LichLamViec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {

    // kiểm tra trùng ca + ngày
    @Query("""
        SELECT l FROM LichLamViec l
        WHERE l.idCaLam.id = :idCa
        AND l.ngayLam = :ngay
        AND l.xoaMem = false
    """)
    Optional<LichLamViec> findTrungCa(LocalDate ngay, Integer idCa);

    List<LichLamViec> findAllByXoaMemFalseOrderByIdDesc();

    Optional<LichLamViec> findByIdAndXoaMemFalse(Integer id);

    Page<LichLamViec> findAllByXoaMemFalse(Pageable pageable);

}
