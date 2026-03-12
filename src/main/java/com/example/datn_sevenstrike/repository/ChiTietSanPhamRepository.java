package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ChiTietSanPham;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    List<ChiTietSanPham> findAllByXoaMemFalseOrderByIdDesc();

    Optional<ChiTietSanPham> findByIdAndXoaMemFalse(Integer id);

    List<ChiTietSanPham> findAllByIdSanPhamAndXoaMemFalseOrderByIdDesc(Integer idSanPham);

    boolean existsByIdSanPhamAndIdMauSacAndIdKichThuocAndIdLoaiSanAndIdFormChanAndXoaMemFalse(
            Integer idSanPham,
            Integer idMauSac,
            Integer idKichThuoc,
            Integer idLoaiSan,
            Integer idFormChan
    );

    boolean existsByIdSanPhamAndIdMauSacAndIdKichThuocAndIdLoaiSanAndIdFormChanAndXoaMemFalseAndIdNot(
            Integer idSanPham,
            Integer idMauSac,
            Integer idKichThuoc,
            Integer idLoaiSan,
            Integer idFormChan,
            Integer id
    );

    interface CtspBanHangView {
        Integer getId();
        String getMaCtsp();
        String getTenSanPham();
        String getMauSac();
        String getKichCo();
        Integer getSoLuong();
        BigDecimal getGiaNiemYet();
        BigDecimal getGiaBan();
        String getAnhUrl();
    }

    @Query(value = """
        select
            ctsp.id as id,
            ctsp.ma_chi_tiet_san_pham as maCtsp,
            sp.ten_san_pham as tenSanPham,
            ms.ten_mau_sac as mauSac,
            kc.ten_kich_thuoc as kichCo,
            ctsp.so_luong as soLuong,
            ctsp.gia_niem_yet as giaNiemYet,
            ctsp.gia_ban as giaBan,
            a.duong_dan_anh as anhUrl
        from chi_tiet_san_pham ctsp
        join san_pham sp
            on sp.id = ctsp.id_san_pham
           and sp.xoa_mem = 0
           and sp.trang_thai_kinh_doanh = 1
        join thuong_hieu th
            on th.id = sp.id_thuong_hieu
           and th.xoa_mem = 0
           and th.trang_thai = 1
        left join xuat_xu xx
            on xx.id = sp.id_xuat_xu
           and xx.xoa_mem = 0
           and xx.trang_thai = 1
        left join vi_tri_thi_dau vt
            on vt.id = sp.id_vi_tri_thi_dau
           and vt.xoa_mem = 0
           and vt.trang_thai = 1
        left join phong_cach_choi pc
            on pc.id = sp.id_phong_cach_choi
           and pc.xoa_mem = 0
           and pc.trang_thai = 1
        left join co_giay cg
            on cg.id = sp.id_co_giay
           and cg.xoa_mem = 0
           and cg.trang_thai = 1
        left join chat_lieu cl
            on cl.id = sp.id_chat_lieu
           and cl.xoa_mem = 0
           and cl.trang_thai = 1
        join mau_sac ms
            on ms.id = ctsp.id_mau_sac
           and ms.xoa_mem = 0
           and ms.trang_thai = 1
        join kich_thuoc kc
            on kc.id = ctsp.id_kich_thuoc
           and kc.xoa_mem = 0
           and kc.trang_thai = 1
        join loai_san ls
            on ls.id = ctsp.id_loai_san
           and ls.xoa_mem = 0
           and ls.trang_thai = 1
        join form_chan fc
            on fc.id = ctsp.id_form_chan
           and fc.xoa_mem = 0
           and fc.trang_thai = 1
        left join anh_chi_tiet_san_pham a
            on a.id_chi_tiet_san_pham = ctsp.id
           and a.la_anh_dai_dien = 1
           and a.xoa_mem = 0
        where ctsp.xoa_mem = 0
          and ctsp.trang_thai = 1
          and ctsp.so_luong > 0
          and (sp.id_xuat_xu is null or xx.id is not null)
          and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
          and (sp.id_phong_cach_choi is null or pc.id is not null)
          and (sp.id_co_giay is null or cg.id is not null)
          and (sp.id_chat_lieu is null or cl.id is not null)
        order by ctsp.id desc
    """, nativeQuery = true)
    List<CtspBanHangView> findBanHang();

    @Query(value = """
        select case when exists (
            select 1
            from chi_tiet_san_pham ctsp
            join san_pham sp
                on sp.id = ctsp.id_san_pham
               and sp.xoa_mem = 0
               and sp.trang_thai_kinh_doanh = 1
            join thuong_hieu th
                on th.id = sp.id_thuong_hieu
               and th.xoa_mem = 0
               and th.trang_thai = 1
            left join xuat_xu xx
                on xx.id = sp.id_xuat_xu
               and xx.xoa_mem = 0
               and xx.trang_thai = 1
            left join vi_tri_thi_dau vt
                on vt.id = sp.id_vi_tri_thi_dau
               and vt.xoa_mem = 0
               and vt.trang_thai = 1
            left join phong_cach_choi pc
                on pc.id = sp.id_phong_cach_choi
               and pc.xoa_mem = 0
               and pc.trang_thai = 1
            left join co_giay cg
                on cg.id = sp.id_co_giay
               and cg.xoa_mem = 0
               and cg.trang_thai = 1
            left join chat_lieu cl
                on cl.id = sp.id_chat_lieu
               and cl.xoa_mem = 0
               and cl.trang_thai = 1
            join mau_sac ms
                on ms.id = ctsp.id_mau_sac
               and ms.xoa_mem = 0
               and ms.trang_thai = 1
            join kich_thuoc kc
                on kc.id = ctsp.id_kich_thuoc
               and kc.xoa_mem = 0
               and kc.trang_thai = 1
            join loai_san ls
                on ls.id = ctsp.id_loai_san
               and ls.xoa_mem = 0
               and ls.trang_thai = 1
            join form_chan fc
                on fc.id = ctsp.id_form_chan
               and fc.xoa_mem = 0
               and fc.trang_thai = 1
            where ctsp.id = :ctspId
              and ctsp.xoa_mem = 0
              and ctsp.trang_thai = 1
              and (sp.id_xuat_xu is null or xx.id is not null)
              and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
              and (sp.id_phong_cach_choi is null or pc.id is not null)
              and (sp.id_co_giay is null or cg.id is not null)
              and (sp.id_chat_lieu is null or cl.id is not null)
        ) then cast(1 as bit) else cast(0 as bit) end
    """, nativeQuery = true)
    boolean existsBanDuoc(@Param("ctspId") Integer ctspId);

    // =========================
    // CASCADE OFF
    // =========================

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where id_san_pham = :sanPhamId
           and xoa_mem = 0
           and trang_thai = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoSanPham(@Param("sanPhamId") Integer sanPhamId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 1
           and exists (
               select 1
               from san_pham sp
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.id_thuong_hieu = :thuongHieuId
           )
    """, nativeQuery = true)
    int ngungKinhDoanhTheoThuongHieu(@Param("thuongHieuId") Integer thuongHieuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 1
           and exists (
               select 1
               from san_pham sp
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.id_xuat_xu = :xuatXuId
           )
    """, nativeQuery = true)
    int ngungKinhDoanhTheoXuatXu(@Param("xuatXuId") Integer xuatXuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 1
           and exists (
               select 1
               from san_pham sp
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.id_vi_tri_thi_dau = :viTriThiDauId
           )
    """, nativeQuery = true)
    int ngungKinhDoanhTheoViTriThiDau(@Param("viTriThiDauId") Integer viTriThiDauId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 1
           and exists (
               select 1
               from san_pham sp
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.id_phong_cach_choi = :phongCachChoiId
           )
    """, nativeQuery = true)
    int ngungKinhDoanhTheoPhongCachChoi(@Param("phongCachChoiId") Integer phongCachChoiId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 1
           and exists (
               select 1
               from san_pham sp
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.id_co_giay = :coGiayId
           )
    """, nativeQuery = true)
    int ngungKinhDoanhTheoCoGiay(@Param("coGiayId") Integer coGiayId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 1
           and exists (
               select 1
               from san_pham sp
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.id_chat_lieu = :chatLieuId
           )
    """, nativeQuery = true)
    int ngungKinhDoanhTheoChatLieu(@Param("chatLieuId") Integer chatLieuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where id_mau_sac = :mauSacId
           and xoa_mem = 0
           and trang_thai = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoMauSac(@Param("mauSacId") Integer mauSacId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where id_kich_thuoc = :kichThuocId
           and xoa_mem = 0
           and trang_thai = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoKichThuoc(@Param("kichThuocId") Integer kichThuocId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where id_loai_san = :loaiSanId
           and xoa_mem = 0
           and trang_thai = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoLoaiSan(@Param("loaiSanId") Integer loaiSanId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 0,
               ngay_cap_nhat = sysdatetime()
         where id_form_chan = :formChanId
           and xoa_mem = 0
           and trang_thai = 1
    """, nativeQuery = true)
    int ngungKinhDoanhTheoFormChan(@Param("formChanId") Integer formChanId);

    // =========================
    // CASCADE ON
    // =========================

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where id_san_pham = :sanPhamId
           and xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.id = :sanPhamId
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoSanPham(@Param("sanPhamId") Integer sanPhamId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and sp.id_thuong_hieu = :thuongHieuId
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoThuongHieu(@Param("thuongHieuId") Integer thuongHieuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and sp.id_xuat_xu = :xuatXuId
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoXuatXu(@Param("xuatXuId") Integer xuatXuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and sp.id_vi_tri_thi_dau = :viTriThiDauId
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoViTriThiDau(@Param("viTriThiDauId") Integer viTriThiDauId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and sp.id_phong_cach_choi = :phongCachChoiId
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoPhongCachChoi(@Param("phongCachChoiId") Integer phongCachChoiId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and sp.id_co_giay = :coGiayId
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoCoGiay(@Param("coGiayId") Integer coGiayId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and sp.id_chat_lieu = :chatLieuId
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoChatLieu(@Param("chatLieuId") Integer chatLieuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where id_mau_sac = :mauSacId
           and xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoMauSac(@Param("mauSacId") Integer mauSacId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where id_kich_thuoc = :kichThuocId
           and xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoKichThuoc(@Param("kichThuocId") Integer kichThuocId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where id_loai_san = :loaiSanId
           and xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoLoaiSan(@Param("loaiSanId") Integer loaiSanId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set trang_thai = 1,
               ngay_cap_nhat = sysdatetime()
         where id_form_chan = :formChanId
           and xoa_mem = 0
           and trang_thai = 0
           and exists (
               select 1
               from san_pham sp
               join thuong_hieu th
                   on th.id = sp.id_thuong_hieu
                  and th.xoa_mem = 0
                  and th.trang_thai = 1
               left join xuat_xu xx
                   on xx.id = sp.id_xuat_xu
                  and xx.xoa_mem = 0
                  and xx.trang_thai = 1
               left join vi_tri_thi_dau vt
                   on vt.id = sp.id_vi_tri_thi_dau
                  and vt.xoa_mem = 0
                  and vt.trang_thai = 1
               left join phong_cach_choi pc
                   on pc.id = sp.id_phong_cach_choi
                  and pc.xoa_mem = 0
                  and pc.trang_thai = 1
               left join co_giay cg
                   on cg.id = sp.id_co_giay
                  and cg.xoa_mem = 0
                  and cg.trang_thai = 1
               left join chat_lieu cl
                   on cl.id = sp.id_chat_lieu
                  and cl.xoa_mem = 0
                  and cl.trang_thai = 1
               join mau_sac ms
                   on ms.id = chi_tiet_san_pham.id_mau_sac
                  and ms.xoa_mem = 0
                  and ms.trang_thai = 1
               join kich_thuoc kc
                   on kc.id = chi_tiet_san_pham.id_kich_thuoc
                  and kc.xoa_mem = 0
                  and kc.trang_thai = 1
               join loai_san ls
                   on ls.id = chi_tiet_san_pham.id_loai_san
                  and ls.xoa_mem = 0
                  and ls.trang_thai = 1
               join form_chan fc
                   on fc.id = chi_tiet_san_pham.id_form_chan
                  and fc.xoa_mem = 0
                  and fc.trang_thai = 1
               where sp.id = chi_tiet_san_pham.id_san_pham
                 and sp.xoa_mem = 0
                 and sp.trang_thai_kinh_doanh = 1
                 and (sp.id_xuat_xu is null or xx.id is not null)
                 and (sp.id_vi_tri_thi_dau is null or vt.id is not null)
                 and (sp.id_phong_cach_choi is null or pc.id is not null)
                 and (sp.id_co_giay is null or cg.id is not null)
                 and (sp.id_chat_lieu is null or cl.id is not null)
           )
    """, nativeQuery = true)
    int batKinhDoanhTheoFormChan(@Param("formChanId") Integer formChanId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set so_luong = so_luong - :qty,
               ngay_cap_nhat = sysdatetime()
         where id = :ctspId
           and xoa_mem = 0
           and trang_thai = 1
           and so_luong >= :qty
    """, nativeQuery = true)
    int giamTonNeuDu(@Param("ctspId") Integer ctspId, @Param("qty") Integer qty);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update chi_tiet_san_pham
           set so_luong = so_luong + :qty,
               ngay_cap_nhat = sysdatetime()
         where id = :ctspId
           and xoa_mem = 0
    """, nativeQuery = true)
    int tangTon(@Param("ctspId") Integer ctspId, @Param("qty") Integer qty);
}