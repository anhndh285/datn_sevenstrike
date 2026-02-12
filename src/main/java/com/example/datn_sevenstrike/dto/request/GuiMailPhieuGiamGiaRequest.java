package com.example.datn_sevenstrike.dto.request;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuiMailPhieuGiamGiaRequest {

    private List<Integer> idKhachHangs;
}
