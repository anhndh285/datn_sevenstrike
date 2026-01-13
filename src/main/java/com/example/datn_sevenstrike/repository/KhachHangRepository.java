package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    java.util.Optional<KhachHang> findByIdAndXoaMemFalse(Integer id);
    java.util.List<KhachHang> findAllByXoaMemFalseOrderByIdDesc();
    java.util.Optional<KhachHang> findByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

}
