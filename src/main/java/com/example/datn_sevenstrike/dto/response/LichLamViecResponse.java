package com.example.datn_sevenstrike.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LichLamViecResponse {
    private Integer id;
    private Integer idCaLam;
    private String tenCa;
    private LocalDate ngayLam;
    private String ghiChu;
    private List<NhanVienTrongCaResponse> nhanViens;
}

