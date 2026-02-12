// File: src/main/java/com/example/datn_sevenstrike/repository/PhieuGiamGiaCaNhanRepository.java
package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGiaCaNhan;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, Integer> {

    List<PhieuGiamGiaCaNhan> findAllByXoaMemFalseOrderByIdDesc();
    Optional<PhieuGiamGiaCaNhan> findByIdAndXoaMemFalse(Integer id);

    boolean existsByIdKhachHangAndIdPhieuGiamGiaAndXoaMemFalse(Integer idKhachHang, Integer idPhieuGiamGia);

    List<PhieuGiamGiaCaNhan> findAllByIdKhachHangAndXoaMemFalseOrderByIdDesc(Integer idKhachHang);
    List<PhieuGiamGiaCaNhan> findAllByIdKhachHangAndDaSuDungFalseAndXoaMemFalseOrderByIdDesc(Integer idKhachHang);

    List<PhieuGiamGiaCaNhan> findAllByIdPhieuGiamGiaAndXoaMemFalseOrderByIdDesc(Integer idPhieuGiamGia);

    // ✅ NEW: lấy cả bản ghi đã xóa mềm (để preserve da_gui_mail/ngay_gui_mail khi re-add khách)
    List<PhieuGiamGiaCaNhan> findAllByIdPhieuGiamGiaOrderByIdDesc(Integer idPhieuGiamGia);

    // ✅ FETCH JOIN: tránh lỗi LAZY khi map demo field
    @Query("""
        select x
          from PhieuGiamGiaCaNhan x
          left join fetch x.khachHang
          left join fetch x.phieuGiamGia
         where x.idKhachHang = :idKhachHang
           and x.xoaMem = false
         order by x.id desc
    """)
    List<PhieuGiamGiaCaNhan> findAllByIdKhachHangFetch(@Param("idKhachHang") Integer idKhachHang);

    @Query("""
        select x
          from PhieuGiamGiaCaNhan x
          left join fetch x.khachHang
          left join fetch x.phieuGiamGia
         where x.idKhachHang = :idKhachHang
           and x.daSuDung = false
           and x.xoaMem = false
         order by x.id desc
    """)
    List<PhieuGiamGiaCaNhan> findAllByIdKhachHangAvailableFetch(@Param("idKhachHang") Integer idKhachHang);

    // ✅ SOFT DELETE BẰNG QUERY
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PhieuGiamGiaCaNhan x
           set x.xoaMem = true
         where x.idPhieuGiamGia = :voucherId
           and x.xoaMem = false
    """)
    int softDeleteAliveByVoucherId(@Param("voucherId") Integer voucherId);

    // ✅ POS: mark đã dùng (chỉ khi đúng khách & chưa dùng)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PhieuGiamGiaCaNhan x
           set x.daSuDung = true
         where x.id = :id
           and x.idKhachHang = :khachHangId
           and x.xoaMem = false
           and x.daSuDung = false
    """)
    int markUsedNeuHopLe(@Param("id") Integer id, @Param("khachHangId") Integer khachHangId);

    // ✅ FE: lấy danh sách KH đã gửi mail theo voucher
    @Query("""
        select x.idKhachHang
          from PhieuGiamGiaCaNhan x
         where x.idPhieuGiamGia = :voucherId
           and x.xoaMem = false
           and x.daGuiMail = true
    """)
    List<Integer> findAllIdKhachHangDaGuiByVoucherId(@Param("voucherId") Integer voucherId);

    // ✅ NEW: reserve "đã gửi" để chặn double click / request song song
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PhieuGiamGiaCaNhan x
           set x.daGuiMail = true,
               x.ngayGuiMail = :thoiGianGui
         where x.idPhieuGiamGia = :voucherId
           and x.idKhachHang = :khachHangId
           and x.xoaMem = false
           and x.daGuiMail = false
    """)
    int markDaGuiMailNeuChuaGui(
            @Param("voucherId") Integer voucherId,
            @Param("khachHangId") Integer khachHangId,
            @Param("thoiGianGui") LocalDateTime thoiGianGui
    );

    // ✅ NEW: rollback nếu gửi mail lỗi (chỉ rollback đúng lần reserve đó)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PhieuGiamGiaCaNhan x
           set x.daGuiMail = false,
               x.ngayGuiMail = null
         where x.idPhieuGiamGia = :voucherId
           and x.idKhachHang = :khachHangId
           and x.xoaMem = false
           and x.daGuiMail = true
           and x.ngayGuiMail = :thoiGianGui
    """)
    int rollbackDaGuiMailNeuGuiLoi(
            @Param("voucherId") Integer voucherId,
            @Param("khachHangId") Integer khachHangId,
            @Param("thoiGianGui") LocalDateTime thoiGianGui
    );
}
