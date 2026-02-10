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
            ctsp.gia_ban as giaBan,
            a.duong_dan_anh as anhUrl
        from chi_tiet_san_pham ctsp
        join san_pham sp on sp.id = ctsp.id_san_pham and sp.xoa_mem = 0
        join mau_sac ms on ms.id = ctsp.id_mau_sac and ms.xoa_mem = 0
        join kich_thuoc kc on kc.id = ctsp.id_kich_thuoc and kc.xoa_mem = 0
        left join anh_chi_tiet_san_pham a
            on a.id_chi_tiet_san_pham = ctsp.id
           and a.la_anh_dai_dien = 1
           and a.xoa_mem = 0
        where ctsp.xoa_mem = 0
          and ctsp.trang_thai = 1
        order by ctsp.id desc
    """, nativeQuery = true)
    List<CtspBanHangView> findBanHang();

    // ================= POS SAFE STOCK UPDATE =================
    // Trừ tồn có điều kiện để chống race (bán cùng lúc).
    // Return 1 = trừ tồn OK, 0 = không đủ tồn hoặc CTSP không hợp lệ.
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
}
