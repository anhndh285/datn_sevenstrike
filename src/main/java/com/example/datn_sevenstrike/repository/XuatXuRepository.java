package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.XuatXu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XuatXuRepository extends JpaRepository<XuatXu, Integer> {

    java.util.Optional<XuatXu> findByIdAndXoaMemFalse(Integer id);
    java.util.List<XuatXu> findAllByXoaMemFalseOrderByIdDesc();

}
