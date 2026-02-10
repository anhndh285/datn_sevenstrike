package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.NhanVien;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    List<NhanVien> findAllByXoaMemFalseOrderByIdDesc();

    Optional<NhanVien> findByIdAndXoaMemFalse(Integer id);

    Page<NhanVien> findAllByXoaMemFalse(Pageable pageable);

    // ✅ AuthService dùng
    Optional<NhanVien> findByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

    boolean existsByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

    boolean existsByEmailAndXoaMemFalse(String email);

    boolean existsByTenTaiKhoanAndXoaMemFalseAndIdNot(String tenTaiKhoan, Integer id);

    boolean existsByEmailAndXoaMemFalseAndIdNot(String email, Integer id);
}
