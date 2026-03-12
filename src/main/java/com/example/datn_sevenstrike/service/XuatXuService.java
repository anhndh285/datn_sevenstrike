package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.XuatXuRequest;
import com.example.datn_sevenstrike.dto.response.XuatXuResponse;
import com.example.datn_sevenstrike.entity.XuatXu;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import com.example.datn_sevenstrike.repository.SanPhamRepository;
import com.example.datn_sevenstrike.repository.XuatXuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class XuatXuService {

    private final XuatXuRepository repo;
    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ModelMapper mapper;

    public List<XuatXuResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<XuatXuResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public XuatXuResponse one(Integer id) {
        XuatXu e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy XuatXu id=" + id));
        return toResponse(e);
    }

    @Transactional
    public XuatXuResponse create(XuatXuRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        XuatXu e = mapper.map(req, XuatXu.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public XuatXuResponse update(Integer id, XuatXuRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        XuatXu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy XuatXu id=" + id));

        boolean activeCu = isActive(db);

        if (req.getTenXuatXu() != null) db.setTenXuatXu(req.getTenXuatXu());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        XuatXu saved = repo.save(db);
        boolean activeMoi = isActive(saved);

        if (activeCu && !activeMoi) {
            sanPhamRepository.ngungKinhDoanhTheoXuatXu(saved.getId());
            chiTietSanPhamRepository.ngungKinhDoanhTheoXuatXu(saved.getId());
        } else if (!activeCu && activeMoi) {
            sanPhamRepository.batKinhDoanhTheoXuatXu(saved.getId());
            chiTietSanPhamRepository.batKinhDoanhTheoXuatXu(saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        XuatXu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy XuatXu id=" + id));

        db.setXoaMem(true);
        db.setTrangThai(false);
        repo.save(db);

        sanPhamRepository.ngungKinhDoanhTheoXuatXu(id);
        chiTietSanPhamRepository.ngungKinhDoanhTheoXuatXu(id);
    }

    private void applyDefaults(XuatXu e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getTenXuatXu() != null) e.setTenXuatXu(e.getTenXuatXu().trim());
    }

    private void validate(XuatXu e) {
        if (e.getTenXuatXu() == null || e.getTenXuatXu().isBlank()) {
            throw new BadRequestEx("Thiếu ten_xuat_xu");
        }
    }

    private boolean isActive(XuatXu e) {
        return !Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getTrangThai());
    }

    private XuatXuResponse toResponse(XuatXu e) {
        return mapper.map(e, XuatXuResponse.class);
    }
}