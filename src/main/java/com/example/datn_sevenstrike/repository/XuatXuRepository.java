package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.XuatXu;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XuatXuRepository extends JpaRepository<XuatXu, Integer> {

    List<XuatXu> findAllByXoaMemFalseOrderByIdDesc();

    Optional<XuatXu> findByIdAndXoaMemFalse(Integer id);
}
