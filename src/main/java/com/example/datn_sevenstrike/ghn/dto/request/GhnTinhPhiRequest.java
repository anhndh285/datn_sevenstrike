package com.example.datn_sevenstrike.ghn.dto.request;

import lombok.Data;

@Data
public class GhnTinhPhiRequest {

    // nơi nhận (BẮT BUỘC)
    private Integer toDistrictId;
    private String toWardCode;

    // nơi gửi (OPTIONAL: nếu không truyền sẽ auto lấy từ GHN shop/all hoặc từ application)
    private Integer fromDistrictId;
    private String fromWardCode;

    // gói hàng (OPTIONAL)
    private Integer tongCanNang; // gram
    private Integer dai; // cm
    private Integer rong; // cm
    private Integer cao; // cm

    private Long tongGiaTriHang; // insurance_value (optional)

    // dịch vụ (OPTIONAL) - nếu null sẽ tự pick theo available-services
    private Integer serviceId;
}