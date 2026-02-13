package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.CaLamRequest;
import com.example.datn_sevenstrike.dto.response.CaLamResponse;
import com.example.datn_sevenstrike.entity.CaLam;
import com.example.datn_sevenstrike.exception.NgoaiLeDuLieuKhongHopLe;
import com.example.datn_sevenstrike.exception.NgoaiLeKhongTimThay;
import com.example.datn_sevenstrike.repository.CaLamRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaLamService {

    private final CaLamRepository repo;

    public List<CaLamResponse> all() {
        return repo.findByXoaMemFalseOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CaLamResponse one(Integer id) {
        CaLam e = repo.findById(id).orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy ca làm."));
        if (Boolean.TRUE.equals(e.getXoaMem())) throw new NgoaiLeKhongTimThay("Không tìm thấy ca làm.");
        return toResponse(e);
    }

    public CaLamResponse create(CaLamRequest req) {
        if (req.getGioKetThuc().compareTo(req.getGioBatDau()) <= 0) {
            throw new NgoaiLeDuLieuKhongHopLe("Giờ kết thúc phải lớn hơn giờ bắt đầu.");
        }
        CaLam e = new CaLam();
        e.setTenCa(req.getTenCa().trim());
        e.setGioBatDau(req.getGioBatDau());
        e.setGioKetThuc(req.getGioKetThuc());
        e.setMoTa(req.getMoTa());
        e.setTrangThai(req.getTrangThai() == null ? true : req.getTrangThai());
        e.setXoaMem(false);
        return toResponse(repo.save(e));
    }

    public CaLamResponse update(Integer id, CaLamRequest req) {
        CaLam e = repo.findById(id).orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy ca làm."));
        if (Boolean.TRUE.equals(e.getXoaMem())) throw new NgoaiLeKhongTimThay("Không tìm thấy ca làm.");

        if (req.getGioKetThuc().compareTo(req.getGioBatDau()) <= 0) {
            throw new NgoaiLeDuLieuKhongHopLe("Giờ kết thúc phải lớn hơn giờ bắt đầu.");
        }

        e.setTenCa(req.getTenCa().trim());
        e.setGioBatDau(req.getGioBatDau());
        e.setGioKetThuc(req.getGioKetThuc());
        e.setMoTa(req.getMoTa());
        if (req.getTrangThai() != null) e.setTrangThai(req.getTrangThai());

        return toResponse(repo.save(e));
    }

    public void delete(Integer id) {
        CaLam e = repo.findById(id).orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy ca làm."));
        e.setXoaMem(true);
        repo.save(e);
    }

    private CaLamResponse toResponse(CaLam e) {
        return CaLamResponse.builder()
                .id(e.getId())
                .maCa(e.getMaCa())
                .tenCa(e.getTenCa())
                .gioBatDau(e.getGioBatDau())
                .gioKetThuc(e.getGioKetThuc())
                .moTa(e.getMoTa())
                .trangThai(e.getTrangThai())
                .build();
    }
}

