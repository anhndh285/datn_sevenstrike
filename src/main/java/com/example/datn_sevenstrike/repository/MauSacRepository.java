package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    java.util.Optional<MauSac> findByIdAndXoaMemFalse(Integer id);
    java.util.List<MauSac> findAllByXoaMemFalseOrderByIdDesc();

}
