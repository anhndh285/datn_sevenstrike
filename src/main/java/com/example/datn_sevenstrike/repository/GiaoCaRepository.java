package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.GiaoCa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiaoCaRepository extends JpaRepository<GiaoCa, Integer> {

    Optional<GiaoCa> findFirstByIdNhanVienAndXoaMemFalseAndTrangThaiAndThoiGianKetCaIsNullOrderByIdDesc(
            Integer idNhanVien, Integer trangThai
    );

    boolean existsByIdNhanVienAndXoaMemFalseAndTrangThaiAndThoiGianKetCaIsNull(Integer idNhanVien, Integer trangThai);

    Optional<GiaoCa> findByIdAndXoaMemFalse(Integer id);

    Optional<GiaoCa> findFirstByIdLichLamViecAndXoaMemFalseAndTrangThaiOrderByIdDesc(
            Integer idLichLamViec, Integer trangThai
    );
}
