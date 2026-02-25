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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LichLamViecNhanVienService {

    private final LichLamViecNhanVienRepository repo;
    private final LichLamViecRepository lichRepo;
    private final NhanVienRepository nvRepo;
    private final ModelMapper mapper;

    public List<LichLamViecNhanVienResponse> getByNhanVien(Integer idNhanVien, LocalDate ngayLam) {
        return repo.findAllByNhanVienAndNgayLam(idNhanVien, ngayLam).stream().map(this::toResponse).toList();
    }

    public List<LichLamViecNhanVienResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public LichLamViecNhanVienResponse create(LichLamViecNhanVienRequest req) {
        // 1. Check tồn tại
        LichLamViec lich = lichRepo.findByIdAndXoaMemFalse(req.getIdLichLamViec())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy lịch làm việc!"));
        NhanVien nv = nvRepo.findByIdAndXoaMemFalse(req.getIdNhanVien())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy nhân viên!"));

        // 2. Check trùng (Nhân viên này đã có trong lịch này chưa)
        if (repo.findByLichAndNhanVien(req.getIdLichLamViec(), req.getIdNhanVien()).isPresent()) {
            throw new BadRequestEx("Nhân viên " + nv.getTenNhanVien() + " đã được phân công vào lịch này rồi!");
        }

        LichLamViecNhanVien entity = new LichLamViecNhanVien();
        entity.setId(null);
        entity.setLichLamViec(lich);
        entity.setNhanVien(nv);
        entity.setNgayTao(java.time.Instant.now());
        entity.setNguoiTao(req.getNguoiTao());
        entity.setXoaMem(false);

        return toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        var entity = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy bản ghi phân công!"));
        entity.setXoaMem(true);
        repo.save(entity);
    }

    private LichLamViecNhanVienResponse toResponse(LichLamViecNhanVien e) {
        LichLamViecNhanVienResponse res = mapper.map(e, LichLamViecNhanVienResponse.class);

        if (e.getLichLamViec() != null) {
            res.setLichLamViec(e.getLichLamViec());
            res.setNgayLam(e.getLichLamViec().getNgayLam());
            res.setTenCa(e.getLichLamViec().getIdCaLam().getTenCa());
            res.setGioBatDau(e.getLichLamViec().getIdCaLam().getGioBatDau());
            res.setGioKetThuc(e.getLichLamViec().getIdCaLam().getGioKetThuc());
        }

        if (e.getNhanVien() != null) {
            res.setMaNhanVien(e.getNhanVien().getMaNhanVien());
            res.setTenNhanVien(e.getNhanVien().getTenNhanVien());
            res.setTenTaiKhoan(e.getNhanVien().getTenTaiKhoan());
        }
        return res;
    }

    // --- Logic Import Excel tương tự như bạn đã làm ---
    @Transactional
    public List<LichLamViecNhanVienResponse> importExcel(MultipartFile file) {
        // Tương tự hàm import của LichLamViec, đọc 2 cột: ID_Lich và ID_NhanVien
        // Sau đó gọi hàm create(request) ở trên.
        return new java.util.ArrayList<>();
    }
}
