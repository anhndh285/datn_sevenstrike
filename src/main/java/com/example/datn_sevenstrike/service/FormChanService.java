package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.FormChanRequest;
import com.example.datn_sevenstrike.dto.response.FormChanResponse;
import com.example.datn_sevenstrike.entity.FormChan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.FormChanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FormChanService {

    private final FormChanRepository repo;
    private final ModelMapper mapper;

    public List<FormChanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public FormChanResponse one(Integer id) {
        FormChan e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy FormChan id=" + id));
        return toResponse(e);
    }

    @Transactional
    public FormChanResponse create(FormChanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        FormChan e = mapper.map(req, FormChan.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public FormChanResponse update(Integer id, FormChanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        FormChan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy FormChan id=" + id));


        if (req.getTenFormChan() != null) db.setTenFormChan(req.getTenFormChan());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        FormChan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy FormChan id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(FormChan e) {
        if (e.getTenFormChan() == null || e.getTenFormChan().isBlank()) throw new BadRequestEx("Thiếu ten_form_chan");
    }

    private FormChanResponse toResponse(FormChan e) {
        return mapper.map(e, FormChanResponse.class);
    }
}