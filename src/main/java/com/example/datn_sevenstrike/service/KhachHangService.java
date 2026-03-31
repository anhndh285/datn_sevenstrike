package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.KhachHangRequest;
import com.example.datn_sevenstrike.dto.response.KhachHangResponse;
import com.example.datn_sevenstrike.entity.KhachHang;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.KhachHangRepository;
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
public class KhachHangService {

    private final KhachHangRepository repo;
    private final ModelMapper mapper;
    private final TaiKhoanEmailService emailService;

    // =========================
    // ADMIN: dùng cho màn quản lý khách hàng
    // =========================
    public List<KhachHangResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<KhachHangResponse> page(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);

        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));
        return repo.findAllByXoaMemFalse(pageable).map(this::toResponse);
    }

    public KhachHangResponse one(Integer id) {
        KhachHang e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KhachHang id=" + id));
        return toResponse(e);
    }

    // =========================
    // POS: chỉ lấy khách hàng còn hoạt động
    // =========================
    public List<KhachHangResponse> allActiveForPos() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<KhachHangResponse> pageActiveForPos(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);

        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));
        return repo.findAllByXoaMemFalseAndTrangThaiTrue(pageable).map(this::toResponse);
    }

    public KhachHangResponse oneActiveForPos(Integer id) {
        KhachHang e = repo.findByIdAndXoaMemFalseAndTrangThaiTrue(id)
                .orElseThrow(() -> new NotFoundEx("Khách hàng đã ngừng hoạt động hoặc không còn hợp lệ"));
        return toResponse(e);
    }

    @Transactional
    public KhachHangResponse create(KhachHangRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        KhachHang e = mapper.map(req, KhachHang.class);
        e.setId(null);

        applyDefaults(e, true);
        validate(e);
        validateDuplicateCreate(e);

        String rawPassword = req.getMatKhau();

        KhachHang saved = repo.save(e);

        if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
            emailService.sendKhachHangEmail(saved.getEmail(), saved, rawPassword);
        }

        return toResponse(saved);
    }

    @Transactional
    public KhachHangResponse update(Integer id, KhachHangRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        KhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KhachHang id=" + id));

        String oldTenTaiKhoan = db.getTenTaiKhoan();
        String oldEmail = db.getEmail();

        if (req.getTenKhachHang() != null) db.setTenKhachHang(req.getTenKhachHang());
        if (req.getTenTaiKhoan() != null) db.setTenTaiKhoan(req.getTenTaiKhoan());
        if (req.getMatKhau() != null) db.setMatKhau(req.getMatKhau());
        if (req.getEmail() != null) db.setEmail(req.getEmail());
        if (req.getSoDienThoai() != null) db.setSoDienThoai(req.getSoDienThoai());
        if (req.getGioiTinh() != null) db.setGioiTinh(req.getGioiTinh());
        if (req.getNgaySinh() != null) db.setNgaySinh(req.getNgaySinh());
        if (req.getAnhDaiDien() != null) db.setAnhDaiDien(req.getAnhDaiDien());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());

        if (req.getNguoiCapNhat() != null) db.setNguoiCapNhat(req.getNguoiCapNhat());
        db.setNgayCapNhat(LocalDateTime.now());

        applyDefaults(db, false);
        validate(db);
        validateDuplicateUpdate(db, oldTenTaiKhoan, oldEmail);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        KhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KhachHang id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        if (db.getNguoiCapNhat() == null) db.setNguoiCapNhat(1);
        repo.save(db);
    }

    private void applyDefaults(KhachHang e, boolean createMode) {
        if (e.getXoaMem() == null) e.setXoaMem(false);

        if (createMode && e.getTrangThai() == null) e.setTrangThai(true);

        LocalDateTime now = LocalDateTime.now();
        if (createMode && e.getNgayTao() == null) e.setNgayTao(now);
        if (!createMode && e.getNgayTao() == null) {
            e.setNgayTao(now);
        }
    }

    private void validate(KhachHang e) {
        if (e.getTenKhachHang() == null || e.getTenKhachHang().isBlank()) {
            throw new BadRequestEx("Thiếu ten_khach_hang");
        }

        if (e.getTenTaiKhoan() == null || e.getTenTaiKhoan().isBlank()) {
            throw new BadRequestEx("Thiếu ten_tai_khoan");
        }

        if (e.getEmail() == null || e.getEmail().isBlank()) {
            throw new BadRequestEx("Thiếu email");
        }
    }

    private void validateDuplicateCreate(KhachHang e) {
        if (e.getTenTaiKhoan() != null && repo.existsByTenTaiKhoanAndXoaMemFalse(e.getTenTaiKhoan())) {
            throw new BadRequestEx("Tên tài khoản đã tồn tại");
        }
        if (e.getEmail() != null && repo.existsByEmailAndXoaMemFalse(e.getEmail())) {
            throw new BadRequestEx("Email đã tồn tại");
        }
    }

    private void validateDuplicateUpdate(KhachHang e, String oldTenTaiKhoan, String oldEmail) {
        if (e.getTenTaiKhoan() != null && !e.getTenTaiKhoan().equals(oldTenTaiKhoan)) {
            if (repo.existsByTenTaiKhoanAndXoaMemFalseAndIdNot(e.getTenTaiKhoan(), e.getId())) {
                throw new BadRequestEx("Tên tài khoản đã tồn tại");
            }
        }

        if (e.getEmail() != null && !e.getEmail().equals(oldEmail)) {
            if (repo.existsByEmailAndXoaMemFalseAndIdNot(e.getEmail(), e.getId())) {
                throw new BadRequestEx("Email đã tồn tại");
            }
        }
    }

    private KhachHangResponse toResponse(KhachHang e) {
        return mapper.map(e, KhachHangResponse.class);
    }
}