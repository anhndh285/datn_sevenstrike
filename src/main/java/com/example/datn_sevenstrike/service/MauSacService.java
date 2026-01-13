package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.MauSacRequest;
import com.example.datn_sevenstrike.dto.response.MauSacResponse;
import com.example.datn_sevenstrike.entity.MauSac;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.MauSacRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MauSacService {

    private final MauSacRepository repo;
    private final ModelMapper mapper;

    public List<MauSacResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public MauSacResponse one(Integer id) {
        MauSac e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy MauSac id=" + id));
        return toResponse(e);
    }

    @Transactional
    public MauSacResponse create(MauSacRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        MauSac e = mapper.map(req, MauSac.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public MauSacResponse update(Integer id, MauSacRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        MauSac db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy MauSac id=" + id));


        if (req.getTenMauSac() != null) db.setTenMauSac(req.getTenMauSac());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        MauSac db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy MauSac id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(MauSac e) {
        if (e.getTenMauSac() == null || e.getTenMauSac().isBlank()) throw new BadRequestEx("Thiếu ten_mau_sac");
    }

    private MauSacResponse toResponse(MauSac e) {
        return mapper.map(e, MauSacResponse.class);
    }
}
