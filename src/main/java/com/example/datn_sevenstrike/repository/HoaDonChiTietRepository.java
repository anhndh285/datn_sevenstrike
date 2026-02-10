package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.HoaDonChiTiet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {

    List<HoaDonChiTiet> findAllByXoaMemFalseOrderByIdDesc();

    Optional<HoaDonChiTiet> findByIdAndXoaMemFalse(Integer id);

    List<HoaDonChiTiet> findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(Integer idHoaDon);
    List<HoaDonChiTiet> findByIdHoaDon(Integer idHoaDon);

}
