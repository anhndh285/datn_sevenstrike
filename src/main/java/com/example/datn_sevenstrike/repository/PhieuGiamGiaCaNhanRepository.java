// File: src/main/java/com/example/datn_sevenstrike/repository/PhieuGiamGiaCaNhanRepository.java
package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGiaCaNhan;
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
}
