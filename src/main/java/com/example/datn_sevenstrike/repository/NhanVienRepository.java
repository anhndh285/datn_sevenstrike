package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    java.util.Optional<NhanVien> findByIdAndXoaMemFalse(Integer id);
    java.util.List<NhanVien> findAllByXoaMemFalseOrderByIdDesc();
    java.util.Optional<NhanVien> findByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

}
