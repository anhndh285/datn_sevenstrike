package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LichSuHoaDonRequest;
import com.example.datn_sevenstrike.dto.response.LichSuHoaDonResponse;
import com.example.datn_sevenstrike.entity.LichSuHoaDon;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.LichSuHoaDonRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public LichSuHoaDonResponse create(LichSuHoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        LichSuHoaDon e = new LichSuHoaDon();
        e.setId(null);
        e.setXoaMem(false);

        // bắt buộc
        e.setIdHoaDon(req.getIdHoaDon());
        e.setTrangThai(req.getTrangThai() != null ? req.getTrangThai().trim() : null);

        // optional
        e.setGhiChu(req.getGhiChu() != null ? req.getGhiChu().trim() : null);

        // thoiGian: DB tự set (sysdatetime) => không set từ req

        validate(e);

        LichSuHoaDon saved = repo.save(e);

        // reload để lấy thoiGian default từ DB (tránh response null)
        LichSuHoaDon reloaded = repo.findById(saved.getId()).orElse(saved);
        return toResponse(reloaded);
    }

    @Transactional
    public LichSuHoaDonResponse update(Integer id, LichSuHoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        LichSuHoaDon db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy Lịch sử hóa đơn id=" + id));

        // Cho phép sửa nội dung cần thiết
        if (req.getIdHoaDon() != null) db.setIdHoaDon(req.getIdHoaDon());
        if (req.getTrangThai() != null && !req.getTrangThai().isBlank())
            db.setTrangThai(req.getTrangThai().trim());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu().trim());

        // thoiGian: không update (audit)
        // db.setThoiGian(...) -> bỏ

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
        if (e.getTrangThai() == null || e.getTrangThai().isBlank())
            throw new BadRequestEx("Thiếu trang_thai");
        if (e.getTrangThai().length() > 50)
            throw new BadRequestEx("trang_thai tối đa 50 ký tự");
    }

    private LichSuHoaDonResponse toResponse(LichSuHoaDon e) {
        return mapper.map(e, LichSuHoaDonResponse.class);
    }
}
