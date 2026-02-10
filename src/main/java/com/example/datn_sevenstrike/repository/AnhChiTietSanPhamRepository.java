package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.AnhChiTietSanPham;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnhChiTietSanPhamRepository extends JpaRepository<AnhChiTietSanPham, Integer> {

    List<AnhChiTietSanPham> findAllByXoaMemFalseOrderByIdDesc();

    Optional<AnhChiTietSanPham> findByIdAndXoaMemFalse(Integer id);

    List<AnhChiTietSanPham> findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(Integer idChiTietSanPham);

    // unset ảnh đại diện cũ theo CTSP (tránh unique index)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update AnhChiTietSanPham a
           set a.laAnhDaiDien = false
         where a.idChiTietSanPham = :idCtsp
           and a.xoaMem = false
           and a.laAnhDaiDien = true
    """)
    int unsetDaiDien(@Param("idCtsp") Integer idCtsp);

    // unset ảnh đại diện cũ theo CTSP nhưng chừa lại 1 ảnh (khi set đại diện theo id)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update AnhChiTietSanPham a
           set a.laAnhDaiDien = false
         where a.idChiTietSanPham = :idCtsp
           and a.xoaMem = false
           and a.laAnhDaiDien = true
           and a.id <> :idKeep
    """)
    int unsetDaiDienExcept(@Param("idCtsp") Integer idCtsp, @Param("idKeep") Integer idKeep);
}
