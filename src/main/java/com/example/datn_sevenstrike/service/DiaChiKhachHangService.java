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

    public List<DiaChiKhachHangResponse> byKhachHang(Integer idKhachHang) {
        if (idKhachHang == null) throw new BadRequestEx("Thiếu id_khach_hang");
        return repo.findAllByIdKhachHangAndXoaMemFalseOrderByMacDinhDescIdDesc(idKhachHang)
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

        // ✅ theo DB: xoa_mem NOT NULL default 0; mac_dinh NOT NULL default 0
        if (e.getXoaMem() == null) e.setXoaMem(false);

        // macDinh: nếu null -> xử lý mềm: nếu KH chưa có default => auto true, còn lại false
        if (e.getMacDinh() == null) {
            boolean hasDefault = repo.findFirstByIdKhachHangAndMacDinhTrueAndXoaMemFalse(e.getIdKhachHang()).isPresent();
            e.setMacDinh(!hasDefault);
        }

        trimSafe(e);
        validateByDb(e);

        DiaChiKhachHang saved = repo.save(e);

        // ✅ nếu địa chỉ mới là mặc định -> gỡ mặc định của các địa chỉ khác cùng KH
        if (Boolean.TRUE.equals(saved.getMacDinh())) {
            repo.unsetDefaultOthers(saved.getIdKhachHang(), saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public DiaChiKhachHangResponse update(Integer id, DiaChiKhachHangRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        DiaChiKhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));

        // ✅ chỉ set field nếu req có gửi (đúng kiểu partial update)
        if (req.getIdKhachHang() != null) db.setIdKhachHang(req.getIdKhachHang());
        if (req.getTenDiaChi() != null) db.setTenDiaChi(req.getTenDiaChi());
        if (req.getThanhPho() != null) db.setThanhPho(req.getThanhPho());
        if (req.getQuan() != null) db.setQuan(req.getQuan());
        if (req.getPhuong() != null) db.setPhuong(req.getPhuong());
        if (req.getDiaChiCuThe() != null) db.setDiaChiCuThe(req.getDiaChiCuThe());
        if (req.getMacDinh() != null) db.setMacDinh(req.getMacDinh());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem()); // thường không cho FE set, nhưng bạn đang có field

        // ✅ đảm bảo NOT NULL theo DB
        if (db.getXoaMem() == null) db.setXoaMem(false);
        if (db.getMacDinh() == null) db.setMacDinh(false);

        trimSafe(db);
        validateByDb(db);

        DiaChiKhachHang saved = repo.save(db);

        // ✅ nếu set mặc định -> gỡ mặc định các địa chỉ khác
        if (Boolean.TRUE.equals(saved.getMacDinh())) {
            repo.unsetDefaultOthers(saved.getIdKhachHang(), saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        DiaChiKhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));

        db.setXoaMem(true);
        // nếu xóa địa chỉ mặc định, KH sẽ tạm thời không có default (DB cho phép)
        // Nếu bạn muốn auto chuyển default sang cái khác => nói mình, mình thêm 5 dòng xử lý.
        repo.save(db);
    }

    // ====== VALIDATE theo DB (chỉ bắt buộc NOT NULL trong schema) ======
    private void validateByDb(DiaChiKhachHang e) {
        if (e.getIdKhachHang() == null) throw new BadRequestEx("Thiếu id_khach_hang");

        if (e.getTenDiaChi() == null || e.getTenDiaChi().isBlank()) {
            throw new BadRequestEx("Thiếu ten_dia_chi");
        }

        // thanhPho/quan/phuong/diaChiCuThe: DB cho phép null -> KHÔNG validate bắt buộc
        // macDinh/xoaMem: DB NOT NULL -> đảm bảo không null ở trên
    }

    private void trimSafe(DiaChiKhachHang e) {
        if (e.getTenDiaChi() != null) e.setTenDiaChi(e.getTenDiaChi().trim());
        if (e.getThanhPho() != null) e.setThanhPho(e.getThanhPho().trim());
        if (e.getQuan() != null) e.setQuan(e.getQuan().trim());
        if (e.getPhuong() != null) e.setPhuong(e.getPhuong().trim());
        if (e.getDiaChiCuThe() != null) e.setDiaChiCuThe(e.getDiaChiCuThe().trim());
    }

    private DiaChiKhachHangResponse toResponse(DiaChiKhachHang e) {
        return mapper.map(e, DiaChiKhachHangResponse.class);
    }
}
