package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.PhongCachChoiRequest;
import com.example.datn_sevenstrike.dto.response.PhongCachChoiResponse;
import com.example.datn_sevenstrike.entity.PhongCachChoi;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import com.example.datn_sevenstrike.repository.PhongCachChoiRepository;
import com.example.datn_sevenstrike.repository.SanPhamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhongCachChoiService {

    private final PhongCachChoiRepository repo;
    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ModelMapper mapper;

    public List<PhongCachChoiResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PhongCachChoiResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PhongCachChoiResponse one(Integer id) {
        PhongCachChoi e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhongCachChoi id=" + id));
        return toResponse(e);
    }

    @Transactional
    public PhongCachChoiResponse create(PhongCachChoiRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        }

        PhongCachChoi e = mapper.map(req, PhongCachChoi.class);
        e.setId(null);

        applyDefaults(e);
        validateCreate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public PhongCachChoiResponse update(Integer id, PhongCachChoiRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        }

        PhongCachChoi db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhongCachChoi id=" + id));

        boolean activeCu = isActive(db);

        if (req.getTenPhongCach() != null) {
            db.setTenPhongCach(req.getTenPhongCach());
        }
        if (req.getTrangThai() != null) {
            db.setTrangThai(req.getTrangThai());
        }
        if (req.getXoaMem() != null) {
            db.setXoaMem(req.getXoaMem());
        }

        applyDefaults(db);
        validateUpdate(db);

        PhongCachChoi saved = repo.save(db);
        boolean activeMoi = isActive(saved);

        if (activeCu && !activeMoi) {
            sanPhamRepository.ngungKinhDoanhTheoPhongCachChoi(saved.getId());
            chiTietSanPhamRepository.ngungKinhDoanhTheoPhongCachChoi(saved.getId());
        } else if (!activeCu && activeMoi) {
            sanPhamRepository.batKinhDoanhTheoPhongCachChoi(saved.getId());
            chiTietSanPhamRepository.batKinhDoanhTheoPhongCachChoi(saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        PhongCachChoi db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhongCachChoi id=" + id));

        db.setXoaMem(true);
        db.setTrangThai(false);
        repo.save(db);

        sanPhamRepository.ngungKinhDoanhTheoPhongCachChoi(id);
        chiTietSanPhamRepository.ngungKinhDoanhTheoPhongCachChoi(id);
    }

    private void applyDefaults(PhongCachChoi e) {
        if (e.getXoaMem() == null) {
            e.setXoaMem(false);
        }
        if (e.getTrangThai() == null) {
            e.setTrangThai(true);
        }
        if (e.getTenPhongCach() != null) {
            e.setTenPhongCach(e.getTenPhongCach().trim());
        }
    }

    private void validateCreate(PhongCachChoi e) {
        validateCommon(e);
        if (repo.existsByTenPhongCachIgnoreCaseAndXoaMemFalse(e.getTenPhongCach())) {
            throw new BadRequestEx("Tên phong cách chơi đã tồn tại");
        }
    }

    private void validateUpdate(PhongCachChoi e) {
        validateCommon(e);
        if (repo.existsByTenPhongCachIgnoreCaseAndXoaMemFalseAndIdNot(e.getTenPhongCach(), e.getId())) {
            throw new BadRequestEx("Tên phong cách chơi đã tồn tại");
        }
    }

    private void validateCommon(PhongCachChoi e) {
        if (e.getTenPhongCach() == null || e.getTenPhongCach().isBlank()) {
            throw new BadRequestEx("Thiếu ten_phong_cach");
        }
    }

    private boolean isActive(PhongCachChoi e) {
        return !Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getTrangThai());
    }

    private PhongCachChoiResponse toResponse(PhongCachChoi e) {
        return mapper.map(e, PhongCachChoiResponse.class);
    }
}