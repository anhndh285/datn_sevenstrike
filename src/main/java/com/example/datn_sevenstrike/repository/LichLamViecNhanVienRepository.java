package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.LichLamViecNhanVien;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LichLamViecNhanVienRepository extends JpaRepository<LichLamViecNhanVien, Integer> {

    boolean existsByIdLichLamViecAndIdNhanVienAndXoaMemFalse(Integer idLichLamViec, Integer idNhanVien);

    List<LichLamViecNhanVien> findByIdLichLamViecAndXoaMemFalse(Integer idLichLamViec);

    Optional<LichLamViecNhanVien> findByIdLichLamViecAndIdNhanVien(Integer idLichLamViec, Integer idNhanVien);

    interface NhanVienTrongCaProjection {
        Integer getIdNhanVien();
        String getTenTaiKhoan();
    }

    @Query(value = """
            select nv.id as idNhanVien, nv.ten_tai_khoan as tenTaiKhoan
            from lich_lam_viec_nhan_vien llvnv
            join nhan_vien nv on nv.id = llvnv.id_nhan_vien and nv.xoa_mem = 0
            where llvnv.xoa_mem = 0
              and llvnv.id_lich_lam_viec = :idLichLamViec
            order by nv.ten_tai_khoan asc
            """, nativeQuery = true)
    List<NhanVienTrongCaProjection> nhanVienTrongCa(@Param("idLichLamViec") Integer idLichLamViec);
}

