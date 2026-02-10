package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ThuongHieuRequest;
import com.example.datn_sevenstrike.dto.response.ThuongHieuResponse;
import com.example.datn_sevenstrike.entity.ThuongHieu;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ThuongHieuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ThuongHieuService {

    private final ThuongHieuRepository repo;
    private final ModelMapper mapper;

    public List<ThuongHieuResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<ThuongHieuResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public ThuongHieuResponse one(Integer id) {
        ThuongHieu e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ThuongHieu id=" + id));
        return toResponse(e);
    }

    @Transactional
    public ThuongHieuResponse create(ThuongHieuRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        ThuongHieu e = mapper.map(req, ThuongHieu.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public ThuongHieuResponse update(Integer id, ThuongHieuRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        ThuongHieu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ThuongHieu id=" + id));

        if (req.getTenThuongHieu() != null) db.setTenThuongHieu(req.getTenThuongHieu());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        ThuongHieu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ThuongHieu id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void applyDefaults(ThuongHieu e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getTenThuongHieu() != null) e.setTenThuongHieu(e.getTenThuongHieu().trim());
    }

    private void validate(ThuongHieu e) {
        if (e.getTenThuongHieu() == null || e.getTenThuongHieu().isBlank())
            throw new BadRequestEx("Thiếu ten_thuong_hieu");
    }

    private ThuongHieuResponse toResponse(ThuongHieu e) {
        return mapper.map(e, ThuongHieuResponse.class);
    }
}
