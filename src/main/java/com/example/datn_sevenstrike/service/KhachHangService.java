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

    public List<KhachHangResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    // ✅ NEW: Paging
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

    @Transactional
    public KhachHangResponse create(KhachHangRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        KhachHang e = mapper.map(req, KhachHang.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getNgayTao() == null) e.setNgayTao(LocalDateTime.now());
        e.setNgayCapNhat(null);

        validate(e);
        return toResponse(repo.save(e));
    }

    @Transactional
    public KhachHangResponse update(Integer id, KhachHangRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        KhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KhachHang id=" + id));

        if (req.getTenKhachHang() != null) db.setTenKhachHang(req.getTenKhachHang());
        if (req.getTenTaiKhoan() != null) db.setTenTaiKhoan(req.getTenTaiKhoan());
        if (req.getMatKhau() != null) db.setMatKhau(req.getMatKhau());
        if (req.getEmail() != null) db.setEmail(req.getEmail());
        if (req.getSoDienThoai() != null) db.setSoDienThoai(req.getSoDienThoai());
        if (req.getGioiTinh() != null) db.setGioiTinh(req.getGioiTinh());
        if (req.getNgaySinh() != null) db.setNgaySinh(req.getNgaySinh());

        // khuyến nghị: không update nguoiTao/ngayTao
        if (req.getNguoiCapNhat() != null) db.setNguoiCapNhat(req.getNguoiCapNhat());

        db.setNgayCapNhat(LocalDateTime.now());

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        KhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy KhachHang id=" + id));
        db.setXoaMem(true);
        db.setNgayCapNhat(LocalDateTime.now());
        repo.save(db);
    }

    private void validate(KhachHang e) {
        if (e.getTenKhachHang() == null || e.getTenKhachHang().isBlank()) {
            throw new BadRequestEx("Thiếu ten_khach_hang");
        }
    }

    private KhachHangResponse toResponse(KhachHang e) {
        return mapper.map(e, KhachHangResponse.class);
    }
}
