package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vw_san_pham_thumb")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SanPhamThumbView {

    @Id
    @Column(name = "id_san_pham")
    private Integer idSanPham;

    @Column(name = "ma_san_pham", length = 7)
    private String maSanPham;

    @Column(name = "ten_san_pham", length = 255)
    private String tenSanPham;

    @Column(name = "trang_thai_kinh_doanh")
    private Boolean trangThaiKinhDoanh;

    @Column(name = "id_ctsp_thumb")
    private Integer idCtspThumb;

    @Column(name = "duong_dan_anh_thumb", length = 255)
    private String duongDanAnhThumb;
}
