package com.example.datn_sevenstrike.dto.request;


import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GanNhanVienVaoLichRequest {

    @NotEmpty(message = "Danh sách nhân viên không được để trống.")
    private List<Integer> idNhanViens;
}
