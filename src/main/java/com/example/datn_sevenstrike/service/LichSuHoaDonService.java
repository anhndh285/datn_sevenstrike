package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LichSuHoaDonRequest;
import com.example.datn_sevenstrike.dto.response.LichSuHoaDonResponse;
import com.example.datn_sevenstrike.entity.LichSuHoaDon;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.LichSuHoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LichSuHoaDonService {

    private final LichSuHoaDonRepository repo;
    private final ModelMapper mapper;

    public List<LichSuHoaDonResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public LichSuHoaDonResponse one(Integer id) {
        LichSuHoaDon e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy Lịch sử hóa đơn id=" + id));
        return toResponse(e);
    }

    // Timeline theo hóa đơn (nếu repo có method này thì dùng luôn)
    public List<LichSuHoaDonResponse> byHoaDon(Integer idHoaDon) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id_hoa_don");
        return repo.findAllByIdHoaDonAndXoaMemFalseOrderByThoiGianAsc(idHoaDon)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public LichSuHoaDonResponse create(LichSuHoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        LichSuHoaDon e = new LichSuHoaDon();
        e.setId(null);
        e.setXoaMem(false);

        e.setIdHoaDon(req.getIdHoaDon());
        e.setTrangThai(req.getTrangThai());
        e.setGhiChu(req.getGhiChu() != null ? req.getGhiChu().trim() : null);

        validate(e);

        LichSuHoaDon saved = repo.save(e);
        // thoiGian lấy từ DB default (insertable=false) -> reload lại để chắc chắn có giá trị
        LichSuHoaDon reloaded = repo.findById(saved.getId()).orElse(saved);
        return toResponse(reloaded);
    }

    @Transactional
    public LichSuHoaDonResponse update(Integer id, LichSuHoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        LichSuHoaDon db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy Lịch sử hóa đơn id=" + id));

        // Audit: không sửa thoiGian (DB tự set)
        if (req.getIdHoaDon() != null) db.setIdHoaDon(req.getIdHoaDon());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu().trim());

        validate(db);

        LichSuHoaDon saved = repo.save(db);
        LichSuHoaDon reloaded = repo.findById(saved.getId()).orElse(saved);
        return toResponse(reloaded);
    }

    @Transactional
    public void delete(Integer id) {
        LichSuHoaDon db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy Lịch sử hóa đơn id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void validate(LichSuHoaDon e) {
        if (e.getIdHoaDon() == null) throw new BadRequestEx("Thiếu id_hoa_don");
        if (e.getTrangThai() == null) throw new BadRequestEx("Thiếu trang_thai");
        if (e.getTrangThai() < 1 || e.getTrangThai() > 7) {
            throw new BadRequestEx("trang_thai không hợp lệ (1..7)");
        }
    }

    private LichSuHoaDonResponse toResponse(LichSuHoaDon e) {
        return mapper.map(e, LichSuHoaDonResponse.class);
    }
}
