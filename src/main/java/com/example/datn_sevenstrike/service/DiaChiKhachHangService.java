package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.DiaChiKhachHangRequest;
import com.example.datn_sevenstrike.dto.response.DiaChiKhachHangResponse;
import com.example.datn_sevenstrike.entity.DiaChiKhachHang;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.DiaChiKhachHangRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiaChiKhachHangService {

    private final DiaChiKhachHangRepository repo;
    private final ModelMapper mapper;

    public List<DiaChiKhachHangResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public DiaChiKhachHangResponse one(Integer id) {
        DiaChiKhachHang e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));
        return toResponse(e);
    }

    @Transactional
    public DiaChiKhachHangResponse create(DiaChiKhachHangRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        DiaChiKhachHang e = mapper.map(req, DiaChiKhachHang.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getMacDinh() == null) e.setMacDinh(false);


        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public DiaChiKhachHangResponse update(Integer id, DiaChiKhachHangRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        DiaChiKhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));


        if (req.getIdKhachHang() != null) db.setIdKhachHang(req.getIdKhachHang());
        if (req.getTenDiaChi() != null) db.setTenDiaChi(req.getTenDiaChi());
        if (req.getThanhPho() != null) db.setThanhPho(req.getThanhPho());
        if (req.getQuan() != null) db.setQuan(req.getQuan());
        if (req.getPhuong() != null) db.setPhuong(req.getPhuong());
        if (req.getDiaChiCuThe() != null) db.setDiaChiCuThe(req.getDiaChiCuThe());
        if (req.getMacDinh() != null) db.setMacDinh(req.getMacDinh());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        DiaChiKhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));
        db.setXoaMem(true);

        repo.save(db);
    }

    private void validate(DiaChiKhachHang e) {
        if (e.getIdKhachHang() == null) throw new BadRequestEx("Thiếu id_khach_hang");
        if (e.getTenDiaChi() == null || e.getTenDiaChi().isBlank()) throw new BadRequestEx("Thiếu ten_dia_chi");
    }

    private DiaChiKhachHangResponse toResponse(DiaChiKhachHang e) {
        return mapper.map(e, DiaChiKhachHangResponse.class);
    }
}

