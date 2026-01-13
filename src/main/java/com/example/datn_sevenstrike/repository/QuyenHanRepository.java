package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.QuyenHan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuyenHanRepository extends JpaRepository<QuyenHan, Integer> {

    java.util.Optional<QuyenHan> findByIdAndXoaMemFalse(Integer id);
    java.util.List<QuyenHan> findAllByXoaMemFalseOrderByIdDesc();

}
