package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.constants.TrangThaiGiaoCa;
import com.example.datn_sevenstrike.entity.GiaoCa;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.repository.GiaoCaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GiaoCaGuardService {

    private final GiaoCaRepository giaoCaRepo;

    public GiaoCa batBuocCoCaDangMoVaDaXacNhan(Integer idNhanVien) {
        if (idNhanVien == null) throw new BadRequestEx("Thiếu id nhân viên.");

        GiaoCa gc = giaoCaRepo
                .findFirstByIdNhanVienAndXoaMemFalseAndTrangThaiAndThoiGianKetCaIsNullOrderByIdDesc(
                        idNhanVien, TrangThaiGiaoCa.DANG_HOAT_DONG.code
                )
                .orElseThrow(() -> new BadRequestEx("Bạn chưa vào ca nên không thể thực hiện chức năng này."));

        if (!Boolean.TRUE.equals(gc.getDaXacNhanTienDauCa())) {
            throw new BadRequestEx("Bạn chưa xác nhận tiền đầu ca nên không thể thực hiện chức năng này.");
        }

        return gc;
    }
}
