package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.CoGiayRequest;
import com.example.datn_sevenstrike.dto.response.CoGiayResponse;
import com.example.datn_sevenstrike.entity.CoGiay;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import com.example.datn_sevenstrike.repository.CoGiayRepository;
import com.example.datn_sevenstrike.repository.SanPhamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoGiayService {

    private final CoGiayRepository repo;
    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ModelMapper mapper;

    public List<CoGiayResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CoGiayResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CoGiayResponse one(Integer id) {
        CoGiay e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CoGiay id=" + id));
        return toResponse(e);
    }

    @Transactional
    public CoGiayResponse create(CoGiayRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        }

        CoGiay e = mapper.map(req, CoGiay.class);
        e.setId(null);

        applyDefaults(e);
        validateCreate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public CoGiayResponse update(Integer id, CoGiayRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        }

        CoGiay db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CoGiay id=" + id));

        boolean activeCu = isActive(db);

        if (req.getTenCoGiay() != null) {
            db.setTenCoGiay(req.getTenCoGiay());
        }
        if (req.getTrangThai() != null) {
            db.setTrangThai(req.getTrangThai());
        }
        if (req.getXoaMem() != null) {
            db.setXoaMem(req.getXoaMem());
        }

        applyDefaults(db);
        validateUpdate(db);

        CoGiay saved = repo.save(db);
        boolean activeMoi = isActive(saved);

        if (activeCu && !activeMoi) {
            sanPhamRepository.ngungKinhDoanhTheoCoGiay(saved.getId());
            chiTietSanPhamRepository.ngungKinhDoanhTheoCoGiay(saved.getId());
        } else if (!activeCu && activeMoi) {
            sanPhamRepository.batKinhDoanhTheoCoGiay(saved.getId());
            chiTietSanPhamRepository.batKinhDoanhTheoCoGiay(saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        CoGiay db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CoGiay id=" + id));

        db.setXoaMem(true);
        db.setTrangThai(false);
        repo.save(db);

        sanPhamRepository.ngungKinhDoanhTheoCoGiay(id);
        chiTietSanPhamRepository.ngungKinhDoanhTheoCoGiay(id);
    }

    private void applyDefaults(CoGiay e) {
        if (e.getXoaMem() == null) {
            e.setXoaMem(false);
        }
        if (e.getTrangThai() == null) {
            e.setTrangThai(true);
        }
        if (e.getTenCoGiay() != null) {
            e.setTenCoGiay(e.getTenCoGiay().trim());
        }
    }

    private void validateCreate(CoGiay e) {
        validateCommon(e);
        if (isDuplicateTen(e.getTenCoGiay(), null)) {
            throw new BadRequestEx("Tên cỏ giày đã tồn tại");
        }
    }

    private void validateUpdate(CoGiay e) {
        validateCommon(e);
        if (isDuplicateTen(e.getTenCoGiay(), e.getId())) {
            throw new BadRequestEx("Tên cỏ giày đã tồn tại");
        }
    }

    private void validateCommon(CoGiay e) {
        if (e.getTenCoGiay() == null || e.getTenCoGiay().isBlank()) {
            throw new BadRequestEx("Thiếu ten_co_giay");
        }
    }

    private boolean isDuplicateTen(String ten, Integer currentId) {
        String normalized = normalize(ten);
        return repo.findAllByXoaMemFalseOrderByIdDesc().stream().anyMatch(item ->
                !sameId(item.getId(), currentId) && normalize(item.getTenCoGiay()).equalsIgnoreCase(normalized)
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean sameId(Integer a, Integer b) {
        return a != null && a.equals(b);
    }

    private boolean isActive(CoGiay e) {
        return !Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getTrangThai());
    }

    private CoGiayResponse toResponse(CoGiay e) {
        return mapper.map(e, CoGiayResponse.class);
    }
}