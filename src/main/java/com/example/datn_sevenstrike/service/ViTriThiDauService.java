package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ViTriThiDauRequest;
import com.example.datn_sevenstrike.dto.response.ViTriThiDauResponse;
import com.example.datn_sevenstrike.entity.ViTriThiDau;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
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
    private final ModelMapper mapper;

    public List<ViTriThiDauResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
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

        if (e.getXoaMem() == null) e.setXoaMem(false);

        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public ViTriThiDauResponse update(Integer id, ViTriThiDauRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        ViTriThiDau db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ViTriThiDau id=" + id));

        if (req.getTenViTri() != null) db.setTenViTri(req.getTenViTri());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        ViTriThiDau db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ViTriThiDau id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void validate(ViTriThiDau e) {
        if (e.getTenViTri() == null || e.getTenViTri().isBlank()) {
            throw new BadRequestEx("Thiếu ten_vi_tri");
        }
    }

    private ViTriThiDauResponse toResponse(ViTriThiDau e) {
        return mapper.map(e, ViTriThiDauResponse.class);
    }
}
