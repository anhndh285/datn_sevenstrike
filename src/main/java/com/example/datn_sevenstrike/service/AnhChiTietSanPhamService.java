package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.AnhChiTietSanPhamRequest;
import com.example.datn_sevenstrike.dto.response.AnhChiTietSanPhamResponse;
import com.example.datn_sevenstrike.entity.AnhChiTietSanPham;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.AnhChiTietSanPhamRepository;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AnhChiTietSanPhamService {

    private final AnhChiTietSanPhamRepository repo;
    private final ModelMapper mapper;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public List<AnhChiTietSanPhamResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public AnhChiTietSanPhamResponse one(Integer id) {
        AnhChiTietSanPham e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ảnh CTSP id=" + id));
        return toResponse(e);
    }

    public List<AnhChiTietSanPhamResponse> byChiTietSanPham(Integer idChiTietSanPham) {
        if (idChiTietSanPham == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham");
        return repo.findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(idChiTietSanPham)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AnhChiTietSanPhamResponse create(AnhChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        AnhChiTietSanPham e = mapper.map(req, AnhChiTietSanPham.class);
        e.setId(null);

        applyDefaults(e);

        // nếu tạo mới ảnh đại diện -> unset ảnh đại diện cũ trước (tránh unique index)
        if (Boolean.TRUE.equals(e.getLaAnhDaiDien())) {
            repo.unsetDaiDien(e.getIdChiTietSanPham());
        }

        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public AnhChiTietSanPhamResponse update(Integer id, AnhChiTietSanPhamRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        AnhChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ảnh CTSP id=" + id));

        Integer oldCtsp = db.getIdChiTietSanPham();

        if (req.getIdChiTietSanPham() != null) db.setIdChiTietSanPham(req.getIdChiTietSanPham());
        if (req.getDuongDanAnh() != null) db.setDuongDanAnh(req.getDuongDanAnh());
        if (req.getMoTa() != null) db.setMoTa(req.getMoTa());

        // xử lý set đại diện
        if (req.getLaAnhDaiDien() != null) {
            if (Boolean.TRUE.equals(req.getLaAnhDaiDien())) {
                if (db.getIdChiTietSanPham() == null) throw new BadRequestEx("Ảnh chưa có id_chi_tiet_san_pham");
                repo.unsetDaiDienExcept(db.getIdChiTietSanPham(), db.getId());
                db.setLaAnhDaiDien(true);
            } else {
                db.setLaAnhDaiDien(false);
            }
        }

        // Nếu đổi CTSP mà ảnh đang là đại diện -> phải unset đại diện ở CTSP mới trước (an toàn)
        if (oldCtsp != null && db.getIdChiTietSanPham() != null
                && !oldCtsp.equals(db.getIdChiTietSanPham())
                && Boolean.TRUE.equals(db.getLaAnhDaiDien())) {
            repo.unsetDaiDienExcept(db.getIdChiTietSanPham(), db.getId());
        }

        applyDefaults(db);
        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        AnhChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ảnh CTSP id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    @Transactional
    public AnhChiTietSanPhamResponse setDaiDien(Integer id) {
        AnhChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ảnh CTSP id=" + id));

        Integer idCtsp = db.getIdChiTietSanPham();
        if (idCtsp == null) throw new BadRequestEx("Ảnh chưa có id_chi_tiet_san_pham");

        repo.unsetDaiDienExcept(idCtsp, db.getId());
        db.setLaAnhDaiDien(true);

        applyDefaults(db);
        validate(db);
        return toResponse(repo.save(db));
    }

    // Upload ảnh mới + tạo record
    @Transactional
    public AnhChiTietSanPhamResponse upload(Integer idChiTietSanPham, MultipartFile file, boolean laAnhDaiDien, String moTa) {
        if (idChiTietSanPham == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham");
        if (file == null || file.isEmpty()) throw new BadRequestEx("File rỗng");

        ensureImage(file);

        String publicPath = saveFileToUploads(idChiTietSanPham, file);

        if (laAnhDaiDien) {
            repo.unsetDaiDien(idChiTietSanPham);
        }

        AnhChiTietSanPham e = new AnhChiTietSanPham();
        e.setId(null);
        e.setIdChiTietSanPham(idChiTietSanPham);
        e.setDuongDanAnh(publicPath);
        e.setLaAnhDaiDien(laAnhDaiDien);
        e.setMoTa(moTa == null ? "" : moTa);
        e.setXoaMem(false);

        applyDefaults(e);
        validate(e);
        return toResponse(repo.save(e));
    }

    // Replace file ảnh theo id ảnh
    @Transactional
    public AnhChiTietSanPhamResponse uploadUpdate(Integer id, MultipartFile file, Boolean laAnhDaiDien, String moTa) {
        if (file == null || file.isEmpty()) throw new BadRequestEx("File rỗng");

        AnhChiTietSanPham db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ảnh CTSP id=" + id));

        Integer idCtsp = db.getIdChiTietSanPham();
        if (idCtsp == null) throw new BadRequestEx("Ảnh chưa có id_chi_tiet_san_pham");

        ensureImage(file);

        String publicPath = saveFileToUploads(idCtsp, file);
        db.setDuongDanAnh(publicPath);

        if (moTa != null) db.setMoTa(moTa);

        if (laAnhDaiDien != null) {
            if (Boolean.TRUE.equals(laAnhDaiDien)) {
                repo.unsetDaiDienExcept(idCtsp, db.getId());
                db.setLaAnhDaiDien(true);
            } else {
                db.setLaAnhDaiDien(false);
            }
        }

        applyDefaults(db);
        validate(db);
        return toResponse(repo.save(db));
    }

    private void ensureImage(MultipartFile file) {
        String ct = file.getContentType();
        if (ct != null && !ct.toLowerCase().startsWith("image/")) {
            throw new BadRequestEx("Chỉ cho phép upload ảnh");
        }
    }

    private String saveFileToUploads(Integer idChiTietSanPham, MultipartFile file) {
        Path dir = Paths.get(uploadDir, "ctsp", String.valueOf(idChiTietSanPham))
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new BadRequestEx("Không tạo được thư mục upload: " + e.getMessage());
        }

        String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) ext = original.substring(dot);

        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestEx("Lưu file thất bại: " + e.getMessage());
        }

        return "/uploads/ctsp/" + idChiTietSanPham + "/" + filename;
    }

    private void applyDefaults(AnhChiTietSanPham e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getLaAnhDaiDien() == null) e.setLaAnhDaiDien(false);
        if (e.getMoTa() == null) e.setMoTa("");
    }

    private void validate(AnhChiTietSanPham e) {
        if (e.getIdChiTietSanPham() == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham");
        if (e.getDuongDanAnh() == null || e.getDuongDanAnh().isBlank())
            throw new BadRequestEx("Thiếu duong_dan_anh");
        applyDefaults(e);
    }

    private AnhChiTietSanPhamResponse toResponse(AnhChiTietSanPham e) {
        return mapper.map(e, AnhChiTietSanPhamResponse.class);
    }
}
