package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ChiTietDotGiamGia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietDotGiamGiaRepository extends JpaRepository<ChiTietDotGiamGia, Integer> {

    List<ChiTietDotGiamGia> findAllByXoaMemFalseOrderByIdDesc();

    Optional<ChiTietDotGiamGia> findByIdAndXoaMemFalse(Integer id);

    List<ChiTietDotGiamGia> findAllByIdDotGiamGiaAndXoaMemFalseOrderByIdDesc(Integer idDotGiamGia);

    List<ChiTietDotGiamGia> findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(Integer idChiTietSanPham);

    boolean existsByIdDotGiamGiaAndIdChiTietSanPhamAndXoaMemFalse(Integer idDotGiamGia, Integer idChiTietSanPham);

    boolean existsByIdDotGiamGiaAndIdChiTietSanPhamAndXoaMemFalseAndIdNot(
            Integer idDotGiamGia, Integer idChiTietSanPham, Integer id
    );
}
