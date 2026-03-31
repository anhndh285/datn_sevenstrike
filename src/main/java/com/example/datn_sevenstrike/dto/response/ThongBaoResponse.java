package com.example.datn_sevenstrike.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongBaoResponse {
    private Integer id;
    private String maThongBao;
    private Integer idNhanVienNhan;
    private String loaiThongBao;
    private Integer mucDo;
    private String mucDoLabel;
    private String tieuDe;
    private String noiDung;
    private String loaiDoiTuongLienQuan;
    private Integer idDoiTuongLienQuan;
    private Boolean daDoc;
    private Boolean daXuLy;
    private LocalDateTime thoiGianTao;
    private LocalDateTime thoiGianDoc;
    private LocalDateTime thoiGianXuLy;
    private String duLieuBoSung;
}
