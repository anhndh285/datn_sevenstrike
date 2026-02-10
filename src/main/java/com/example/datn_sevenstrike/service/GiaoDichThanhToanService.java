package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.GiaoDichThanhToanRequest;
import com.example.datn_sevenstrike.dto.response.GiaoDichThanhToanResponse;
import com.example.datn_sevenstrike.entity.GiaoDichThanhToan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.GiaoDichThanhToanRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GiaoDichThanhToanService {

    private final GiaoDichThanhToanRepository repo;
    private final ModelMapper mapper;

    public List<GiaoDichThanhToanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<GiaoDichThanhToanResponse> byHoaDon(Integer idHoaDon) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id_hoa_don");
        return repo.findAllByIdHoaDonAndXoaMemFalseOrderByThoiGianTaoDesc(idHoaDon)
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

        applyDefaults(e, true);
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

        applyDefaults(db, false);
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

    private void applyDefaults(GiaoDichThanhToan e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);

        if (e.getTrangThai() == null || e.getTrangThai().isBlank()) e.setTrangThai("khoi_tao");
        e.setTrangThai(e.getTrangThai().trim());

        // DB có default sysdatetime() cho thoi_gian_tao, nhưng entity đôi khi NOT NULL -> để an toàn:
        if (createMode && e.getThoiGianTao() == null) e.setThoiGianTao(LocalDateTime.now());
        e.setThoiGianCapNhat(LocalDateTime.now());
    }

    private void validate(GiaoDichThanhToan e) {
        if (e.getIdHoaDon() == null) throw new BadRequestEx("Thiếu id_hoa_don");
        if (e.getIdPhuongThucThanhToan() == null) throw new BadRequestEx("Thiếu id_phuong_thuc_thanh_toan");
        if (e.getSoTien() == null || e.getSoTien().signum() <= 0) throw new BadRequestEx("so_tien phải > 0");
        if (e.getTrangThai() == null || e.getTrangThai().isBlank()) throw new BadRequestEx("Thiếu trang_thai");

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
