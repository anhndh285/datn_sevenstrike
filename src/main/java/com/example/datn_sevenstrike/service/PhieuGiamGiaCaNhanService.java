package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaCaNhanRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaCaNhanResponse;
import com.example.datn_sevenstrike.entity.PhieuGiamGiaCaNhan;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaCaNhanRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhieuGiamGiaCaNhanService {

    private final PhieuGiamGiaCaNhanRepository repo;
    private final ModelMapper mapper;

    public List<PhieuGiamGiaCaNhanResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<PhieuGiamGiaCaNhanResponse> byKhachHang(Integer idKhachHang) {
        if (idKhachHang == null) throw new BadRequestEx("Thiếu id_khach_hang");
        return repo.findAllByIdKhachHangAndXoaMemFalseOrderByIdDesc(idKhachHang)
                .stream().map(this::toResponse).toList();
    }

    public List<PhieuGiamGiaCaNhanResponse> myAvailable(Integer idKhachHang) {
        if (idKhachHang == null) throw new BadRequestEx("Thiếu id_khach_hang");
        return repo.findAllByIdKhachHangAndDaSuDungFalseAndXoaMemFalseOrderByIdDesc(idKhachHang)
                .stream().map(this::toResponse).toList();
    }

    public PhieuGiamGiaCaNhanResponse one(Integer id) {
        PhieuGiamGiaCaNhan e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGiaCaNhan id=" + id));
        return toResponse(e);
    }

    @Transactional
    public PhieuGiamGiaCaNhanResponse create(PhieuGiamGiaCaNhanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        PhieuGiamGiaCaNhan e = mapper.map(req, PhieuGiamGiaCaNhan.class);
        e.setId(null);

        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getDaSuDung() == null) e.setDaSuDung(false);
        if (e.getNgayNhan() == null) e.setNgayNhan(LocalDate.now());

        validate(e);

        if (repo.existsByIdKhachHangAndIdPhieuGiamGiaAndXoaMemFalse(e.getIdKhachHang(), e.getIdPhieuGiamGia())) {
            throw new BadRequestEx("Khách đã có voucher này (xoa_mem=0)");
        }

        return toResponse(repo.save(e));
    }

    @Transactional
    public PhieuGiamGiaCaNhanResponse update(Integer id, PhieuGiamGiaCaNhanRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        PhieuGiamGiaCaNhan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGiaCaNhan id=" + id));

        Integer newKh = (req.getIdKhachHang() != null) ? req.getIdKhachHang() : db.getIdKhachHang();
        Integer newPgg = (req.getIdPhieuGiamGia() != null) ? req.getIdPhieuGiamGia() : db.getIdPhieuGiamGia();

        boolean changed = !newKh.equals(db.getIdKhachHang()) || !newPgg.equals(db.getIdPhieuGiamGia());
        if (changed && repo.existsByIdKhachHangAndIdPhieuGiamGiaAndXoaMemFalse(newKh, newPgg)) {
            throw new BadRequestEx("Khách đã có voucher này (xoa_mem=0)");
        }

        if (req.getIdKhachHang() != null) db.setIdKhachHang(req.getIdKhachHang());
        if (req.getIdPhieuGiamGia() != null) db.setIdPhieuGiamGia(req.getIdPhieuGiamGia());
        if (req.getNgayNhan() != null) db.setNgayNhan(req.getNgayNhan());
        if (req.getDaSuDung() != null) db.setDaSuDung(req.getDaSuDung());

        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());
        if (db.getXoaMem() == null) db.setXoaMem(false);

        validate(db);
        return toResponse(repo.save(db));
    }

    // ✅ dùng khi checkout/đặt hàng: đánh dấu đã dùng
    @Transactional
    public PhieuGiamGiaCaNhanResponse markUsed(Integer id) {
        PhieuGiamGiaCaNhan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGiaCaNhan id=" + id));
        db.setDaSuDung(true);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        PhieuGiamGiaCaNhan db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGiaCaNhan id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void validate(PhieuGiamGiaCaNhan e) {
        if (e.getIdKhachHang() == null) throw new BadRequestEx("Thiếu id_khach_hang");
        if (e.getIdPhieuGiamGia() == null) throw new BadRequestEx("Thiếu id_phieu_giam_gia");
        if (e.getDaSuDung() == null) e.setDaSuDung(false);
        if (e.getXoaMem() == null) e.setXoaMem(false);
    }

    private PhieuGiamGiaCaNhanResponse toResponse(PhieuGiamGiaCaNhan e) {
        return mapper.map(e, PhieuGiamGiaCaNhanResponse.class);
    }
}
