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
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DiaChiKhachHangResponse> byKhachHang(Integer idKhachHang) {
        if (idKhachHang == null) {
            throw new BadRequestEx("Thiếu id_khach_hang");
        }

        return repo.findAllByIdKhachHangAndXoaMemFalseOrderByMacDinhDescIdDesc(idKhachHang)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DiaChiKhachHangResponse one(Integer id) {
        requireId(id);

        DiaChiKhachHang e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));

        return toResponse(e);
    }

    @Transactional
    public DiaChiKhachHangResponse create(DiaChiKhachHangRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        }

        DiaChiKhachHang e = mapper.map(req, DiaChiKhachHang.class);
        e.setId(null);

        trimSafe(e);
        validateForCreate(e);
        normalizeCreateFlags(e);

        // Nếu tạo mới và yêu cầu là mặc định, phải hạ mặc định cũ xuống trước khi save
        // để tránh đụng unique constraint / 409 conflict.
        if (!Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getMacDinh())) {
            clearDefaultOfOthers(e.getIdKhachHang(), null);
        }

        DiaChiKhachHang saved = repo.save(e);

        // Chuẩn hóa lại để luôn chỉ còn đúng 1 mặc định nếu còn địa chỉ sống
        if (!Boolean.TRUE.equals(saved.getXoaMem())) {
            ensureSingleDefault(saved.getIdKhachHang(), Boolean.TRUE.equals(saved.getMacDinh()) ? saved.getId() : null);
        }

        return toResponse(saved);
    }

    @Transactional
    public DiaChiKhachHangResponse update(Integer id, DiaChiKhachHangRequest req) {
        requireId(id);

        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        }

        DiaChiKhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));

        Integer idKhachHang = db.getIdKhachHang();

        // KHÔNG cho đổi idKhachHang khi update
        if (req.getTenDiaChi() != null) db.setTenDiaChi(req.getTenDiaChi());
        if (req.getThanhPho() != null) db.setThanhPho(req.getThanhPho());
        if (req.getQuan() != null) db.setQuan(req.getQuan());
        if (req.getPhuong() != null) db.setPhuong(req.getPhuong());
        if (req.getDiaChiCuThe() != null) db.setDiaChiCuThe(req.getDiaChiCuThe());

        trimSafe(db);
        validateForUpdate(db);

        boolean targetXoaMem = req.getXoaMem() != null ? req.getXoaMem() : Boolean.TRUE.equals(db.getXoaMem());
        Boolean reqMacDinh = req.getMacDinh();

        if (targetXoaMem) {
            // Soft delete thì không được là mặc định
            db.setXoaMem(true);
            db.setMacDinh(false);

            DiaChiKhachHang saved = repo.save(db);

            // Nếu còn địa chỉ sống khác thì tự bù 1 địa chỉ mặc định
            ensureSingleDefault(idKhachHang, null);
            return toResponse(saved);
        }

        db.setXoaMem(false);

        // Nếu request muốn set địa chỉ này thành mặc định,
        // phải hạ các địa chỉ khác xuống trước khi save.
        if (Boolean.TRUE.equals(reqMacDinh)) {
            clearDefaultOfOthers(idKhachHang, db.getId());
            db.setMacDinh(true);
        } else if (reqMacDinh != null) {
            db.setMacDinh(reqMacDinh);
        } else if (db.getMacDinh() == null) {
            db.setMacDinh(false);
        }

        DiaChiKhachHang saved = repo.save(db);

        // Chuẩn hóa:
        // - nếu vừa set true thì ưu tiên chính nó
        // - nếu vừa set false / dữ liệu cũ bị lệch thì tự sửa lại cho đúng invariant
        Integer preferredId = Boolean.TRUE.equals(saved.getMacDinh()) ? saved.getId() : null;
        ensureSingleDefault(idKhachHang, preferredId);

        // đọc lại để trả về đúng trạng thái mới nhất sau khi chuẩn hóa
        DiaChiKhachHang fresh = repo.findByIdAndXoaMemFalse(saved.getId())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + saved.getId()));

        return toResponse(fresh);
    }

    @Transactional
    public void delete(Integer id) {
        requireId(id);

        DiaChiKhachHang db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DiaChiKhachHang id=" + id));

        Integer idKhachHang = db.getIdKhachHang();

        db.setXoaMem(true);
        db.setMacDinh(false);
        repo.save(db);

        // Nếu còn địa chỉ sống khác thì luôn đảm bảo vẫn có đúng 1 mặc định
        ensureSingleDefault(idKhachHang, null);
    }

    private void normalizeCreateFlags(DiaChiKhachHang e) {
        if (e.getXoaMem() == null) {
            e.setXoaMem(false);
        }

        if (Boolean.TRUE.equals(e.getXoaMem())) {
            e.setMacDinh(false);
            return;
        }

        if (e.getMacDinh() == null) {
            e.setMacDinh(false);
        }
    }

    private void validateForCreate(DiaChiKhachHang e) {
        if (e.getIdKhachHang() == null) {
            throw new BadRequestEx("Thiếu id_khach_hang");
        }
        if (e.getTenDiaChi() == null || e.getTenDiaChi().isBlank()) {
            throw new BadRequestEx("Thiếu ten_dia_chi");
        }
    }

    private void validateForUpdate(DiaChiKhachHang e) {
        if (e.getIdKhachHang() == null) {
            throw new BadRequestEx("Thiếu id_khach_hang");
        }
        if (e.getTenDiaChi() == null || e.getTenDiaChi().isBlank()) {
            throw new BadRequestEx("Thiếu ten_dia_chi");
        }
    }

    private void requireId(Integer id) {
        if (id == null) {
            throw new BadRequestEx("Thiếu id");
        }
    }

    private void trimSafe(DiaChiKhachHang e) {
        if (e.getTenDiaChi() != null) e.setTenDiaChi(e.getTenDiaChi().trim());
        if (e.getThanhPho() != null) e.setThanhPho(e.getThanhPho().trim());
        if (e.getQuan() != null) e.setQuan(e.getQuan().trim());
        if (e.getPhuong() != null) e.setPhuong(e.getPhuong().trim());
        if (e.getDiaChiCuThe() != null) e.setDiaChiCuThe(e.getDiaChiCuThe().trim());
    }

    /**
     * Hạ tất cả địa chỉ mặc định khác xuống false trước khi set 1 địa chỉ thành mặc định.
     * Làm trước khi save địa chỉ đích để tránh unique constraint violation.
     */
    private void clearDefaultOfOthers(Integer idKhachHang, Integer keepId) {
        List<DiaChiKhachHang> list = repo.findAllByIdKhachHangAndXoaMemFalseOrderByMacDinhDescIdDesc(idKhachHang);

        List<DiaChiKhachHang> toUpdate = list.stream()
                .filter(e -> keepId == null || !e.getId().equals(keepId))
                .filter(e -> Boolean.TRUE.equals(e.getMacDinh()))
                .toList();

        if (toUpdate.isEmpty()) {
            return;
        }

        toUpdate.forEach(e -> e.setMacDinh(false));
        repo.saveAll(toUpdate);
        repo.flush();
    }

    /**
     * Đảm bảo sau mỗi thao tác:
     * - nếu không còn địa chỉ sống => không làm gì
     * - nếu còn địa chỉ sống => luôn có đúng 1 địa chỉ mặc định
     * preferredId dùng để ưu tiên 1 địa chỉ cụ thể làm mặc định khi cần.
     */
    private void ensureSingleDefault(Integer idKhachHang, Integer preferredId) {
        List<DiaChiKhachHang> live = repo.findAllByIdKhachHangAndXoaMemFalseOrderByMacDinhDescIdDesc(idKhachHang);

        if (live.isEmpty()) {
            return;
        }

        DiaChiKhachHang chosen = chooseDefaultAddress(live, preferredId);
        long defaultCount = live.stream().filter(e -> Boolean.TRUE.equals(e.getMacDinh())).count();

        boolean alreadyValid = defaultCount == 1
                && chosen != null
                && Boolean.TRUE.equals(chosen.getMacDinh());

        if (alreadyValid) {
            return;
        }

        List<DiaChiKhachHang> currentDefaults = live.stream()
                .filter(e -> Boolean.TRUE.equals(e.getMacDinh()))
                .toList();

        if (!currentDefaults.isEmpty()) {
            currentDefaults.forEach(e -> e.setMacDinh(false));
            repo.saveAll(currentDefaults);
            repo.flush();
        }

        if (chosen != null && !Boolean.TRUE.equals(chosen.getMacDinh())) {
            chosen.setMacDinh(true);
            repo.save(chosen);
        }
    }

    private DiaChiKhachHang chooseDefaultAddress(List<DiaChiKhachHang> live, Integer preferredId) {
        if (preferredId != null) {
            for (DiaChiKhachHang e : live) {
                if (preferredId.equals(e.getId())) {
                    return e;
                }
            }
        }

        for (DiaChiKhachHang e : live) {
            if (Boolean.TRUE.equals(e.getMacDinh())) {
                return e;
            }
        }

        // Không có mặc định nào thì chọn bản ghi đầu tiên còn sống
        return live.get(0);
    }

    private DiaChiKhachHangResponse toResponse(DiaChiKhachHang e) {
        return mapper.map(e, DiaChiKhachHangResponse.class);
    }
}