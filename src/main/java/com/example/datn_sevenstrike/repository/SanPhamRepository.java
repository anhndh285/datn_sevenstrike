package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.SanPham;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findAllByXoaMemFalseOrderByIdDesc();

    Page<SanPham> findAllByXoaMemFalse(Pageable pageable);

    Optional<SanPham> findByIdAndXoaMemFalse(Integer id);

    List<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrueOrderByIdDesc();

    Page<SanPham> findAllByXoaMemFalseAndTrangThaiKinhDoanhTrue(Pageable pageable);

    @Query("""
        SELECT s
        FROM SanPham s
        WHERE s.ngayTao >= :threshold
          AND s.xoaMem = false
          AND s.trangThaiKinhDoanh = true
        ORDER BY s.ngayTao DESC
    """)
    List<SanPham> findNewArrivals(@Param("threshold") LocalDateTime threshold, Pageable pageable);

    // =========================
    // CASCADE OFF
    // =========================

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 0,
               ngay_cap_nhat = sysdatetime()
         where id_thuong_hieu = :thuongHieuId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoThuongHieu(@Param("thuongHieuId") Integer thuongHieuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 0,
               ngay_cap_nhat = sysdatetime()
         where id_xuat_xu = :xuatXuId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoXuatXu(@Param("xuatXuId") Integer xuatXuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 0,
               ngay_cap_nhat = sysdatetime()
         where id_vi_tri_thi_dau = :viTriThiDauId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoViTriThiDau(@Param("viTriThiDauId") Integer viTriThiDauId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 0,
               ngay_cap_nhat = sysdatetime()
         where id_phong_cach_choi = :phongCachChoiId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoPhongCachChoi(@Param("phongCachChoiId") Integer phongCachChoiId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 0,
               ngay_cap_nhat = sysdatetime()
         where id_co_giay = :coGiayId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoCoGiay(@Param("coGiayId") Integer coGiayId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 0,
               ngay_cap_nhat = sysdatetime()
         where id_chat_lieu = :chatLieuId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoChatLieu(@Param("chatLieuId") Integer chatLieuId);

    // =========================
    // CASCADE ON
    // =========================

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 1,
               ngay_cap_nhat = sysdatetime()
         where id_thuong_hieu = :thuongHieuId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 0
           and exists (
               select 1
               from thuong_hieu th
               where th.id = san_pham.id_thuong_hieu
                 and th.xoa_mem = 0
                 and th.trang_thai = 1
           )
           and (
               id_xuat_xu is null or exists (
                   select 1 from xuat_xu xx
                   where xx.id = san_pham.id_xuat_xu
                     and xx.xoa_mem = 0
                     and xx.trang_thai = 1
               )
           )
           and (
               id_vi_tri_thi_dau is null or exists (
                   select 1 from vi_tri_thi_dau vt
                   where vt.id = san_pham.id_vi_tri_thi_dau
                     and vt.xoa_mem = 0
                     and vt.trang_thai = 1
               )
           )
           and (
               id_phong_cach_choi is null or exists (
                   select 1 from phong_cach_choi pc
                   where pc.id = san_pham.id_phong_cach_choi
                     and pc.xoa_mem = 0
                     and pc.trang_thai = 1
               )
           )
           and (
               id_co_giay is null or exists (
                   select 1 from co_giay cg
                   where cg.id = san_pham.id_co_giay
                     and cg.xoa_mem = 0
                     and cg.trang_thai = 1
               )
           )
           and (
               id_chat_lieu is null or exists (
                   select 1 from chat_lieu cl
                   where cl.id = san_pham.id_chat_lieu
                     and cl.xoa_mem = 0
                     and cl.trang_thai = 1
               )
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoThuongHieu(@Param("thuongHieuId") Integer thuongHieuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 1,
               ngay_cap_nhat = sysdatetime()
         where id_xuat_xu = :xuatXuId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 0
           and exists (
               select 1
               from thuong_hieu th
               where th.id = san_pham.id_thuong_hieu
                 and th.xoa_mem = 0
                 and th.trang_thai = 1
           )
           and exists (
               select 1 from xuat_xu xx
               where xx.id = san_pham.id_xuat_xu
                 and xx.xoa_mem = 0
                 and xx.trang_thai = 1
           )
           and (
               id_vi_tri_thi_dau is null or exists (
                   select 1 from vi_tri_thi_dau vt
                   where vt.id = san_pham.id_vi_tri_thi_dau
                     and vt.xoa_mem = 0
                     and vt.trang_thai = 1
               )
           )
           and (
               id_phong_cach_choi is null or exists (
                   select 1 from phong_cach_choi pc
                   where pc.id = san_pham.id_phong_cach_choi
                     and pc.xoa_mem = 0
                     and pc.trang_thai = 1
               )
           )
           and (
               id_co_giay is null or exists (
                   select 1 from co_giay cg
                   where cg.id = san_pham.id_co_giay
                     and cg.xoa_mem = 0
                     and cg.trang_thai = 1
               )
           )
           and (
               id_chat_lieu is null or exists (
                   select 1 from chat_lieu cl
                   where cl.id = san_pham.id_chat_lieu
                     and cl.xoa_mem = 0
                     and cl.trang_thai = 1
               )
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoXuatXu(@Param("xuatXuId") Integer xuatXuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 1,
               ngay_cap_nhat = sysdatetime()
         where id_vi_tri_thi_dau = :viTriThiDauId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 0
           and exists (
               select 1
               from thuong_hieu th
               where th.id = san_pham.id_thuong_hieu
                 and th.xoa_mem = 0
                 and th.trang_thai = 1
           )
           and (
               id_xuat_xu is null or exists (
                   select 1 from xuat_xu xx
                   where xx.id = san_pham.id_xuat_xu
                     and xx.xoa_mem = 0
                     and xx.trang_thai = 1
               )
           )
           and exists (
               select 1 from vi_tri_thi_dau vt
               where vt.id = san_pham.id_vi_tri_thi_dau
                 and vt.xoa_mem = 0
                 and vt.trang_thai = 1
           )
           and (
               id_phong_cach_choi is null or exists (
                   select 1 from phong_cach_choi pc
                   where pc.id = san_pham.id_phong_cach_choi
                     and pc.xoa_mem = 0
                     and pc.trang_thai = 1
               )
           )
           and (
               id_co_giay is null or exists (
                   select 1 from co_giay cg
                   where cg.id = san_pham.id_co_giay
                     and cg.xoa_mem = 0
                     and cg.trang_thai = 1
               )
           )
           and (
               id_chat_lieu is null or exists (
                   select 1 from chat_lieu cl
                   where cl.id = san_pham.id_chat_lieu
                     and cl.xoa_mem = 0
                     and cl.trang_thai = 1
               )
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoViTriThiDau(@Param("viTriThiDauId") Integer viTriThiDauId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 1,
               ngay_cap_nhat = sysdatetime()
         where id_phong_cach_choi = :phongCachChoiId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 0
           and exists (
               select 1
               from thuong_hieu th
               where th.id = san_pham.id_thuong_hieu
                 and th.xoa_mem = 0
                 and th.trang_thai = 1
           )
           and (
               id_xuat_xu is null or exists (
                   select 1 from xuat_xu xx
                   where xx.id = san_pham.id_xuat_xu
                     and xx.xoa_mem = 0
                     and xx.trang_thai = 1
               )
           )
           and (
               id_vi_tri_thi_dau is null or exists (
                   select 1 from vi_tri_thi_dau vt
                   where vt.id = san_pham.id_vi_tri_thi_dau
                     and vt.xoa_mem = 0
                     and vt.trang_thai = 1
               )
           )
           and exists (
               select 1 from phong_cach_choi pc
               where pc.id = san_pham.id_phong_cach_choi
                 and pc.xoa_mem = 0
                 and pc.trang_thai = 1
           )
           and (
               id_co_giay is null or exists (
                   select 1 from co_giay cg
                   where cg.id = san_pham.id_co_giay
                     and cg.xoa_mem = 0
                     and cg.trang_thai = 1
               )
           )
           and (
               id_chat_lieu is null or exists (
                   select 1 from chat_lieu cl
                   where cl.id = san_pham.id_chat_lieu
                     and cl.xoa_mem = 0
                     and cl.trang_thai = 1
               )
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoPhongCachChoi(@Param("phongCachChoiId") Integer phongCachChoiId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 1,
               ngay_cap_nhat = sysdatetime()
         where id_co_giay = :coGiayId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 0
           and exists (
               select 1
               from thuong_hieu th
               where th.id = san_pham.id_thuong_hieu
                 and th.xoa_mem = 0
                 and th.trang_thai = 1
           )
           and (
               id_xuat_xu is null or exists (
                   select 1 from xuat_xu xx
                   where xx.id = san_pham.id_xuat_xu
                     and xx.xoa_mem = 0
                     and xx.trang_thai = 1
               )
           )
           and (
               id_vi_tri_thi_dau is null or exists (
                   select 1 from vi_tri_thi_dau vt
                   where vt.id = san_pham.id_vi_tri_thi_dau
                     and vt.xoa_mem = 0
                     and vt.trang_thai = 1
               )
           )
           and (
               id_phong_cach_choi is null or exists (
                   select 1 from phong_cach_choi pc
                   where pc.id = san_pham.id_phong_cach_choi
                     and pc.xoa_mem = 0
                     and pc.trang_thai = 1
               )
           )
           and exists (
               select 1 from co_giay cg
               where cg.id = san_pham.id_co_giay
                 and cg.xoa_mem = 0
                 and cg.trang_thai = 1
           )
           and (
               id_chat_lieu is null or exists (
                   select 1 from chat_lieu cl
                   where cl.id = san_pham.id_chat_lieu
                     and cl.xoa_mem = 0
                     and cl.trang_thai = 1
               )
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoCoGiay(@Param("coGiayId") Integer coGiayId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update san_pham
           set trang_thai_kinh_doanh = 1,
               ngay_cap_nhat = sysdatetime()
         where id_chat_lieu = :chatLieuId
           and xoa_mem = 0
           and trang_thai_kinh_doanh = 0
           and exists (
               select 1
               from thuong_hieu th
               where th.id = san_pham.id_thuong_hieu
                 and th.xoa_mem = 0
                 and th.trang_thai = 1
           )
           and (
               id_xuat_xu is null or exists (
                   select 1 from xuat_xu xx
                   where xx.id = san_pham.id_xuat_xu
                     and xx.xoa_mem = 0
                     and xx.trang_thai = 1
               )
           )
           and (
               id_vi_tri_thi_dau is null or exists (
                   select 1 from vi_tri_thi_dau vt
                   where vt.id = san_pham.id_vi_tri_thi_dau
                     and vt.xoa_mem = 0
                     and vt.trang_thai = 1
               )
           )
           and (
               id_phong_cach_choi is null or exists (
                   select 1 from phong_cach_choi pc
                   where pc.id = san_pham.id_phong_cach_choi
                     and pc.xoa_mem = 0
                     and pc.trang_thai = 1
               )
           )
           and (
               id_co_giay is null or exists (
                   select 1 from co_giay cg
                   where cg.id = san_pham.id_co_giay
                     and cg.xoa_mem = 0
                     and cg.trang_thai = 1
               )
           )
           and exists (
               select 1 from chat_lieu cl
               where cl.id = san_pham.id_chat_lieu
                 and cl.xoa_mem = 0
                 and cl.trang_thai = 1
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoChatLieu(@Param("chatLieuId") Integer chatLieuId);
}