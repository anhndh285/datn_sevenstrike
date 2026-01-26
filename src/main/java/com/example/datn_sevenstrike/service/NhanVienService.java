package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.NhanVienRequest;
import com.example.datn_sevenstrike.dto.response.NhanVienResponse;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepository repo;
    private final ModelMapper mapper;

    public List<NhanVienResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    // ✅ NEW: Paging
    public Page<NhanVienResponse> page(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);

        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));
        return repo.findAllByXoaMemFalse(pageable).map(this::toResponse);
    }

    public NhanVienResponse one(Integer id) {
        NhanVien e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy NhanVien id=" + id));
        return toResponse(e);
    }

    @Transactional
    public NhanVienResponse create(NhanVienRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        NhanVien e = mapper.map(req, NhanVien.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getNgayTao() == null) e.setNgayTao(LocalDateTime.now());
        e.setNgayCapNhat(null);

        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public NhanVienResponse update(Integer id, NhanVienRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        NhanVien db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy NhanVien id=" + id));

        // ✅ update kiểu patch: req field nào != null thì set
        // LƯU Ý: các getter dưới đây phải tồn tại trong NhanVienRequest.
        // Nếu request bạn có field khác, chỉnh lại đúng tên field.
        if (req.getTenNhanVien() != null) db.setTenNhanVien(req.getTenNhanVien());
        if (req.getTenTaiKhoan() != null) db.setTenTaiKhoan(req.getTenTaiKhoan());
        if (req.getMatKhau() != null) db.setMatKhau(req.getMatKhau());
        if (req.getEmail() != null) db.setEmail(req.getEmail());
        if (req.getSoDienThoai() != null) db.setSoDienThoai(req.getSoDienThoai());

        // optional fields (nếu có)
        if (req.getNgaySinh() != null) db.setNgaySinh(req.getNgaySinh());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu());
        if (req.getAnhNhanVien() != null) db.setAnhNhanVien(req.getAnhNhanVien());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getIdQuyenHan() != null) db.setIdQuyenHan(req.getIdQuyenHan());

        if (req.getNguoiCapNhat() != null) db.setNguoiCapNhat(req.getNguoiCapNhat());
        db.setNgayCapNhat(LocalDateTime.now());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        NhanVien db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy NhanVien id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void validate(NhanVien e) {
        if (e.getTenTaiKhoan() == null || e.getTenTaiKhoan().isBlank()) {
            throw new BadRequestEx("Thiếu ten_tai_khoan");
        }
        if (e.getTenNhanVien() == null || e.getTenNhanVien().isBlank()) {
            throw new BadRequestEx("Thiếu ten_nhan_vien");
        }
    }

    private NhanVienResponse toResponse(NhanVien e) {
        return mapper.map(e, NhanVienResponse.class);
    }
}
