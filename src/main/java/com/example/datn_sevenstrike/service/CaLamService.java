package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.CaLamRequest;
import com.example.datn_sevenstrike.dto.response.CaLamResponse;
import com.example.datn_sevenstrike.entity.CaLam;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.CaLamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaLamService {

    private final CaLamRepository repo;
    private final ModelMapper mapper;

    public List<CaLamResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public CaLamResponse one(Integer id) {
        CaLam e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CaLam id=" + id));
        return toResponse(e);
    }

    @Transactional
    public CaLamResponse create(CaLamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        CaLam e = mapper.map(req, CaLam.class);
        e.setId(null);
        applyDefaults(e, true);
        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public CaLamResponse update(Integer id, CaLamRequest req) {
        CaLam db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CaLam id=" + id));

        mapper.map(req, db); // Map đè dữ liệu mới
        applyDefaults(db, false);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        CaLam db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CaLam id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void applyDefaults(CaLam e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
    }

    private void validate(CaLam e) {
        if (e.getTenCa() == null || e.getTenCa().isBlank()) throw new BadRequestEx("Tên ca không được để trống");
        if (e.getGioBatDau() == null || e.getGioKetThuc() == null) throw new BadRequestEx("Thiếu giờ bắt đầu/kết thúc");
        if (e.getGioBatDau().isAfter(e.getGioKetThuc())) throw new BadRequestEx("Giờ bắt đầu phải trước giờ kết thúc");
    }

    private CaLamResponse toResponse(CaLam e) {
        return mapper.map(e, CaLamResponse.class);
    }
}