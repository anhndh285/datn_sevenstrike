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

    Optional<NhanVien> findByIdAndXoaMemFalse(Integer id);

    List<NhanVien> findAllByXoaMemFalseOrderByIdDesc();

    Optional<NhanVien> findByTenTaiKhoanAndXoaMemFalse(String tenTaiKhoan);

    // ✅ NEW: paging server-side
    Page<NhanVien> findAllByXoaMemFalse(Pageable pageable);
}
