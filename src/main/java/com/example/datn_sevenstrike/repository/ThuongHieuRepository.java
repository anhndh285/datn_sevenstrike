package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {

    java.util.Optional<ThuongHieu> findByIdAndXoaMemFalse(Integer id);
    java.util.List<ThuongHieu> findAllByXoaMemFalseOrderByIdDesc();

}
