package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.CoGiayRequest;
import com.example.datn_sevenstrike.dto.response.CoGiayResponse;
import com.example.datn_sevenstrike.entity.CoGiay;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.CoGiayRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoGiayService {

    private final CoGiayRepository repo;
    private final ModelMapper mapper;

    public List<CoGiayResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<CoGiayResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public CoGiayResponse one(Integer id) {
        CoGiay e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CoGiay id=" + id));
        return toResponse(e);
    }

    @Transactional
    public CoGiayResponse create(CoGiayRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        CoGiay e = mapper.map(req, CoGiay.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public CoGiayResponse update(Integer id, CoGiayRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        CoGiay db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CoGiay id=" + id));

        if (req.getTenCoGiay() != null) db.setTenCoGiay(req.getTenCoGiay());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        CoGiay db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CoGiay id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void applyDefaults(CoGiay e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
    }

    private void validate(CoGiay e) {
        if (e.getTenCoGiay() == null || e.getTenCoGiay().isBlank())
            throw new BadRequestEx("Thiếu ten_co_giay");
    }

    private CoGiayResponse toResponse(CoGiay e) {
        return mapper.map(e, CoGiayResponse.class);
    }
}
