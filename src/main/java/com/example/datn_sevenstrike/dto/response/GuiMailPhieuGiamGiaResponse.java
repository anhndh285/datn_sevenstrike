package com.example.datn_sevenstrike.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuiMailPhieuGiamGiaResponse {

    private Integer soLuongGuiThanhCong;
    private Integer soLuongBoQua;
    private List<Integer> danhSachDaGuiIds;
}
