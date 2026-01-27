package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.DiaChiKhachHang;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang, Integer> {

    Optional<DiaChiKhachHang> findByIdAndXoaMemFalse(Integer id);

    List<DiaChiKhachHang> findAllByXoaMemFalseOrderByIdDesc();

    // ✅ Lấy danh sách địa chỉ của 1 KH (để show / chọn default)
    List<DiaChiKhachHang> findAllByIdKhachHangAndXoaMemFalseOrderByMacDinhDescIdDesc(Integer idKhachHang);

    // ✅ Kiểm tra KH đã có địa chỉ mặc định chưa
    Optional<DiaChiKhachHang> findFirstByIdKhachHangAndMacDinhTrueAndXoaMemFalse(Integer idKhachHang);

    // ✅ Khi set 1 địa chỉ làm mặc định => gỡ mặc định các địa chỉ khác
    @Modifying
    @Query("""
        update DiaChiKhachHang d
           set d.macDinh = false
         where d.idKhachHang = :idKhachHang
           and d.xoaMem = false
           and d.id <> :keepId
           and d.macDinh = true
    """)
    int unsetDefaultOthers(@Param("idKhachHang") Integer idKhachHang,
                           @Param("keepId") Integer keepId);
}
