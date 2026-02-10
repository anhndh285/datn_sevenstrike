package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuongHieuResponse {
    private Integer id;
    private String maThuongHieu;
    private String tenThuongHieu;
    private Boolean trangThai;
    private Boolean xoaMem;
}
