// File: src/main/java/com/example/datn_sevenstrike/dto/response/HoaDonResponse.java
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

    /**
     * ✅ DB mới: tinyint
     * 0: Tại quầy | 1: Giao hàng | 2: Online
     */
    private Integer loaiDon;

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

    private Boolean daHoanPhi;
    /**
     * 0: Tiền mặt / COD
     * 1: Chuyển khoản / VNPAY
     */
    private Integer loaiThanhToan;

    /**
     * Constructor cũ của main branch - giữ nguyên để tránh vỡ projection/service cũ
     */
    public HoaDonResponse(
            Integer id,
            Integer idKhachHang,
            Integer idNhanVien,
            String tenNhanVien,
            Integer idPhieuGiamGia,
            Integer idPhieuGiamGiaCaNhan,
            String maHoaDon,
            Integer loaiDon,
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

    /**
     * Constructor theo nhánh của Duy - giữ để tương thích nếu đã có query/service dùng
     */
    public HoaDonResponse(
            Integer id,
            Integer idKhachHang,
            Integer idNhanVien,
            String tenNhanVien,
            Integer idPhieuGiamGia,
            Integer idPhieuGiamGiaCaNhan,
            String maHoaDon,
            Integer loaiDon,
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
            Integer nguoiCapNhat,
            Boolean daHoanPhi
    ) {
        this(
                id,
                idKhachHang,
                idNhanVien,
                tenNhanVien,
                idPhieuGiamGia,
                idPhieuGiamGiaCaNhan,
                maHoaDon,
                loaiDon,
                phiVanChuyen,
                tongTien,
                tongTienSauGiam,
                tongTienGiam,
                tenKhachHang,
                diaChiKhachHang,
                soDienThoaiKhachHang,
                emailKhachHang,
                trangThaiHienTai,
                trangThaiLabel,
                ngayTao,
                ngayThanhToan,
                ghiChu,
                xoaMem,
                nguoiTao,
                ngayCapNhat,
                nguoiCapNhat
        );
        this.daHoanPhi = daHoanPhi;
    }

    /**
     * Constructor mở rộng đầy đủ cho các flow mới
     */
    public HoaDonResponse(
            Integer id,
            Integer idKhachHang,
            Integer idNhanVien,
            String tenNhanVien,
            Integer idPhieuGiamGia,
            Integer idPhieuGiamGiaCaNhan,
            String maHoaDon,
            Integer loaiDon,
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
            Integer nguoiCapNhat,
            Boolean daHoanPhi,
            Integer loaiThanhToan
    ) {
        this(
                id,
                idKhachHang,
                idNhanVien,
                tenNhanVien,
                idPhieuGiamGia,
                idPhieuGiamGiaCaNhan,
                maHoaDon,
                loaiDon,
                phiVanChuyen,
                tongTien,
                tongTienSauGiam,
                tongTienGiam,
                tenKhachHang,
                diaChiKhachHang,
                soDienThoaiKhachHang,
                emailKhachHang,
                trangThaiHienTai,
                trangThaiLabel,
                ngayTao,
                ngayThanhToan,
                ghiChu,
                xoaMem,
                nguoiTao,
                ngayCapNhat,
                nguoiCapNhat
        );
        this.daHoanPhi = daHoanPhi;
        this.loaiThanhToan = loaiThanhToan;
    }

    public HoaDonResponse(
            Integer id,
            Integer idKhachHang,
            Integer idNhanVien,
            String tenNhanVien,
            String maHoaDon,
            Integer loaiDon,
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