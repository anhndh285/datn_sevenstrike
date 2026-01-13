package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.DotGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Integer> {

    java.util.Optional<DotGiamGia> findByIdAndXoaMemFalse(Integer id);
    java.util.List<DotGiamGia> findAllByXoaMemFalseOrderByIdDesc();

}
