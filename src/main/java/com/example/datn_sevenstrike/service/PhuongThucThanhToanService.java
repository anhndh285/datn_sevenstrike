package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.PhuongThucThanhToanRequest;
import com.example.datn_sevenstrike.dto.response.PhuongThucThanhToanResponse;
import com.example.datn_sevenstrike.entity.PhuongThucThanhToan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.PhuongThucThanhToanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhuongThucThanhToanService {

    private final PhuongThucThanhToanRepository repo;
    private final ModelMapper mapper;

    public List<PhuongThucThanhToanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<PhuongThucThanhToanResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public PhuongThucThanhToanResponse one(Integer id) {
        PhuongThucThanhToan e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhuongThucThanhToan id=" + id));
        return toResponse(e);
    }

    @Transactional
    public PhuongThucThanhToanResponse create(PhuongThucThanhToanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        PhuongThucThanhToan e = mapper.map(req, PhuongThucThanhToan.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public PhuongThucThanhToanResponse update(Integer id, PhuongThucThanhToanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        PhuongThucThanhToan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhuongThucThanhToan id=" + id));

        if (req.getTenPhuongThucThanhToan() != null) db.setTenPhuongThucThanhToan(req.getTenPhuongThucThanhToan());
        if (req.getNhaCungCap() != null) db.setNhaCungCap(req.getNhaCungCap());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        PhuongThucThanhToan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhuongThucThanhToan id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void applyDefaults(PhuongThucThanhToan e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);

        if (e.getTenPhuongThucThanhToan() != null) e.setTenPhuongThucThanhToan(e.getTenPhuongThucThanhToan().trim());
        if (e.getNhaCungCap() != null) e.setNhaCungCap(e.getNhaCungCap().trim());
    }

    private void validate(PhuongThucThanhToan e) {
        if (e.getTenPhuongThucThanhToan() == null || e.getTenPhuongThucThanhToan().isBlank())
            throw new BadRequestEx("Thiếu ten_phuong_thuc_thanh_toan");
    }

    private PhuongThucThanhToanResponse toResponse(PhuongThucThanhToan e) {
        return mapper.map(e, PhuongThucThanhToanResponse.class);
    }
}
