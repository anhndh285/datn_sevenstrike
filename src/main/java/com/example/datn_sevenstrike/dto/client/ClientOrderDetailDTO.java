package com.example.datn_sevenstrike.dto.client;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientOrderDetailDTO {
    private Integer id;
    private String maHoaDon;
    private LocalDateTime ngayTao;
    private String trangThai;        // Label (vd: "Đang vận chuyển")
    private Integer trangThaiHienTai; // Numeric code 1–5

    // Receiver Info
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChi;

    // Costs
    private BigDecimal tamTinh;
    private BigDecimal phiVanChuyen;
    private BigDecimal giamGia;
    private BigDecimal tongTien;

    // Lists
    private List<ClientOrderItemDTO> items;
    private List<ClientTimelineDTO> timeline;

    // Payment
    private boolean daThanhToan;
    private String phuongThucThanhToan;
}