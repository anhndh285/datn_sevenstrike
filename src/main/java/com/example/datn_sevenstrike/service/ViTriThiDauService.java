package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ViTriThiDauRequest;
import com.example.datn_sevenstrike.dto.response.ViTriThiDauResponse;
import com.example.datn_sevenstrike.entity.ViTriThiDau;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import com.example.datn_sevenstrike.repository.SanPhamRepository;
import com.example.datn_sevenstrike.repository.ViTriThiDauRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViTriThiDauService {

    private final ViTriThiDauRepository repo;
    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ModelMapper mapper;

    public List<ViTriThiDauResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<ViTriThiDauResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public ViTriThiDauResponse one(Integer id) {
        ViTriThiDau e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ViTriThiDau id=" + id));
        return toResponse(e);
    }

    @Transactional
    public ViTriThiDauResponse create(ViTriThiDauRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        ViTriThiDau e = mapper.map(req, ViTriThiDau.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public ViTriThiDauResponse update(Integer id, ViTriThiDauRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        ViTriThiDau db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ViTriThiDau id=" + id));

        boolean activeCu = isActive(db);

        if (req.getTenViTri() != null) db.setTenViTri(req.getTenViTri());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        ViTriThiDau saved = repo.save(db);
        boolean activeMoi = isActive(saved);

        if (activeCu && !activeMoi) {
            sanPhamRepository.ngungKinhDoanhTheoViTriThiDau(saved.getId());
            chiTietSanPhamRepository.ngungKinhDoanhTheoViTriThiDau(saved.getId());
        } else if (!activeCu && activeMoi) {
            sanPhamRepository.batKinhDoanhTheoViTriThiDau(saved.getId());
            chiTietSanPhamRepository.batKinhDoanhTheoViTriThiDau(saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        ViTriThiDau db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ViTriThiDau id=" + id));

        db.setXoaMem(true);
        db.setTrangThai(false);
        repo.save(db);

        sanPhamRepository.ngungKinhDoanhTheoViTriThiDau(id);
        chiTietSanPhamRepository.ngungKinhDoanhTheoViTriThiDau(id);
    }

    private void applyDefaults(ViTriThiDau e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getTenViTri() != null) e.setTenViTri(e.getTenViTri().trim());
    }

    private void validate(ViTriThiDau e) {
        if (e.getTenViTri() == null || e.getTenViTri().isBlank()) {
            throw new BadRequestEx("Thiếu ten_vi_tri");
        }
    }

    private boolean isActive(ViTriThiDau e) {
        return !Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getTrangThai());
    }

    private ViTriThiDauResponse toResponse(ViTriThiDau e) {
        return mapper.map(e, ViTriThiDauResponse.class);
    }
}