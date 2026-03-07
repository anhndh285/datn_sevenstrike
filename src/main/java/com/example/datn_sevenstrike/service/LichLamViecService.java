package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LichLamViecNhanVienRequest;
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
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LichLamViecService {

    private final LichLamViecRepository repo;
    private final NhanVienRepository nhanVienRepo;
    private final CaLamRepository caLamRepo;
    private final ModelMapper mapper;

    private final LichLamViecNhanVienService lichLamViecNhanVienService;

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
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new BadRequestEx("File Excel không có dòng tiêu đề!");

            Map<String, Integer> columnMap = parseHeaderRow(headerRow);

            for (Row row : sheet) {
                if (row.getRowNum() == 0 || isRowEmpty(row)) continue;

                try {
                    // 1. Đọc dữ liệu Ca và Ngày
                    String caLamInput = getSafeString(row.getCell(columnMap.getOrDefault("CA_LAM", 0)));
                    LocalDate ngayLam = getSafeDate(row.getCell(columnMap.getOrDefault("NGAY_LAM", 1)));

                    // 2. Tìm CaLam
                    CaLam ca = findCaLamByNameOrId(caLamInput);
                    if (ca == null) throw new BadRequestEx("Không tìm thấy ca làm: " + caLamInput);

                    // 3. Xử lý LichLamViec (Header của ca trực)
                    // Kiểm tra xem ngày đó ca đó đã tồn tại chưa
                    Optional<LichLamViec> existingLich = repo.findTrungCa(ngayLam, ca.getId());
                    LichLamViecResponse lichRes;

                    if (existingLich.isPresent()) {
                        // Nếu đã có lịch, lấy lịch đó để gán nhân viên vào
                        lichRes = toResponse(existingLich.get());
                    } else {
                        // Nếu chưa có, tạo mới
                        LichLamViecRequest request = new LichLamViecRequest();
                        request.setIdCaLam(ca.getId());
                        request.setNgayLam(ngayLam);
                        request.setGhiChu(getSafeString(row.getCell(columnMap.getOrDefault("GHI_CHU", row.getLastCellNum() - 1))));
                        request.setNguoiTao(1);
                        lichRes = this.create(request);
                    }
                    resultList.add(lichRes);

                    // 4. Đọc danh sách nhân viên từ các cột
                    List<String> nhanVienList = new ArrayList<>();
                    int startCol = columnMap.getOrDefault("NHAN_VIEN_START", 2);
                    for (int colIdx = startCol; colIdx < row.getLastCellNum(); colIdx++) {
                        String value = getSafeString(row.getCell(colIdx));
                        if (!value.isEmpty() && !value.toLowerCase().matches(".*(ghi chú|note).*")) {
                            for (String name : value.split("[,;]")) {
                                if (!name.trim().isEmpty()) nhanVienList.add(name.trim());
                            }
                        }
                    }

                    // 5. Gán nhân viên vào lịch vừa tìm/tạo được
                    for (String nhanVienName : nhanVienList) {
                        try {
                            NhanVien nv = findNhanVienByNameOrCode(nhanVienName);
                            if (nv != null) {
                                // Tạo request gán nhân viên
                                LichLamViecNhanVienRequest assignReq = new LichLamViecNhanVienRequest();
                                assignReq.setIdLichLamViec(lichRes.getId());
                                assignReq.setIdNhanVien(nv.getId());
                                assignReq.setNguoiTao(1);

                                // Gọi sang service NhanVien để lưu bản ghi phân công
                                // Hàm create của LichLamViecNhanVienService đã có logic check trùng
                                lichLamViecNhanVienService.create(assignReq);
                            }
                        } catch (BadRequestEx ex) {
                            // Nếu nhân viên đã được gán rồi (trùng), log lại và bỏ qua dòng này
                            System.out.println("Bỏ qua: " + nhanVienName + " đã có trong lịch.");
                        } catch (Exception e) {
                            System.err.println("Lỗi gán nhân viên " + nhanVienName + ": " + e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    throw new BadRequestEx("Lỗi tại dòng " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BadRequestEx("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return resultList;
    }

    private Map<String, Integer> parseHeaderRow(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();

        for (int colIdx = 0; colIdx < headerRow.getLastCellNum(); colIdx++) {
            Cell cell = headerRow.getCell(colIdx);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String headerText = cell.getStringCellValue().trim().toUpperCase();

                if (headerText.contains("CA")) {
                    columnMap.put("CA_LAM", colIdx);
                } else if (headerText.contains("NGÀY") || headerText.contains("NGAY")) {
                    columnMap.put("NGAY_LAM", colIdx);
                } else if (headerText.contains("NHÂN") || headerText.contains("NHAN")) {
                    if (!columnMap.containsKey("NHAN_VIEN_START")) {
                        columnMap.put("NHAN_VIEN_START", colIdx);
                    }
                } else if (headerText.contains("GHI") || headerText.contains("NOTE")) {
                    columnMap.put("GHI_CHU", colIdx);
                }
            }
        }

        return columnMap;
    }

    private CaLam findCaLamByNameOrId(String input) {
        if (input.isEmpty()) return null;

        // Try to parse as ID
        try {
            Integer id = Integer.parseInt(input.trim());
            return caLamRepo.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            // Not a number, search by name
        }

        // Search by name (contains)
        List<CaLam> allCa = caLamRepo.findAll();
        String searchTerm = input.toLowerCase().trim();

        for (CaLam ca : allCa) {
            if (ca.getTenCa().toLowerCase().contains(searchTerm)) {
                return ca;
            }
        }

        return null;
    }

    private NhanVien findNhanVienByNameOrCode(String input) {
        if (input.isEmpty()) return null;

        // Try to parse as ID
        try {
            Integer id = Integer.parseInt(input.trim());
            return nhanVienRepo.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            // Not a number, search by name or code
        }

        // Search by name or code
        List<NhanVien> allNv = nhanVienRepo.findAll();
        String searchTerm = input.toLowerCase().trim();

        for (NhanVien nv : allNv) {
            if (nv.getTenNhanVien().toLowerCase().contains(searchTerm) ||
                    (nv.getMaNhanVien() != null && nv.getMaNhanVien().toLowerCase().contains(searchTerm))) {
                return nv;
            }
        }

        return null;
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

    private LocalDate getSafeDate(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new BadRequestEx("Ngày làm không được để trống");
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }

            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                if (dateStr.isEmpty()) throw new BadRequestEx("Ngày làm trống");

                dateStr = dateStr.replace("/", "-");

                if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return LocalDate.parse(dateStr);
                }

                java.time.format.DateTimeFormatter formatter =
                        java.time.format.DateTimeFormatter.ofPattern("d-M-yyyy");
                return LocalDate.parse(dateStr, formatter);
            }

            if (cell.getCellType() == CellType.NUMERIC) {
                return DateUtil.getJavaDate(cell.getNumericCellValue())
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } catch (Exception e) {
            throw new BadRequestEx("Ngày làm tại ô " + cell.getAddress() + " không hợp lệ: " + cell);
        }
        throw new BadRequestEx("Định dạng ngày không được hỗ trợ");
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