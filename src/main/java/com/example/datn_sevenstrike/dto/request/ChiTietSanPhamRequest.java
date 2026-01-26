package com.example.datn_sevenstrike.dto.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ChiTietSanPhamRequest {
    private Integer idSanPham;
    private Integer idMauSac;
    private Integer idKichThuoc;
    private Integer idLoaiSan;
    private Integer idFormChan;

    private Integer soLuong;
    private BigDecimal giaNiemYet;
    private BigDecimal giaBan;

    private Boolean trangThai;
    private String ghiChu;
}
