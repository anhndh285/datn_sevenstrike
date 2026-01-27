package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.NhanVienRequest;
import com.example.datn_sevenstrike.dto.response.NhanVienResponse;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepository repo;
    private final ModelMapper mapper;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

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

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getNgayTao() == null) e.setNgayTao(LocalDateTime.now());
        e.setNgayCapNhat(null);

        if (file != null && !file.isEmpty()) {
            String url = saveNhanVienImage(file);
            e.setAnhNhanVien(url); // dạng "/uploads/nhan_vien/xxx.png"
        }

        validate(e);
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
        if (req.getCccd() != null) db.setCccd(req.getCccd());

        if (file != null && !file.isEmpty()) {
            String url = saveNhanVienImage(file);
            db.setAnhNhanVien(url);
        }

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

    // ========= SAVE FILE =========
    private String saveNhanVienImage(MultipartFile file) {
        try {
            // uploads/nhan_vien/
            Path dir = Paths.get(uploadDir, "nhan_vien").toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String original = (file.getOriginalFilename() == null) ? "file" : file.getOriginalFilename();
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0 && dot < original.length() - 1) ext = original.substring(dot);

            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = dir.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // StaticResourceConfig map "/uploads/**" -> folder uploads
            return "/uploads/nhan_vien/" + filename;
        } catch (IOException ex) {
            throw new BadRequestEx("Upload ảnh thất bại: " + ex.getMessage());
        }
    }
}
