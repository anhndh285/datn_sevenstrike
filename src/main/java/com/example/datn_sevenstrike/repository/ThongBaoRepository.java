package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ThongBao;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {

    List<ThongBao> findTop50ByIdNhanVienNhanAndXoaMemFalseOrderByThoiGianTaoDesc(Integer idNhanVienNhan);

    List<ThongBao> findTop50ByIdNhanVienNhanAndDaDocFalseAndXoaMemFalseOrderByThoiGianTaoDesc(Integer idNhanVienNhan);

    long countByIdNhanVienNhanAndDaDocFalseAndXoaMemFalse(Integer idNhanVienNhan);

    long countByIdNhanVienNhanAndDaXuLyFalseAndXoaMemFalse(Integer idNhanVienNhan);

    Optional<ThongBao> findByIdAndIdNhanVienNhanAndXoaMemFalse(Integer id, Integer idNhanVienNhan);

    boolean existsByIdNhanVienNhanAndKhoaChongTrungAndXoaMemFalse(Integer idNhanVienNhan, String khoaChongTrung);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ThongBao tb
           set tb.daDoc = true,
               tb.thoiGianDoc = :thoiDiem,
               tb.thoiGianCapNhat = :thoiDiem,
               tb.nguoiCapNhat = :nguoiCapNhat
         where tb.id = :id
           and tb.idNhanVienNhan = :idNhanVienNhan
           and tb.xoaMem = false
           and tb.daDoc = false
    """)
    int danhDauDaDoc(
            @Param("id") Integer id,
            @Param("idNhanVienNhan") Integer idNhanVienNhan,
            @Param("nguoiCapNhat") Integer nguoiCapNhat,
            @Param("thoiDiem") LocalDateTime thoiDiem
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ThongBao tb
           set tb.daDoc = true,
               tb.thoiGianDoc = :thoiDiem,
               tb.thoiGianCapNhat = :thoiDiem,
               tb.nguoiCapNhat = :nguoiCapNhat
         where tb.idNhanVienNhan = :idNhanVienNhan
           and tb.xoaMem = false
           and tb.daDoc = false
    """)
    int danhDauTatCaDaDoc(
            @Param("idNhanVienNhan") Integer idNhanVienNhan,
            @Param("nguoiCapNhat") Integer nguoiCapNhat,
            @Param("thoiDiem") LocalDateTime thoiDiem
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ThongBao tb
           set tb.daXuLy = true,
               tb.thoiGianXuLy = :thoiDiem,
               tb.thoiGianCapNhat = :thoiDiem,
               tb.nguoiCapNhat = :nguoiCapNhat
         where tb.id = :id
           and tb.idNhanVienNhan = :idNhanVienNhan
           and tb.xoaMem = false
           and tb.daXuLy = false
    """)
    int danhDauDaXuLy(
            @Param("id") Integer id,
            @Param("idNhanVienNhan") Integer idNhanVienNhan,
            @Param("nguoiCapNhat") Integer nguoiCapNhat,
            @Param("thoiDiem") LocalDateTime thoiDiem
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ThongBao tb
           set tb.daXuLy = true,
               tb.thoiGianXuLy = :thoiDiem,
               tb.thoiGianCapNhat = :thoiDiem,
               tb.nguoiCapNhat = :nguoiCapNhat
         where tb.xoaMem = false
           and tb.daXuLy = false
           and tb.loaiDoiTuongLienQuan = :loaiDoiTuong
           and tb.idDoiTuongLienQuan = :idDoiTuong
           and tb.loaiThongBao in :dsLoai
    """)
    int danhDauDaXuLyTheoDoiTuongVaLoai(
            @Param("loaiDoiTuong") String loaiDoiTuong,
            @Param("idDoiTuong") Integer idDoiTuong,
            @Param("dsLoai") Collection<String> dsLoai,
            @Param("nguoiCapNhat") Integer nguoiCapNhat,
            @Param("thoiDiem") LocalDateTime thoiDiem
    );
}
