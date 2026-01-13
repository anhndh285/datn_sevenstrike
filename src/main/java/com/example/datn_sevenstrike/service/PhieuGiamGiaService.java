package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaResponse;
import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhieuGiamGiaService {

    private final PhieuGiamGiaRepository repo;
    private final ModelMapper mapper;

    public List<PhieuGiamGiaResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public PhieuGiamGiaResponse one(Integer id) {
        PhieuGiamGia e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));
        return toResponse(e);
    }

    @Transactional
    public PhieuGiamGiaResponse create(PhieuGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        PhieuGiamGia e = mapper.map(req, PhieuGiamGia.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getLoaiPhieuGiamGia() == null) e.setLoaiPhieuGiamGia(false);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public PhieuGiamGiaResponse update(Integer id, PhieuGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        PhieuGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));


        if (req.getTenPhieuGiamGia() != null) db.setTenPhieuGiamGia(req.getTenPhieuGiamGia());
        if (req.getLoaiPhieuGiamGia() != null) db.setLoaiPhieuGiamGia(req.getLoaiPhieuGiamGia());
        if (req.getGiaTriGiamGia() != null) db.setGiaTriGiamGia(req.getGiaTriGiamGia());
        if (req.getSoTienGiamToiDa() != null) db.setSoTienGiamToiDa(req.getSoTienGiamToiDa());
        if (req.getHoaDonToiThieu() != null) db.setHoaDonToiThieu(req.getHoaDonToiThieu());
        if (req.getSoLuongSuDung() != null) db.setSoLuongSuDung(req.getSoLuongSuDung());
        if (req.getNgayBatDau() != null) db.setNgayBatDau(req.getNgayBatDau());
        if (req.getNgayKetThuc() != null) db.setNgayKetThuc(req.getNgayKetThuc());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getMoTa() != null) db.setMoTa(req.getMoTa());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        PhieuGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(PhieuGiamGia e) {
        if (e.getTenPhieuGiamGia() == null || e.getTenPhieuGiamGia().isBlank()) throw new BadRequestEx("Thiếu ten_phieu_giam_gia");
        if (e.getNgayBatDau() == null || e.getNgayKetThuc() == null) throw new BadRequestEx("Thiếu ngày_bat_dau/ngày_ket_thuc");
        if (e.getNgayKetThuc().isBefore(e.getNgayBatDau())) throw new BadRequestEx("Ngày kết thúc phải >= ngày bắt đầu");
        if (Boolean.FALSE.equals(e.getLoaiPhieuGiamGia())) { if (e.getGiaTriGiamGia() == null || e.getGiaTriGiamGia().doubleValue() < 0 || e.getGiaTriGiamGia().doubleValue() > 100) throw new BadRequestEx("Giảm % phải nằm trong 0..100"); }
        if (e.getGiaTriGiamGia() == null) throw new BadRequestEx("Thiếu gia_tri_giam_gia");
        if (e.getGiaTriGiamGia().signum() < 0) throw new BadRequestEx("Giá trị giảm phải >= 0");
        if (e.getSoLuongSuDung() == null || e.getSoLuongSuDung() < 0) throw new BadRequestEx("so_luong_su_dung phải >= 0");
    }

    private PhieuGiamGiaResponse toResponse(PhieuGiamGia e) {
        return mapper.map(e, PhieuGiamGiaResponse.class);
    }
}