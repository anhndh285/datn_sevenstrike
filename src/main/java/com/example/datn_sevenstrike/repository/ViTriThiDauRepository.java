package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ViTriThiDau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViTriThiDauRepository extends JpaRepository<ViTriThiDau, Integer> {

    java.util.Optional<ViTriThiDau> findByIdAndXoaMemFalse(Integer id);
    java.util.List<ViTriThiDau> findAllByXoaMemFalseOrderByIdDesc();

}
