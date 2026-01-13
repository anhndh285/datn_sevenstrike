package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.PhongCachChoiRequest;
import com.example.datn_sevenstrike.dto.response.PhongCachChoiResponse;
import com.example.datn_sevenstrike.entity.PhongCachChoi;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.PhongCachChoiRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhongCachChoiService {

    private final PhongCachChoiRepository repo;
    private final ModelMapper mapper;

    public List<PhongCachChoiResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public PhongCachChoiResponse one(Integer id) {
        PhongCachChoi e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhongCachChoi id=" + id));
        return toResponse(e);
    }

    @Transactional
    public PhongCachChoiResponse create(PhongCachChoiRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        PhongCachChoi e = mapper.map(req, PhongCachChoi.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public PhongCachChoiResponse update(Integer id, PhongCachChoiRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        PhongCachChoi db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhongCachChoi id=" + id));


        if (req.getTenPhongCach() != null) db.setTenPhongCach(req.getTenPhongCach());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        PhongCachChoi db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhongCachChoi id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(PhongCachChoi e) {
        if (e.getTenPhongCach() == null || e.getTenPhongCach().isBlank()) throw new BadRequestEx("Thiếu ten_phong_cach");
    }

    private PhongCachChoiResponse toResponse(PhongCachChoi e) {
        return mapper.map(e, PhongCachChoiResponse.class);
    }
}

