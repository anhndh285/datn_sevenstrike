package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ChiTietSanPham;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    List<ChiTietSanPham> findAllByXoaMemFalseOrderByIdDesc();

    Optional<ChiTietSanPham> findByIdAndXoaMemFalse(Integer id);

    List<ChiTietSanPham> findAllByIdSanPhamAndXoaMemFalseOrderByIdDesc(Integer idSanPham);

    boolean existsByIdSanPhamAndIdMauSacAndIdKichThuocAndIdLoaiSanAndIdFormChanAndXoaMemFalse(
            Integer idSanPham, Integer idMauSac, Integer idKichThuoc, Integer idLoaiSan, Integer idFormChan
    );

    boolean existsByIdSanPhamAndIdMauSacAndIdKichThuocAndIdLoaiSanAndIdFormChanAndXoaMemFalseAndIdNot(
            Integer idSanPham, Integer idMauSac, Integer idKichThuoc, Integer idLoaiSan, Integer idFormChan, Integer id
    );
}
