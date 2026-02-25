package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoCaResponse {
    // 1. Thông tin định danh
    private Integer id;
    private String maGiaoCa;
    private Integer idNhanVien;
    private String tenNhanVien;

    // 2. Thông tin ca làm việc (Lấy từ LichLamViec -> CaLam)
    private Integer idLichLamViec;
    private String tenCaLam;
    private LocalTime gioBatDauCa;  // Thời gian bắt đầu chuẩn của ca (VD: 08:00)
    private LocalTime gioKetThucCa; // Thời gian kết thúc chuẩn của ca (VD: 14:00)

    // 3. Thông tin tiền nong
    private BigDecimal tienBanGiaoDuKien; // Tiền từ ca trước bàn giao sang
    private BigDecimal tienDauCaNhap;     // Tiền thực tế nhân viên xác nhận khi vào ca
    private Boolean daXacNhanTienDauCa;   // Trạng thái đã khớp tiền hay chưa

    private Integer soDonHangChoXuLy;     // Số đơn đang chờ/đang phục vụ

    // 5. Trạng thái và thời gian thực tế
    private String maCaTruoc;             // Mã giao ca của người bàn giao trước đó
    private LocalDateTime thoiGianNhanCa; // Lúc nhân viên bấm "Bắt đầu"
    private LocalDateTime thoiGianKetCa;  // Lúc nhân viên bấm "Kết thúc"
    private Integer trangThai;            // 0: Đang hoạt động, 1: Đã đóng ca
    private String ghiChu;

    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;
}