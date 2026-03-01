package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.SanPham;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findAllByXoaMemFalseOrderByIdDesc();

    Page<SanPham> findAllByXoaMemFalse(Pageable pageable);

    Optional<SanPham> findByIdAndXoaMemFalse(Integer id);

    // chỉ lấy SP đang kinh doanh
    List<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrueOrderByIdDesc();

    Page<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrue(Pageable pageable);

    // Hàng mới nhập trong vòng N ngày gần đây
    @Query("SELECT s FROM SanPham s WHERE s.ngayTao >= :threshold AND s.xoaMem = false AND s.trangThaiKinhDoanh = true ORDER BY s.ngayTao DESC")
    List<SanPham> findNewArrivals(@Param("threshold") LocalDateTime threshold, Pageable pageable);
}
