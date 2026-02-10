package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ChiTietSanPhamRequest;
import com.example.datn_sevenstrike.dto.response.ChiTietSanPhamBanHangResponse;
import com.example.datn_sevenstrike.dto.response.ChiTietSanPhamResponse;
import com.example.datn_sevenstrike.entity.ChiTietSanPham;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChiTietSanPhamService {

    private final ChiTietSanPhamRepository repo;
    private final ModelMapper mapper;

    public List<ChiTietSanPhamResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public ChiTietSanPhamResponse one(Integer id) {
        ChiTietSanPham e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CTSP id=" + id));
        return toResponse(e);
    }

    public List<ChiTietSanPhamResponse> bySanPham(Integer idSanPham) {
        if (idSanPham == null) throw new BadRequestEx("Thiếu id_san_pham");
        return repo.findAllByIdSanPhamAndXoaMemFalseOrderByIdDesc(idSanPham)
                .stream().map(this::toResponse).toList();
    }

    // ============================
    // POS: list CTSP bán hàng
    // ============================
    public List<ChiTietSanPhamBanHangResponse> banHang() {
        return repo.findBanHang().stream().map(v ->
                ChiTietSanPhamBanHangResponse.builder()
                        .id(v.getId())
                        .maCtsp(v.getMaCtsp())
                        .tenSanPham(v.getTenSanPham())
                        .mauSac(v.getMauSac())
                        .kichCo(v.getKichCo())
                        .soLuong(v.getSoLuong() == null ? 0 : v.getSoLuong())
                        .giaBan(v.getGiaBan() == null ? BigDecimal.ZERO : v.getGiaBan())
                        .anhUrl(v.getAnhUrl() == null ? "" : v.getAnhUrl())
                        .build()
        ).toList();
    }

    @Transactional
    public ChiTietSanPhamResponse create(ChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        ChiTietSanPham e = mapper.map(req, ChiTietSanPham.class);
        e.setId(null);

        applyDefaults(e, true);
        validateRequired(e);
        validateDuplicateCreate(e);

        try {
            return toResponse(repo.save(e));
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestEx("Không thể tạo CTSP: dữ liệu không hợp lệ hoặc biến thể đã tồn tại.");
        }
    }

    @Transactional
    public ChiTietSanPhamResponse update(Integer id, ChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        ChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CTSP id=" + id));

        if (req.getIdSanPham() != null) db.setIdSanPham(req.getIdSanPham());
        if (req.getIdMauSac() != null) db.setIdMauSac(req.getIdMauSac());
        if (req.getIdKichThuoc() != null) db.setIdKichThuoc(req.getIdKichThuoc());
        if (req.getIdLoaiSan() != null) db.setIdLoaiSan(req.getIdLoaiSan());
        if (req.getIdFormChan() != null) db.setIdFormChan(req.getIdFormChan());

        if (req.getGiaNiemYet() != null) db.setGiaNiemYet(req.getGiaNiemYet());
        if (req.getGiaBan() != null) db.setGiaBan(req.getGiaBan());
        if (req.getSoLuong() != null) db.setSoLuong(req.getSoLuong());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu());

        applyDefaults(db, false);
        validateRequired(db);
        validateDuplicateUpdate(db);

        try {
            return toResponse(repo.save(db));
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestEx("Không thể cập nhật CTSP: dữ liệu không hợp lệ hoặc biến thể đã tồn tại.");
        }
    }

    @Transactional
    public void delete(Integer id) {
        ChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy CTSP id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void applyDefaults(ChiTietSanPham e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getSoLuong() == null) e.setSoLuong(0);

        if (e.getGiaNiemYet() == null) e.setGiaNiemYet(BigDecimal.ZERO);
        if (e.getGiaBan() == null) e.setGiaBan(e.getGiaNiemYet());

        if (e.getGhiChu() == null) e.setGhiChu("");

        LocalDateTime now = LocalDateTime.now();
        if (createMode && e.getNgayTao() == null) e.setNgayTao(now);
        if (e.getNgayCapNhat() == null) e.setNgayCapNhat(now);
    }

    private void validateRequired(ChiTietSanPham e) {
        if (e.getIdSanPham() == null) throw new BadRequestEx("Thiếu id_san_pham");
        if (e.getIdMauSac() == null) throw new BadRequestEx("Thiếu id_mau_sac");
        if (e.getIdKichThuoc() == null) throw new BadRequestEx("Thiếu id_kich_thuoc");
        if (e.getIdLoaiSan() == null) throw new BadRequestEx("Thiếu id_loai_san");
        if (e.getIdFormChan() == null) throw new BadRequestEx("Thiếu id_form_chan");

        if (e.getSoLuong() != null && e.getSoLuong() < 0)
            throw new BadRequestEx("Số lượng không hợp lệ");

        if (e.getGiaNiemYet() != null && e.getGiaNiemYet().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestEx("Giá niêm yết không hợp lệ");

        if (e.getGiaBan() != null && e.getGiaBan().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestEx("Giá bán không hợp lệ");
    }

    private void validateDuplicateCreate(ChiTietSanPham e) {
        boolean exists = repo.existsByIdSanPhamAndIdMauSacAndIdKichThuocAndIdLoaiSanAndIdFormChanAndXoaMemFalse(
                e.getIdSanPham(),
                e.getIdMauSac(),
                e.getIdKichThuoc(),
                e.getIdLoaiSan(),
                e.getIdFormChan()
        );
        if (exists) throw new BadRequestEx("Biến thể này đã tồn tại (trùng màu/size/loại sân/form).");
    }

    private void validateDuplicateUpdate(ChiTietSanPham e) {
        boolean exists = repo.existsByIdSanPhamAndIdMauSacAndIdKichThuocAndIdLoaiSanAndIdFormChanAndXoaMemFalseAndIdNot(
                e.getIdSanPham(),
                e.getIdMauSac(),
                e.getIdKichThuoc(),
                e.getIdLoaiSan(),
                e.getIdFormChan(),
                e.getId()
        );
        if (exists) throw new BadRequestEx("Biến thể này đã tồn tại (trùng màu/size/loại sân/form).");
    }

    private ChiTietSanPhamResponse toResponse(ChiTietSanPham e) {
        return mapper.map(e, ChiTietSanPhamResponse.class);
    }
}
