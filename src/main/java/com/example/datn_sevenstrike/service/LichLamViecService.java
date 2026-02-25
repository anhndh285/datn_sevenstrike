package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LichLamViecRequest;
import com.example.datn_sevenstrike.dto.response.LichLamViecResponse;
import com.example.datn_sevenstrike.entity.CaLam;
import com.example.datn_sevenstrike.entity.LichLamViec;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.CaLamRepository;
import com.example.datn_sevenstrike.repository.LichLamViecRepository;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LichLamViecService {

    private final LichLamViecRepository repo;
    private final NhanVienRepository nhanVienRepo;
    private final CaLamRepository caLamRepo;
    private final ModelMapper mapper;

    public List<LichLamViecResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public Page<LichLamViecResponse> getpage(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);
        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "ngayLam"));

        return repo.findAllByXoaMemFalse(pageable).map(this::toResponse);
    }

    public List<LichLamViecResponse> checkCa(Integer idCa, String ngay) {
        LocalDate localDate = LocalDate.parse(ngay);

        return repo.findTrungCa(localDate, idCa)
                .map(this::toResponse)
                .map(List::of)
                .orElse(List.of());
    }

    @Transactional
    public LichLamViecResponse create(LichLamViecRequest req) {

        CaLam ca = caLamRepo.findById(req.getIdCaLam())
                .orElseThrow(() ->
                        new NotFoundEx("Không tìm thấy ca làm"));

        if (repo.findTrungCa(req.getNgayLam(), req.getIdCaLam()).isPresent()) {
            throw new BadRequestEx(
                    "Ca này đã có lịch vào ngày " + req.getNgayLam()
            );
        }

        LichLamViec lv = new LichLamViec();
        lv.setIdCaLam(ca);
        lv.setNgayLam(req.getNgayLam());
        lv.setGhiChu(req.getGhiChu());
        lv.setXoaMem(false);
        lv.setNgayTao(Instant.now());
        lv.setNguoiTao(req.getNguoiTao());

        return toResponse(repo.save(lv));
    }


    @Transactional
    public LichLamViecResponse update(Integer id, LichLamViecRequest req) {

        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        }

        LichLamViec db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() ->
                        new NotFoundEx("Không tìm thấy lịch làm việc ID: " + id));

        if (req.getIdCaLam() != null) {
            CaLam ca = caLamRepo.findById(req.getIdCaLam())
                    .orElseThrow(() ->
                            new NotFoundEx("Không tìm thấy ca làm ID: " + req.getIdCaLam()));

            db.setIdCaLam(ca);
        }

        if (req.getNgayLam() != null) {
            db.setNgayLam(req.getNgayLam());
        }

        if (req.getGhiChu() != null) {
            db.setGhiChu(req.getGhiChu());
        }

        Integer caId = db.getIdCaLam().getId();
        LocalDate ngay = db.getNgayLam();

        repo.findTrungCa(ngay, caId)
                .filter(lv -> !lv.getId().equals(id))
                .ifPresent(lv -> {
                    throw new BadRequestEx(
                            "Ca làm này đã tồn tại lịch vào ngày " + ngay
                    );
                });

        db.setNgayCapNhat(java.time.Instant.now());

        return toResponse(repo.save(db));
    }


    @Transactional
    public void delete(Integer id) {
        LichLamViec db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy lịch làm việc có ID: " + id));
        db.setXoaMem(true);
        repo.save(db);
    }


    private LichLamViecResponse toResponse(LichLamViec e) {

        return LichLamViecResponse.builder()
                .id(e.getId())
                .ngayLam(e.getNgayLam())
                .ghiChu(e.getGhiChu())
                .xoaMem(e.getXoaMem())
                .tenCa(e.getIdCaLam().getTenCa())
                .gioBatDau(e.getIdCaLam().getGioBatDau())
                .gioKetThuc(e.getIdCaLam().getGioKetThuc())
                .build();
    }


    @Transactional
    public List<LichLamViecResponse> importExcel(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestEx("Vui lòng chọn file Excel để upload!");
        }

        List<LichLamViecResponse> resultList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // 1. Bỏ qua dòng tiêu đề (Header)
                if (row.getRowNum() == 0) continue;

                // 2. Bỏ qua dòng trống (quan trọng để tránh lỗi dòng thừa ở cuối file)
                if (isRowEmpty(row)) continue;

                try {
                    // 3. Đọc dữ liệu an toàn (có kiểm tra null)
                    Integer idCa = getSafeInt(row.getCell(1), "ID Ca làm");
                    LocalDate ngayLam = getSafeDate(row.getCell(2));
                    String ghiChu = getSafeString(row.getCell(3));

                    // 4. Tạo request và gọi hàm create
                    LichLamViecRequest request = new LichLamViecRequest();
                    request.setIdCaLam(idCa);
                    request.setNgayLam(ngayLam);
                    request.setGhiChu(ghiChu);

                    // Tận dụng hàm create để validate trùng lịch luôn
                    resultList.add(this.create(request));

                } catch (Exception e) {
                    // Bắt lỗi và chỉ rõ dòng nào bị sai để FE hiển thị
                    throw new BadRequestEx("Lỗi tại dòng " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BadRequestEx("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return resultList;
    }

    // --- CÁC HÀM BỔ TRỢ (HELPER METHODS) ---

    // Kiểm tra xem dòng có trống trơn không
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }

    // Lấy số nguyên an toàn
    private Integer getSafeInt(Cell cell, String fieldName) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new BadRequestEx(fieldName + " không được để trống");
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                throw new BadRequestEx(fieldName + " phải là số");
            }
        }
        throw new BadRequestEx(fieldName + " định dạng không hợp lệ");
    }

    // Lấy ngày tháng an toàn
    private LocalDate getSafeDate(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new BadRequestEx("Ngày làm không được để trống");
        }
        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                // Hỗ trợ nhập ngày dạng text "yyyy-MM-dd"
                return LocalDate.parse(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            throw new BadRequestEx("Ngày làm sai định dạng (Yêu cầu: dd/MM/yyyy hoặc yyyy-MM-dd)");
        }
        throw new BadRequestEx("Ngày làm không hợp lệ");
    }

    // Lấy chuỗi an toàn (trả về rỗng nếu null)
    private String getSafeString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return "";
    }
}