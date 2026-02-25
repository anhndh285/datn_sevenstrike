package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LichLamViecNhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LichLamViecNhanVienRepository extends JpaRepository<LichLamViecNhanVien, Integer> {

    @Query("SELECT l FROM LichLamViecNhanVien l WHERE l.lichLamViec.id = :idLich AND l.nhanVien.id = :idNv AND l.xoaMem = false")
    Optional<LichLamViecNhanVien> findByLichAndNhanVien(Integer idLich, Integer idNv);

    List<LichLamViecNhanVien> findAllByXoaMemFalseOrderByIdDesc();

    Page<LichLamViecNhanVien> findAllByXoaMemFalse(Pageable pageable);

    Optional<LichLamViecNhanVien> findByIdAndXoaMemFalse(Integer id);

    @Query("SELECT l FROM LichLamViecNhanVien l " +
            "WHERE l.nhanVien.id = :idNv " +
            "AND l.lichLamViec.ngayLam = :ngayLam " +
            "AND l.xoaMem = false " +
            "ORDER BY l.id DESC")
    List<LichLamViecNhanVien> findAllByNhanVienAndNgayLam(@Param("idNv") Integer idNhanVien, @Param("ngayLam") LocalDate ngayLam);

    @Query("SELECT COUNT(l) > 0 FROM LichLamViecNhanVien l WHERE l.lichLamViec.id = :idLich AND l.nhanVien.id = :idNv AND l.xoaMem = false")
    boolean existsByLichLamViecAndNhanVien(@Param("idLich") Integer idLich, @Param("idNv") Integer idNv);

}