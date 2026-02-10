package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    List<PhieuGiamGia> findAllByXoaMemFalseOrderByIdDesc();

    Optional<PhieuGiamGia> findByIdAndXoaMemFalse(Integer id);

    // ✅ DB xác nhận: so_luong_su_dung là "số lượt còn lại" -> dùng 1 lần thì -1
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
}
