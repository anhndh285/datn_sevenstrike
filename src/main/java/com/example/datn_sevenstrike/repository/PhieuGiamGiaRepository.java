// File: src/main/java/com/example/datn_sevenstrike/repository/PhieuGiamGiaRepository.java
package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    List<PhieuGiamGia> findAllByXoaMemFalseOrderByIdDesc();

    Optional<PhieuGiamGia> findByIdAndXoaMemFalse(Integer id);

    @Query("""
        select p
        from PhieuGiamGia p
        where p.xoaMem = false
          and (
                :keyword is null
                or lower(p.maPhieuGiamGia) like lower(concat('%', :keyword, '%'))
                or lower(p.tenPhieuGiamGia) like lower(concat('%', :keyword, '%'))
              )
          and (:ngayBatDau is null or p.ngayBatDau >= :ngayBatDau)
          and (:ngayKetThuc is null or p.ngayKetThuc <= :ngayKetThuc)
          and (:trangThai is null or p.trangThai = :trangThai)
        order by p.id desc
    """)
    List<PhieuGiamGia> search(
            @Param("keyword") String keyword,
            @Param("ngayBatDau") LocalDate ngayBatDau,
            @Param("ngayKetThuc") LocalDate ngayKetThuc,
            @Param("trangThai") Boolean trangThai
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update phieu_giam_gia
           set so_luong_su_dung = so_luong_su_dung - 1
         where id = :id
           and xoa_mem = 0
           and trang_thai = 1
           and so_luong_su_dung >= 1
    """, nativeQuery = true)
    int consumeNeuCon(@Param("id") Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update phieu_giam_gia
           set so_luong_su_dung = so_luong_su_dung + 1
         where id = :id
           and xoa_mem = 0
    """, nativeQuery = true)
    int restoreOne(@Param("id") Integer id);
}