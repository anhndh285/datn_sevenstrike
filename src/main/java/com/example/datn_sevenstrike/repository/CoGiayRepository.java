package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.CoGiay;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoGiayRepository extends JpaRepository<CoGiay, Integer> {

    List<CoGiay> findAllByXoaMemFalseOrderByIdDesc();

    List<CoGiay> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<CoGiay> findByIdAndXoaMemFalse(Integer id);
}
