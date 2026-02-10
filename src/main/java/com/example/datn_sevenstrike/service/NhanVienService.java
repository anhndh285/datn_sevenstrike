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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepository repo;
    private final ModelMapper mapper;
    private final FileStorageService fileStorageService;

    public List<NhanVienResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

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

    // ========= CREATE =========

    @Transactional
    public NhanVienResponse create(NhanVienRequest req) {
        return create(req, null);
    }

    @Transactional
    public NhanVienResponse create(NhanVienRequest req, MultipartFile file) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        NhanVien e = mapper.map(req, NhanVien.class);
        e.setId(null);

        applyDefaults(e, true);

        // upload avatar
        if (file != null && !file.isEmpty()) {
            String url = fileStorageService.saveNhanVienAvatar(file);
            e.setAnhNhanVien(url);
        }

        validate(e);
        validateDuplicateCreate(e);

        return toResponse(repo.save(e));
    }

    // ========= UPDATE =========

    @Transactional
    public NhanVienResponse update(Integer id, NhanVienRequest req) {
        return update(id, req, null);
    }

    @Transactional
    public NhanVienResponse update(Integer id, NhanVienRequest req, MultipartFile file) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        NhanVien db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy NhanVien id=" + id));

        if (req.getTenNhanVien() != null) db.setTenNhanVien(req.getTenNhanVien());
        if (req.getTenTaiKhoan() != null) db.setTenTaiKhoan(req.getTenTaiKhoan());
        if (req.getMatKhau() != null) db.setMatKhau(req.getMatKhau());
        if (req.getEmail() != null) db.setEmail(req.getEmail());
        if (req.getSoDienThoai() != null) db.setSoDienThoai(req.getSoDienThoai());
        if (req.getNgaySinh() != null) db.setNgaySinh(req.getNgaySinh());
        if (req.getGhiChu() != null) db.setGhiChu(req.getGhiChu());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getIdQuyenHan() != null) db.setIdQuyenHan(req.getIdQuyenHan());

        if (req.getThanhPho() != null) db.setThanhPho(req.getThanhPho());
        if (req.getQuan() != null) db.setQuan(req.getQuan());
        if (req.getPhuong() != null) db.setPhuong(req.getPhuong());
        if (req.getDiaChiCuThe() != null) db.setDiaChiCuThe(req.getDiaChiCuThe());

        // ✅ CCCD: BỎ – không lưu, không update
        // if (req.getCccd() != null) db.setCccd(req.getCccd()); // bỏ

        if (file != null && !file.isEmpty()) {
            // optional: xóa ảnh cũ
            fileStorageService.tryDeleteByPublicPath(db.getAnhNhanVien());

            String url = fileStorageService.saveNhanVienAvatar(file);
            db.setAnhNhanVien(url);
        }

        if (req.getNguoiCapNhat() != null) db.setNguoiCapNhat(req.getNguoiCapNhat());
        db.setNgayCapNhat(LocalDateTime.now());

        applyDefaults(db, false);
        validate(db);
        validateDuplicateUpdate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        NhanVien db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy NhanVien id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        if (db.getNguoiCapNhat() == null) db.setNguoiCapNhat(1);
        repo.save(db);
    }

    private void applyDefaults(NhanVien e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);

        LocalDateTime now = LocalDateTime.now();
        if (createMode && e.getNgayTao() == null) e.setNgayTao(now);
        if (createMode) e.setNgayCapNhat(null);
        if (!createMode && e.getNgayCapNhat() == null) e.setNgayCapNhat(now);

        if (e.getGhiChu() == null) e.setGhiChu("");
        if (e.getNguoiTao() == null) e.setNguoiTao(1);
        if (e.getNguoiCapNhat() == null && !createMode) e.setNguoiCapNhat(1);
    }

    private void validate(NhanVien e) {
        if (e.getTenTaiKhoan() == null || e.getTenTaiKhoan().isBlank())
            throw new BadRequestEx("Thiếu ten_tai_khoan");
        if (e.getTenNhanVien() == null || e.getTenNhanVien().isBlank())
            throw new BadRequestEx("Thiếu ten_nhan_vien");
        if (e.getEmail() == null || e.getEmail().isBlank())
            throw new BadRequestEx("Thiếu email");
    }

    private void validateDuplicateCreate(NhanVien e) {
        if (e.getTenTaiKhoan() != null && repo.existsByTenTaiKhoanAndXoaMemFalse(e.getTenTaiKhoan())) {
            throw new BadRequestEx("Tên tài khoản đã tồn tại");
        }
        if (e.getEmail() != null && repo.existsByEmailAndXoaMemFalse(e.getEmail())) {
            throw new BadRequestEx("Email đã tồn tại");
        }
    }

    private void validateDuplicateUpdate(NhanVien e) {
        if (e.getTenTaiKhoan() != null && repo.existsByTenTaiKhoanAndXoaMemFalseAndIdNot(e.getTenTaiKhoan(), e.getId())) {
            throw new BadRequestEx("Tên tài khoản đã tồn tại");
        }
        if (e.getEmail() != null && repo.existsByEmailAndXoaMemFalseAndIdNot(e.getEmail(), e.getId())) {
            throw new BadRequestEx("Email đã tồn tại");
        }
    }

    private NhanVienResponse toResponse(NhanVien e) {
        return mapper.map(e, NhanVienResponse.class);
    }
}
