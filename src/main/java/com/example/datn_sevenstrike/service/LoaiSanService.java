package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LoaiSanRequest;
import com.example.datn_sevenstrike.dto.response.LoaiSanResponse;
import com.example.datn_sevenstrike.entity.LoaiSan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import com.example.datn_sevenstrike.repository.LoaiSanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoaiSanService {

    private final LoaiSanRepository repo;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ModelMapper mapper;

    public List<LoaiSanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<LoaiSanResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public LoaiSanResponse one(Integer id) {
        LoaiSan e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy LoaiSan id=" + id));
        return toResponse(e);
    }

    @Transactional
    public LoaiSanResponse create(LoaiSanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        LoaiSan e = mapper.map(req, LoaiSan.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public LoaiSanResponse update(Integer id, LoaiSanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        LoaiSan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy LoaiSan id=" + id));

        boolean activeCu = isActive(db);

        if (req.getTenLoaiSan() != null) db.setTenLoaiSan(req.getTenLoaiSan());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        LoaiSan saved = repo.save(db);
        boolean activeMoi = isActive(saved);

        if (activeCu && !activeMoi) {
            chiTietSanPhamRepository.ngungKinhDoanhTheoLoaiSan(saved.getId());
        } else if (!activeCu && activeMoi) {
            chiTietSanPhamRepository.batKinhDoanhTheoLoaiSan(saved.getId());
        }

        return toResponse(saved);
    }
    @Transactional
    public void delete(Integer id) {
        LoaiSan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy LoaiSan id=" + id));

        db.setXoaMem(true);
        db.setTrangThai(false);
        repo.save(db);

        chiTietSanPhamRepository.ngungKinhDoanhTheoLoaiSan(id);
    }

    private void applyDefaults(LoaiSan e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getTenLoaiSan() != null) e.setTenLoaiSan(e.getTenLoaiSan().trim());
    }

    private void validate(LoaiSan e) {
        if (e.getTenLoaiSan() == null || e.getTenLoaiSan().isBlank()) {
            throw new BadRequestEx("Thiếu ten_loai_san");
        }
    }

    private boolean isActive(LoaiSan e) {
        return !Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getTrangThai());
    }

    private LoaiSanResponse toResponse(LoaiSan e) {
        return mapper.map(e, LoaiSanResponse.class);
    }
}