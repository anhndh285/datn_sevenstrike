package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongBaoTongQuanResponse {
    private Integer idNhanVien;
    private long soThongBaoChuaDoc;
    private long soThongBaoChuaXuLy;
}
