package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGiaCaNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, Integer> {

    java.util.Optional<PhieuGiamGiaCaNhan> findByIdAndXoaMemFalse(Integer id);
    java.util.List<PhieuGiamGiaCaNhan> findAllByXoaMemFalseOrderByIdDesc();
    boolean existsByIdKhachHangAndIdPhieuGiamGiaAndXoaMemFalse(Integer idKhachHang, Integer idPhieuGiamGia);
    boolean existsByIdAndIdKhachHangAndIdPhieuGiamGiaAndXoaMemFalse(Integer id, Integer idKhachHang, Integer idPhieuGiamGia);

}
