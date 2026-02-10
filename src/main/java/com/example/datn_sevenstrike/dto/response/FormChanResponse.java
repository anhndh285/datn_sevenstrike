package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormChanResponse {
    private Integer id;
    private String maFormChan;
    private String tenFormChan;
    private Boolean trangThai;
    private Boolean xoaMem;
}
