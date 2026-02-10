package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonResponse {

    private Integer id;

    private Integer idKhachHang;
    private Integer idNhanVien;
    private String tenNhanVien;

    private Integer idPhieuGiamGia;
    private Integer idPhieuGiamGiaCaNhan;

    private String maHoaDon;

    private Boolean loaiDon;

    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private BigDecimal tongTienGiam;

    private String tenKhachHang;
    private String diaChiKhachHang;
    private String soDienThoaiKhachHang;
    private String emailKhachHang;

    private Integer trangThaiHienTai;

    private String trangThaiLabel;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayThanhToan;

    private String ghiChu;

    private Boolean xoaMem;

    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;

    private List<HoaDonChiTietResponse> chiTietHoaDon;

    // 🔥 CONSTRUCTOR CHUẨN KHỚP 100% VỚI JPQL CỦA ANH
    public HoaDonResponse(
            Integer id,
            Integer idKhachHang,
            Integer idNhanVien,
            String tenNhanVien,
            Integer idPhieuGiamGia,
            Integer idPhieuGiamGiaCaNhan,
            String maHoaDon,
            Boolean loaiDon,
            BigDecimal phiVanChuyen,
            BigDecimal tongTien,
            BigDecimal tongTienSauGiam,
            BigDecimal tongTienGiam,
            String tenKhachHang,
            String diaChiKhachHang,
            String soDienThoaiKhachHang,
            String emailKhachHang,
            Integer trangThaiHienTai,
            String trangThaiLabel,
            LocalDateTime ngayTao,
            LocalDateTime ngayThanhToan,
            String ghiChu,
            Boolean xoaMem,
            Integer nguoiTao,
            LocalDateTime ngayCapNhat,
            Integer nguoiCapNhat
    ) {
        this.id = id;
        this.idKhachHang = idKhachHang;
        this.idNhanVien = idNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.idPhieuGiamGia = idPhieuGiamGia;
        this.idPhieuGiamGiaCaNhan = idPhieuGiamGiaCaNhan;
        this.maHoaDon = maHoaDon;
        this.loaiDon = loaiDon;
        this.phiVanChuyen = phiVanChuyen;
        this.tongTien = tongTien;
        this.tongTienSauGiam = tongTienSauGiam;
        this.tongTienGiam = tongTienGiam;
        this.tenKhachHang = tenKhachHang;
        this.diaChiKhachHang = diaChiKhachHang;
        this.soDienThoaiKhachHang = soDienThoaiKhachHang;
        this.emailKhachHang = emailKhachHang;
        this.trangThaiHienTai = trangThaiHienTai;
        this.trangThaiLabel = trangThaiLabel;
        this.ngayTao = ngayTao;
        this.ngayThanhToan = ngayThanhToan;
        this.ghiChu = ghiChu;
        this.xoaMem = xoaMem;
        this.nguoiTao = nguoiTao;
        this.ngayCapNhat = ngayCapNhat;
        this.nguoiCapNhat = nguoiCapNhat;
    }

    // Constructor rút gọn (nếu anh đang dùng ở chỗ khác)
    public HoaDonResponse(
            Integer id,
            Integer idKhachHang,
            Integer idNhanVien,
            String tenNhanVien,
            String maHoaDon,
            Boolean loaiDon,
            BigDecimal tongTien,
            Integer trangThaiHienTai,
            LocalDateTime ngayTao
    ) {
        this.id = id;
        this.idKhachHang = idKhachHang;
        this.idNhanVien = idNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.maHoaDon = maHoaDon;
        this.loaiDon = loaiDon;
        this.tongTien = tongTien;
        this.trangThaiHienTai = trangThaiHienTai;
        this.ngayTao = ngayTao;
    }
}
