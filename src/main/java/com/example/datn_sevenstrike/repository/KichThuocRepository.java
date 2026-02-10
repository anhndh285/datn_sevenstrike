package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {

    List<KichThuoc> findAllByXoaMemFalseOrderByIdDesc();

    List<KichThuoc> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<KichThuoc> findByIdAndXoaMemFalse(Integer id);

    boolean existsByGiaTriKichThuocAndXoaMemFalse(BigDecimal giaTriKichThuoc);

    boolean existsByGiaTriKichThuocAndXoaMemFalseAndIdNot(BigDecimal giaTriKichThuoc, Integer id);
}
