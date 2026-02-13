package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LichLamViec;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {
    Optional<LichLamViec> findByIdAndXoaMemFalse(Integer id);

    List<LichLamViec> findByXoaMemFalseAndNgayLamBetweenOrderByNgayLamAscIdAsc(LocalDate tuNgay, LocalDate denNgay);
}

