package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.SanPham;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findAllByXoaMemFalseOrderByIdDesc();

    Page<SanPham> findAllByXoaMemFalse(Pageable pageable);

    Optional<SanPham> findByIdAndXoaMemFalse(Integer id);

    // chỉ lấy SP đang kinh doanh
    List<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrueOrderByIdDesc();

    Page<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrue(Pageable pageable);
}
