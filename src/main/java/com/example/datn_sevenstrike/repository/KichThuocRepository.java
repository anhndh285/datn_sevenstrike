package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {

    java.util.Optional<KichThuoc> findByIdAndXoaMemFalse(Integer id);
    java.util.List<KichThuoc> findAllByXoaMemFalseOrderByIdDesc();

}
