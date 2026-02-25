package com.example.datn_sevenstrike.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LichLamViecNhanVienRequest {
    private Integer idLichLamViec;
    private Integer idNhanVien;
    private Integer nguoiTao;
}