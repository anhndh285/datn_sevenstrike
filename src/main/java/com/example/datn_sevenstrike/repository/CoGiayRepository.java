package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.CoGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoGiayRepository extends JpaRepository<CoGiay, Integer> {

    java.util.Optional<CoGiay> findByIdAndXoaMemFalse(Integer id);
    java.util.List<CoGiay> findAllByXoaMemFalseOrderByIdDesc();

}
