package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamResponse {

    private Integer id;

    private Integer idSanPham;
    private Integer idMauSac;
    private Integer idKichThuoc;
    private Integer idLoaiSan;
    private Integer idFormChan;

    private String maChiTietSanPham;

    private Integer soLuong;
    private BigDecimal giaNiemYet;
    private BigDecimal giaBan;

    private Boolean trangThai;
    private String ghiChu;

    private Boolean xoaMem;

    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;

    // tiện demo (để list biến thể theo SP/CTSP)
    private String maSanPham;
    private String tenSanPham;

    private String maMauSac;
    private String tenMauSac;
    private String maMauHex;

    private String maKichThuoc;
    private String tenKichThuoc;

    private String maLoaiSan;
    private String tenLoaiSan;

    private String maFormChan;
    private String tenFormChan;

    // tiện demo thumbnail CTSP
    private String duongDanAnhDaiDien;
}
