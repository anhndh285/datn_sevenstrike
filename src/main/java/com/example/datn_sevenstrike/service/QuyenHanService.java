package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.QuyenHanRequest;
import com.example.datn_sevenstrike.dto.response.QuyenHanResponse;
import com.example.datn_sevenstrike.entity.QuyenHan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.QuyenHanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuyenHanService {

    private final QuyenHanRepository repo;
    private final ModelMapper mapper;

    public List<QuyenHanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<QuyenHanResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public QuyenHanResponse one(Integer id) {
        QuyenHan e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy QuyenHan id=" + id));
        return toResponse(e);
    }

    @Transactional
    public QuyenHanResponse create(QuyenHanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        QuyenHan e = mapper.map(req, QuyenHan.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public QuyenHanResponse update(Integer id, QuyenHanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        QuyenHan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy QuyenHan id=" + id));

        if (req.getTenQuyenHan() != null) db.setTenQuyenHan(req.getTenQuyenHan());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        QuyenHan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy QuyenHan id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void applyDefaults(QuyenHan e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getTenQuyenHan() != null) e.setTenQuyenHan(e.getTenQuyenHan().trim());
    }

    private void validate(QuyenHan e) {
        if (e.getTenQuyenHan() == null || e.getTenQuyenHan().isBlank())
            throw new BadRequestEx("Thiếu ten_quyen_han");
    }

    private QuyenHanResponse toResponse(QuyenHan e) {
        return mapper.map(e, QuyenHanResponse.class);
    }
}
