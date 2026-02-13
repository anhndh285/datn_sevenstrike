package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.GanNhanVienVaoLichRequest;
import com.example.datn_sevenstrike.dto.request.LichLamViecRequest;
import com.example.datn_sevenstrike.dto.response.LichLamViecResponse;
import com.example.datn_sevenstrike.dto.response.NhanVienTrongCaResponse;
import com.example.datn_sevenstrike.entity.CaLam;
import com.example.datn_sevenstrike.entity.LichLamViec;
import com.example.datn_sevenstrike.entity.LichLamViecNhanVien;
import com.example.datn_sevenstrike.exception.NgoaiLeDuLieuKhongHopLe;
import com.example.datn_sevenstrike.exception.NgoaiLeKhongTimThay;
import com.example.datn_sevenstrike.repository.CaLamRepository;
import com.example.datn_sevenstrike.repository.LichLamViecNhanVienRepository;
import com.example.datn_sevenstrike.repository.LichLamViecRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LichLamViecService {

    private final CaLamRepository caLamRepo;
    private final LichLamViecRepository lichRepo;
    private final LichLamViecNhanVienRepository llvnvRepo;

    public List<LichLamViecResponse> range(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null) {
            throw new NgoaiLeDuLieuKhongHopLe("Khoảng thời gian không hợp lệ.");
        }
        if (denNgay.isBefore(tuNgay)) {
            throw new NgoaiLeDuLieuKhongHopLe("Ngày kết thúc không được nhỏ hơn ngày bắt đầu.");
        }

        List<LichLamViec> ds = lichRepo.findByXoaMemFalseAndNgayLamBetweenOrderByNgayLamAscIdAsc(tuNgay, denNgay);
        if (ds.isEmpty()) return Collections.emptyList();

        // map ca_lam để lấy tên ca
        Map<Integer, CaLam> caMap = caLamRepo.findAll().stream()
                .filter(ca -> !Boolean.TRUE.equals(ca.getXoaMem()))
                .collect(Collectors.toMap(CaLam::getId, ca -> ca, (a, b) -> a));

        return ds.stream().map(llv -> {
            String tenCa = caMap.get(llv.getIdCaLam()) == null ? null : caMap.get(llv.getIdCaLam()).getTenCa();
            List<NhanVienTrongCaResponse> nhanViens = nhanVienTrongCa(llv.getId());
            return LichLamViecResponse.builder()
                    .id(llv.getId())
                    .idCaLam(llv.getIdCaLam())
                    .tenCa(tenCa)
                    .ngayLam(llv.getNgayLam())
                    .ghiChu(llv.getGhiChu())
                    .nhanViens(nhanViens)
                    .build();
        }).collect(Collectors.toList());
    }

    public LichLamViecResponse one(Integer id) {
        LichLamViec llv = lichRepo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy lịch làm việc."));
        return range(llv.getNgayLam(), llv.getNgayLam()).stream()
                .findFirst()
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy lịch làm việc."));
    }

    @Transactional
    public LichLamViecResponse create(LichLamViecRequest req) {
        CaLam ca = caLamRepo.findById(req.getIdCaLam()).orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy ca làm."));
        if (Boolean.TRUE.equals(ca.getXoaMem())) throw new NgoaiLeKhongTimThay("Không tìm thấy ca làm.");

        LichLamViec llv = new LichLamViec();
        llv.setIdCaLam(req.getIdCaLam());
        llv.setNgayLam(req.getNgayLam());
        llv.setGhiChu(req.getGhiChu());
        llv.setXoaMem(false);

        LichLamViec saved = lichRepo.save(llv);
        return one(saved.getId());
    }

    @Transactional
    public LichLamViecResponse update(Integer id, LichLamViecRequest req) {
        LichLamViec llv = lichRepo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy lịch làm việc."));

        CaLam ca = caLamRepo.findById(req.getIdCaLam()).orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy ca làm."));
        if (Boolean.TRUE.equals(ca.getXoaMem())) throw new NgoaiLeKhongTimThay("Không tìm thấy ca làm.");

        llv.setIdCaLam(req.getIdCaLam());
        llv.setNgayLam(req.getNgayLam());
        llv.setGhiChu(req.getGhiChu());
        llv.setNgayCapNhat(LocalDateTime.now());

        lichRepo.save(llv);
        return one(id);
    }

    @Transactional
    public LichLamViecResponse ganNhanVien(Integer idLichLamViec, GanNhanVienVaoLichRequest req) {
        LichLamViec llv = lichRepo.findByIdAndXoaMemFalse(idLichLamViec)
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy lịch làm việc."));

        for (Integer idNv : req.getIdNhanViens()) {
            if (idNv == null) continue;

            Optional<LichLamViecNhanVien> opt = llvnvRepo.findByIdLichLamViecAndIdNhanVien(idLichLamViec, idNv);
            if (opt.isPresent()) {
                LichLamViecNhanVien e = opt.get();
                if (Boolean.TRUE.equals(e.getXoaMem())) {
                    e.setXoaMem(false);
                    e.setNgayCapNhat(LocalDateTime.now());
                    llvnvRepo.save(e);
                }
            } else {
                LichLamViecNhanVien e = new LichLamViecNhanVien();
                e.setIdLichLamViec(llv.getId());
                e.setIdNhanVien(idNv);
                e.setXoaMem(false);
                llvnvRepo.save(e);
            }
        }

        return one(idLichLamViec);
    }

    public List<NhanVienTrongCaResponse> nhanVienTrongCa(Integer idLichLamViec) {
        // trả account (ten_tai_khoan), không trả tên nhân viên
        return llvnvRepo.nhanVienTrongCa(idLichLamViec).stream()
                .map(p -> NhanVienTrongCaResponse.builder()
                        .idNhanVien(p.getIdNhanVien())
                        .tenTaiKhoan(p.getTenTaiKhoan())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Integer id) {
        LichLamViec llv = lichRepo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy lịch làm việc."));

        llv.setXoaMem(true);
        llv.setNgayCapNhat(LocalDateTime.now());
        lichRepo.save(llv);

        // soft delete toàn bộ gán nhân viên để sạch dữ liệu
        List<LichLamViecNhanVien> ds = llvnvRepo.findByIdLichLamViecAndXoaMemFalse(id);
        for (LichLamViecNhanVien e : ds) {
            e.setXoaMem(true);
            e.setNgayCapNhat(LocalDateTime.now());
        }
        llvnvRepo.saveAll(ds);
    }
}

