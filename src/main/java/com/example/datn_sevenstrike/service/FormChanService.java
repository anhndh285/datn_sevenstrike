package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.FormChanRequest;
import com.example.datn_sevenstrike.dto.response.FormChanResponse;
import com.example.datn_sevenstrike.entity.FormChan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
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
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ModelMapper mapper;

    public List<FormChanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FormChanResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FormChanResponse one(Integer id) {
        FormChan e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy FormChan id=" + id));
        return toResponse(e);
    }

    @Transactional
    public FormChanResponse create(FormChanRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        }

        FormChan e = mapper.map(req, FormChan.class);
        e.setId(null);

        applyDefaults(e);
        validateCreate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public FormChanResponse update(Integer id, FormChanRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        }

        FormChan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy FormChan id=" + id));

        boolean activeCu = isActive(db);

        if (req.getTenFormChan() != null) {
            db.setTenFormChan(req.getTenFormChan());
        }
        if (req.getTrangThai() != null) {
            db.setTrangThai(req.getTrangThai());
        }
        if (req.getXoaMem() != null) {
            db.setXoaMem(req.getXoaMem());
        }

        applyDefaults(db);
        validateUpdate(db);

        FormChan saved = repo.save(db);
        boolean activeMoi = isActive(saved);

        if (activeCu && !activeMoi) {
            chiTietSanPhamRepository.ngungKinhDoanhTheoFormChan(saved.getId());
        } else if (!activeCu && activeMoi) {
            chiTietSanPhamRepository.batKinhDoanhTheoFormChan(saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        FormChan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy FormChan id=" + id));

        db.setXoaMem(true);
        db.setTrangThai(false);
        repo.save(db);

        chiTietSanPhamRepository.ngungKinhDoanhTheoFormChan(id);
    }

    private void applyDefaults(FormChan e) {
        if (e.getXoaMem() == null) {
            e.setXoaMem(false);
        }
        if (e.getTrangThai() == null) {
            e.setTrangThai(true);
        }
        if (e.getTenFormChan() != null) {
            e.setTenFormChan(e.getTenFormChan().trim());
        }
    }

    private void validateCreate(FormChan e) {
        validateCommon(e);
        if (isDuplicateTen(e.getTenFormChan(), null)) {
            throw new BadRequestEx("Tên form chân đã tồn tại");
        }
    }

    private void validateUpdate(FormChan e) {
        validateCommon(e);
        if (isDuplicateTen(e.getTenFormChan(), e.getId())) {
            throw new BadRequestEx("Tên form chân đã tồn tại");
        }
    }

    private void validateCommon(FormChan e) {
        if (e.getTenFormChan() == null || e.getTenFormChan().isBlank()) {
            throw new BadRequestEx("Thiếu ten_form_chan");
        }
    }

    private boolean isDuplicateTen(String ten, Integer currentId) {
        String normalized = normalize(ten);
        return repo.findAllByXoaMemFalseOrderByIdDesc().stream().anyMatch(item ->
                !sameId(item.getId(), currentId) && normalize(item.getTenFormChan()).equalsIgnoreCase(normalized)
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean sameId(Integer a, Integer b) {
        return a != null && a.equals(b);
    }

    private boolean isActive(FormChan e) {
        return !Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getTrangThai());
    }

    private FormChanResponse toResponse(FormChan e) {
        return mapper.map(e, FormChanResponse.class);
    }
}