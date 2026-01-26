package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.SanPham;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    Optional<SanPham> findByIdAndXoaMemFalse(Integer id);

    List<SanPham> findAllByXoaMemFalseOrderByIdDesc();

    // ✅ POS/Online
    List<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrueOrderByIdDesc();

    Optional<SanPham> findByIdAndXoaMemFalseAndTrangThaiKinhDoanhTrue(Integer id);

    // ✅ NEW: paging server-side
    Page<SanPham> findAllByXoaMemFalse(Pageable pageable);

    // ✅ NEW: paging sản phẩm đang kinh doanh
    Page<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrue(Pageable pageable);
}
