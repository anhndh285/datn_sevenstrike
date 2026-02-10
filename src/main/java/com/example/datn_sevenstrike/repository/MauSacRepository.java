package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    List<MauSac> findAllByXoaMemFalseOrderByIdDesc();

    List<MauSac> findAllByTrangThaiTrueAndXoaMemFalseOrderByIdDesc();

    Optional<MauSac> findByIdAndXoaMemFalse(Integer id);

    boolean existsByTenMauSacIgnoreCaseAndXoaMemFalse(String tenMauSac);

    boolean existsByMaMauHexIgnoreCaseAndXoaMemFalse(String maMauHex);

    boolean existsByTenMauSacIgnoreCaseAndXoaMemFalseAndIdNot(String tenMauSac, Integer id);

    boolean existsByMaMauHexIgnoreCaseAndXoaMemFalseAndIdNot(String maMauHex, Integer id);
}
