package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    java.util.Optional<HoaDon> findByIdAndXoaMemFalse(Integer id);
    java.util.List<HoaDon> findAllByXoaMemFalseOrderByIdDesc();

}
