package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.GiaoDichThanhToanRequest;
import com.example.datn_sevenstrike.dto.response.GiaoDichThanhToanResponse;
import com.example.datn_sevenstrike.entity.GiaoDichThanhToan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.GiaoDichThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GiaoDichThanhToanService {

    private final GiaoDichThanhToanRepository repo;
    private final ModelMapper mapper;

    public List<GiaoDichThanhToanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public GiaoDichThanhToanResponse one(Integer id) {
        GiaoDichThanhToan e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy GiaoDichThanhToan id=" + id));
        return toResponse(e);
    }

    @Transactional
    public GiaoDichThanhToanResponse create(GiaoDichThanhToanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        GiaoDichThanhToan e = mapper.map(req, GiaoDichThanhToan.class);
        e.setId(null);

        // default theo DDL
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null || e.getTrangThai().isBlank()) e.setTrangThai("khoi_tao");

        // DB tự set sysdatetime() => không set từ request
        e.setThoiGianTao(null);
        e.setThoiGianCapNhat(null);

        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public GiaoDichThanhToanResponse update(Integer id, GiaoDichThanhToanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        GiaoDichThanhToan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy GiaoDichThanhToan id=" + id));

        if (req.getIdHoaDon() != null) db.setIdHoaDon(req.getIdHoaDon());
        if (req.getIdPhuongThucThanhToan() != null) db.setIdPhuongThucThanhToan(req.getIdPhuongThucThanhToan());
        if (req.getSoTien() != null) db.setSoTien(req.getSoTien());

        if (req.getTrangThai() != null && !req.getTrangThai().isBlank())
            db.setTrangThai(req.getTrangThai().trim());

        if (req.getMaYeuCau() != null) db.setMaYeuCau(req.getMaYeuCau());
        if (req.getMaGiaoDichNgoai() != null) db.setMaGiaoDichNgoai(req.getMaGiaoDichNgoai());
        if (req.getMaThamChieu() != null) db.setMaThamChieu(req.getMaThamChieu());
        if (req.getDuongDanThanhToan() != null) db.setDuongDanThanhToan(req.getDuongDanThanhToan());
        if (req.getDuLieuQr() != null) db.setDuLieuQr(req.getDuLieuQr());
        if (req.getThoiGianHetHan() != null) db.setThoiGianHetHan(req.getThoiGianHetHan());
        if (req.getDuLieuPhanHoi() != null) db.setDuLieuPhanHoi(req.getDuLieuPhanHoi());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu());

        // KHÔNG cho update thoiGianTao (DB set mặc định)
        // if (req.getThoiGianTao() != null) db.setThoiGianTao(req.getThoiGianTao()); // bỏ
        db.setThoiGianCapNhat(LocalDateTime.now());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        GiaoDichThanhToan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy GiaoDichThanhToan id=" + id));
        db.setXoaMem(true);
        db.setThoiGianCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void validate(GiaoDichThanhToan e) {
        if (e.getIdHoaDon() == null) throw new BadRequestEx("Thiếu id_hoa_don");
        if (e.getIdPhuongThucThanhToan() == null) throw new BadRequestEx("Thiếu id_phuong_thuc_thanh_toan");
        if (e.getSoTien() == null || e.getSoTien().signum() <= 0) throw new BadRequestEx("so_tien phải > 0");
        if (e.getTrangThai() == null || e.getTrangThai().isBlank()) throw new BadRequestEx("Thiếu trang_thai");

        // (optional) siết enum trạng thái theo DDL mô tả
        // khoi_tao/dang_xu_ly/thanh_cong/that_bai/huy
        String st = e.getTrangThai().trim();
        if (!st.equals("khoi_tao") && !st.equals("dang_xu_ly") && !st.equals("thanh_cong")
                && !st.equals("that_bai") && !st.equals("huy")) {
            throw new BadRequestEx("trang_thai không hợp lệ: " + st);
        }
    }

    private GiaoDichThanhToanResponse toResponse(GiaoDichThanhToan e) {
        return mapper.map(e, GiaoDichThanhToanResponse.class);
    }
}
