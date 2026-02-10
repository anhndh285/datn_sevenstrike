package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LoaiSanRequest;
import com.example.datn_sevenstrike.dto.response.LoaiSanResponse;
import com.example.datn_sevenstrike.entity.LoaiSan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
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

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);

        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public LoaiSanResponse update(Integer id, LoaiSanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        LoaiSan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy LoaiSan id=" + id));

        if (req.getTenLoaiSan() != null) db.setTenLoaiSan(req.getTenLoaiSan());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        if (db.getXoaMem() == null) db.setXoaMem(false);
        if (db.getTrangThai() == null) db.setTrangThai(true);

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        LoaiSan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy LoaiSan id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void validate(LoaiSan e) {
        if (e.getTenLoaiSan() == null || e.getTenLoaiSan().isBlank())
            throw new BadRequestEx("Thiếu ten_loai_san");
    }

    private LoaiSanResponse toResponse(LoaiSan e) {
        return mapper.map(e, LoaiSanResponse.class);
    }
}
