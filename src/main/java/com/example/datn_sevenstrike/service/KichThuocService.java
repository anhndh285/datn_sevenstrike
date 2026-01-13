package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.KichThuocRequest;
import com.example.datn_sevenstrike.dto.response.KichThuocResponse;
import com.example.datn_sevenstrike.entity.KichThuoc;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.KichThuocRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KichThuocService {

    private final KichThuocRepository repo;
    private final ModelMapper mapper;

    public List<KichThuocResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public KichThuocResponse one(Integer id) {
        KichThuoc e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KichThuoc id=" + id));
        return toResponse(e);
    }

    @Transactional
    public KichThuocResponse create(KichThuocRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        KichThuoc e = mapper.map(req, KichThuoc.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public KichThuocResponse update(Integer id, KichThuocRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        KichThuoc db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KichThuoc id=" + id));


        if (req.getTenKichThuoc() != null) db.setTenKichThuoc(req.getTenKichThuoc());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        KichThuoc db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KichThuoc id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(KichThuoc e) {
        if (e.getTenKichThuoc() == null || e.getTenKichThuoc().isBlank()) throw new BadRequestEx("Thiếu ten_kich_thuoc");
    }

    private KichThuocResponse toResponse(KichThuoc e) {
        return mapper.map(e, KichThuocResponse.class);
    }
}