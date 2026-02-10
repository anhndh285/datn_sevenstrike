package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.KichThuocRequest;
import com.example.datn_sevenstrike.dto.response.KichThuocResponse;
import com.example.datn_sevenstrike.entity.KichThuoc;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.KichThuocRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KichThuocService {

    private final KichThuocRepository repo;
    private final ModelMapper mapper;

    public List<KichThuocResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    // ✅ cho combobox: chỉ lấy đang hoạt động
    public List<KichThuocResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public KichThuocResponse one(Integer id) {
        KichThuoc e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KichThuoc id=" + id));
        return toResponse(e);
    }

    @Transactional
    public KichThuocResponse create(KichThuocRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        KichThuoc e = mapper.map(req, KichThuoc.class);
        e.setId(null);

        applyDefaults(e);
        validateCreate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public KichThuocResponse update(Integer id, KichThuocRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        KichThuoc db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KichThuoc id=" + id));

        if (req.getTenKichThuoc() != null) db.setTenKichThuoc(req.getTenKichThuoc());
        if (req.getGiaTriKichThuoc() != null) db.setGiaTriKichThuoc(req.getGiaTriKichThuoc());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validateUpdate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        KichThuoc db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KichThuoc id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    // ================== PRIVATE ==================

    private void applyDefaults(KichThuoc e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);

        if (e.getTenKichThuoc() != null) e.setTenKichThuoc(e.getTenKichThuoc().trim());
    }

    private void validateCreate(KichThuoc e) {
        validateCommon(e);

        // unique index alive cho gia_tri_kich_thuoc
        if (e.getGiaTriKichThuoc() != null && repo.existsByGiaTriKichThuocAndXoaMemFalse(e.getGiaTriKichThuoc())) {
            throw new BadRequestEx("Giá trị kích thước đã tồn tại: " + e.getGiaTriKichThuoc());
        }
    }

    private void validateUpdate(KichThuoc e) {
        validateCommon(e);

        if (e.getGiaTriKichThuoc() != null
                && repo.existsByGiaTriKichThuocAndXoaMemFalseAndIdNot(e.getGiaTriKichThuoc(), e.getId())) {
            throw new BadRequestEx("Giá trị kích thước đã tồn tại: " + e.getGiaTriKichThuoc());
        }
    }

    private void validateCommon(KichThuoc e) {
        if (e.getTenKichThuoc() == null || e.getTenKichThuoc().isBlank())
            throw new BadRequestEx("Thiếu ten_kich_thuoc");

        if (e.getGiaTriKichThuoc() != null) {
            BigDecimal min = new BigDecimal("38.0");
            BigDecimal max = new BigDecimal("45.0");
            if (e.getGiaTriKichThuoc().compareTo(min) < 0 || e.getGiaTriKichThuoc().compareTo(max) > 0) {
                throw new BadRequestEx("gia_tri_kich_thuoc phải trong khoảng 38.0 đến 45.0");
            }
        }
    }

    private KichThuocResponse toResponse(KichThuoc e) {
        return mapper.map(e, KichThuocResponse.class);
    }
}
