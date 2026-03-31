package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.KhachHang;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    List<KhachHang> findAllByXoaMemFalseOrderByIdDesc();

    List<KhachHang> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<KhachHang> findByIdAndXoaMemFalse(Integer id);

    Optional<KhachHang> findByIdAndXoaMemFalseAndTrangThaiTrue(Integer id);

    Page<KhachHang> findAllByXoaMemFalse(Pageable pageable);

    Page<KhachHang> findAllByXoaMemFalseAndTrangThaiTrue(Pageable pageable);

    // ✅ AuthService dùng
    Optional<KhachHang> findByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

    // ✅ Validate trùng (nếu DB có unique filtered theo xoa_mem)
    boolean existsByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

    boolean existsByEmailAndXoaMemFalse(String email);

    boolean existsByTenTaiKhoanAndXoaMemFalseAndIdNot(String tenTaiKhoan, Integer id);

    boolean existsByEmailAndXoaMemFalseAndIdNot(String email, Integer id);
}