package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ViTriThiDau;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViTriThiDauRepository extends JpaRepository<ViTriThiDau, Integer> {

    List<ViTriThiDau> findAllByXoaMemFalseOrderByIdDesc();

    Optional<ViTriThiDau> findByIdAndXoaMemFalse(Integer id);
}
