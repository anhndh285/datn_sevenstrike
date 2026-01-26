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

    Optional<KhachHang> findByIdAndXoaMemFalse(Integer id);

    List<KhachHang> findAllByXoaMemFalseOrderByIdDesc();

    Optional<KhachHang> findByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

    // ✅ NEW: paging server-side
    Page<KhachHang> findAllByXoaMemFalse(Pageable pageable);
}
