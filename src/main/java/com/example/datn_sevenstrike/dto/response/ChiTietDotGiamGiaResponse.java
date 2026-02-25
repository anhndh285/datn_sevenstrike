package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDotGiamGiaResponse {

    private Integer id;

    private Integer idDotGiamGia;
    private Integer idChiTietSanPham;

    private Integer soLuongApDung;
    private BigDecimal giaTriGiamRieng;
    private BigDecimal soTienGiamToiDaRieng;

    private Boolean trangThai;
    private String ghiChu;
    private Boolean xoaMem;

    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;

    // tiện demo
    private String maDotGiamGia;
    private String tenDotGiamGia;

    private String maChiTietSanPham;
    private String maSanPham;
    private String tenSanPham;

    // =========================
    // BỔ SUNG cho POS (badge + tính giá)
    // =========================
    private BigDecimal giaTriGiamGiaDot;     // % của đợt
    private Integer mucUuTienDot;
    private LocalDate ngayBatDauDot;
    private LocalDate ngayKetThucDot;

    private BigDecimal giaTriGiamApDung;    // % cuối cùng áp dụng (ưu tiên gia_tri_giam_rieng nếu có)
    private BigDecimal soTienGiamToiDaApDung; // hiện tại lấy từ so_tien_giam_toi_da_rieng (nếu có)
}
