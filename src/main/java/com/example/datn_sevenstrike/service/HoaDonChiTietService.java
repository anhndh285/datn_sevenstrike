package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.HoaDonChiTietRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonChiTietResponse;
import com.example.datn_sevenstrike.entity.HoaDonChiTiet;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.HoaDonChiTietRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HoaDonChiTietService {

    private final HoaDonChiTietRepository repo;
    private final ModelMapper mapper;

    public List<HoaDonChiTietResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<HoaDonChiTietResponse> byHoaDon(Integer idHoaDon) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id_hoa_don");
        return repo.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon)
                .stream().map(this::toResponse).toList();
    }

    public HoaDonChiTietResponse one(Integer id) {
        HoaDonChiTiet e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDonChiTiet id=" + id));
        return toResponse(e);
    }

    @Transactional
    public HoaDonChiTietResponse create(HoaDonChiTietRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        HoaDonChiTiet e = mapper.map(req, HoaDonChiTiet.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public HoaDonChiTietResponse update(Integer id, HoaDonChiTietRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        HoaDonChiTiet db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDonChiTiet id=" + id));

        if (req.getIdHoaDon() != null) db.setIdHoaDon(req.getIdHoaDon());
        if (req.getIdChiTietSanPham() != null) db.setIdChiTietSanPham(req.getIdChiTietSanPham());
        if (req.getSoLuong() != null) db.setSoLuong(req.getSoLuong());
        if (req.getDonGia() != null) db.setDonGia(req.getDonGia());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        HoaDonChiTiet db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDonChiTiet id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void applyDefaults(HoaDonChiTiet e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getGhiChu() == null) e.setGhiChu("");
    }

    private void validate(HoaDonChiTiet e) {
        if (e.getIdHoaDon() == null) throw new BadRequestEx("Thiếu id_hoa_don");
        if (e.getIdChiTietSanPham() == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham");

        if (e.getSoLuong() == null || e.getSoLuong() <= 0) throw new BadRequestEx("so_luong phải > 0");
        if (e.getDonGia() == null || e.getDonGia().compareTo(BigDecimal.ZERO) < 0) throw new BadRequestEx("don_gia phải >= 0");
    }

    private HoaDonChiTietResponse toResponse(HoaDonChiTiet e) {
        return mapper.map(e, HoaDonChiTietResponse.class);
    }
}
