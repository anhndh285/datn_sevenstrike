package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.XuatXuRequest;
import com.example.datn_sevenstrike.dto.response.XuatXuResponse;
import com.example.datn_sevenstrike.entity.XuatXu;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
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
    private final ModelMapper mapper;

    public List<XuatXuResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
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

        if (e.getXoaMem() == null) e.setXoaMem(false);

        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public XuatXuResponse update(Integer id, XuatXuRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        XuatXu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy XuatXu id=" + id));

        if (req.getTenXuatXu() != null) db.setTenXuatXu(req.getTenXuatXu());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        XuatXu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy XuatXu id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void validate(XuatXu e) {
        if (e.getTenXuatXu() == null || e.getTenXuatXu().isBlank()) {
            throw new BadRequestEx("Thiếu ten_xuat_xu");
        }
    }

    private XuatXuResponse toResponse(XuatXu e) {
        return mapper.map(e, XuatXuResponse.class);
    }
}
