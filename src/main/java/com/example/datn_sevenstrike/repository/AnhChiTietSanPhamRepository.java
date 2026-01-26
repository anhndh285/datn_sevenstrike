package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.AnhChiTietSanPham;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnhChiTietSanPhamRepository extends JpaRepository<AnhChiTietSanPham, Integer> {

    Optional<AnhChiTietSanPham> findByIdAndXoaMemFalse(Integer id);

    List<AnhChiTietSanPham> findAllByXoaMemFalseOrderByIdDesc();

    List<AnhChiTietSanPham> findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(Integer idChiTietSanPham);

    List<AnhChiTietSanPham> findAllByIdChiTietSanPhamAndXoaMemFalse(Integer idChiTietSanPham);

    // lấy các ảnh đang là đại diện (để hạ xuống false)
    List<AnhChiTietSanPham> findAllByIdChiTietSanPhamAndLaAnhDaiDienTrueAndXoaMemFalse(Integer idChiTietSanPham);

    // ✅ Unset ảnh đại diện cũ để tránh unique filtered index (la_anh_dai_dien=1 & xoa_mem=0)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update AnhChiTietSanPham a
           set a.laAnhDaiDien = false
         where a.idChiTietSanPham = :idCtsp
           and a.xoaMem = false
           and a.laAnhDaiDien = true
    """)
    int unsetDaiDien(@Param("idCtsp") Integer idCtsp);

    // ✅ Unset ảnh đại diện cũ nhưng chừa lại 1 id (khi set lại chính nó)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update AnhChiTietSanPham a
           set a.laAnhDaiDien = false
         where a.idChiTietSanPham = :idCtsp
           and a.xoaMem = false
           and a.laAnhDaiDien = true
           and a.id <> :exceptId
    """)
    int unsetDaiDienExcept(@Param("idCtsp") Integer idCtsp, @Param("exceptId") Integer exceptId);
}
