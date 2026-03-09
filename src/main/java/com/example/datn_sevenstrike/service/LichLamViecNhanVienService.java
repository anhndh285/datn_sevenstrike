package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LichLamViecNhanVienRequest;
import com.example.datn_sevenstrike.dto.response.LichLamViecNhanVienResponse;
import com.example.datn_sevenstrike.entity.LichLamViec;
import com.example.datn_sevenstrike.entity.LichLamViecNhanVien;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.LichLamViecNhanVienRepository;
import com.example.datn_sevenstrike.repository.LichLamViecRepository;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LichLamViecNhanVienService {

    private final LichLamViecNhanVienRepository repo;
    private final LichLamViecRepository lichRepo;
    private final NhanVienRepository nvRepo;

    public List<LichLamViecNhanVienResponse> getByNhanVien(Integer idNhanVien, LocalDate ngayLam) {
        return repo.findAllByNhanVienAndNgayLam(idNhanVien, ngayLam)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LichLamViecNhanVienResponse> getByLich(Integer idLich) {
        return repo.findAllByLichId(idLich)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LichLamViecNhanVienResponse> all() {
        return repo.findAllActiveOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LichLamViecNhanVienResponse create(LichLamViecNhanVienRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu phân công");
        }
        if (req.getIdLichLamViec() == null) {
            throw new BadRequestEx("Lịch làm việc không được để trống");
        }
        if (req.getIdNhanVien() == null) {
            throw new BadRequestEx("Nhân viên không được để trống");
        }

        LichLamViec lich = lichRepo.findByIdAndXoaMemFalse(req.getIdLichLamViec())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy lịch làm việc"));

        NhanVien nv = nvRepo.findByIdAndXoaMemFalse(req.getIdNhanVien())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy nhân viên"));

        if (Integer.valueOf(1).equals(nv.getIdQuyenHan())) {
            throw new BadRequestEx("Không thể phân công tài khoản ADMIN vào lịch làm việc");
        }

        if (repo.existsByLichLamViecAndNhanVien(req.getIdLichLamViec(), req.getIdNhanVien())) {
            throw new BadRequestEx("Nhân viên " + nv.getTenNhanVien() + " đã được phân công vào lịch này rồi");
        }

        LichLamViecNhanVien entity = new LichLamViecNhanVien();
        entity.setId(null);
        entity.setLichLamViec(lich);
        entity.setNhanVien(nv);
        entity.setNgayTao(Instant.now());
        entity.setNguoiTao(req.getNguoiTao());
        entity.setXoaMem(false);

        return toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        LichLamViecNhanVien entity = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy bản ghi phân công"));
        entity.setXoaMem(true);
        repo.save(entity);
    }

    @Transactional
    public List<LichLamViecNhanVienResponse> importExcel(MultipartFile file) {
        throw new BadRequestEx("Vui lòng dùng API /api/admin/lich-lam-viec/import-excel để import file lịch làm việc");
    }

    private LichLamViecNhanVienResponse toResponse(LichLamViecNhanVien e) {
        LichLamViecNhanVienResponse res = new LichLamViecNhanVienResponse();
        res.setId(e.getId());
        res.setXoaMem(e.getXoaMem());

        if (e.getLichLamViec() != null) {
            res.setLichLamViec(e.getLichLamViec());
            res.setIdLichLamViec(e.getLichLamViec().getId());
            res.setNgayLam(e.getLichLamViec().getNgayLam());

            if (e.getLichLamViec().getIdCaLam() != null) {
                res.setIdCaLam(e.getLichLamViec().getIdCaLam().getId());
                res.setTenCa(e.getLichLamViec().getIdCaLam().getTenCa());
                res.setGioBatDau(e.getLichLamViec().getIdCaLam().getGioBatDau());
                res.setGioKetThuc(e.getLichLamViec().getIdCaLam().getGioKetThuc());
            }
        }

        if (e.getNhanVien() != null) {
            res.setNhanVien(e.getNhanVien());
            res.setMaNhanVien(e.getNhanVien().getMaNhanVien());
            res.setTenNhanVien(e.getNhanVien().getTenNhanVien());
            res.setTenTaiKhoan(e.getNhanVien().getTenTaiKhoan());
        }

        return res;
    }
}