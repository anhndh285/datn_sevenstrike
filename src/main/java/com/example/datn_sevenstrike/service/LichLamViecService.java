package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LichLamViecNhanVienRequest;
import com.example.datn_sevenstrike.dto.request.LichLamViecRequest;
import com.example.datn_sevenstrike.dto.response.LichLamViecNhanVienResponse;
import com.example.datn_sevenstrike.dto.response.LichLamViecResponse;
import com.example.datn_sevenstrike.entity.CaLam;
import com.example.datn_sevenstrike.entity.LichLamViec;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.CaLamRepository;
import com.example.datn_sevenstrike.repository.LichLamViecNhanVienRepository;
import com.example.datn_sevenstrike.repository.LichLamViecRepository;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LichLamViecService {

    private static final int NGUOI_TAO_MAC_DINH = 1;

    private final LichLamViecRepository repo;
    private final LichLamViecNhanVienRepository lichNhanVienRepo;
    private final NhanVienRepository nhanVienRepo;
    private final CaLamRepository caLamRepo;
    private final LichLamViecNhanVienService lichLamViecNhanVienService;

    private final DataFormatter dataFormatter = new DataFormatter();

    public List<LichLamViecResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<LichLamViecResponse> getpage(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);
        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "ngayLam"));
        return repo.findAllByXoaMemFalse(pageable).map(this::toResponse);
    }

    public List<LichLamViecResponse> checkCa(Integer idCa, String ngay) {
        try {
            LocalDate localDate = LocalDate.parse(ngay);
            return repo.findTrungCa(localDate, idCa)
                    .map(this::toResponse)
                    .map(List::of)
                    .orElse(List.of());
        } catch (Exception e) {
            throw new BadRequestEx("Ngày không đúng định dạng yyyy-MM-dd");
        }
    }

    @Transactional
    public LichLamViecResponse create(LichLamViecRequest req) {
        if (req == null) {
            throw new BadRequestEx("Thiếu dữ liệu tạo lịch làm việc");
        }
        if (req.getIdCaLam() == null) {
            throw new BadRequestEx("Ca làm không được để trống");
        }
        if (req.getNgayLam() == null) {
            throw new BadRequestEx("Ngày làm không được để trống");
        }

        CaLam ca = caLamRepo.findByIdAndXoaMemFalse(req.getIdCaLam())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ca làm"));

        if (repo.findTrungCa(req.getNgayLam(), req.getIdCaLam()).isPresent()) {
            throw new BadRequestEx("Ca này đã có lịch vào ngày " + req.getNgayLam());
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
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy lịch làm việc ID: " + id));

        if (req.getIdCaLam() != null) {
            CaLam ca = caLamRepo.findByIdAndXoaMemFalse(req.getIdCaLam())
                    .orElseThrow(() -> new NotFoundEx("Không tìm thấy ca làm ID: " + req.getIdCaLam()));
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
                    throw new BadRequestEx("Ca làm này đã tồn tại lịch vào ngày " + ngay);
                });

        db.setNgayCapNhat(Instant.now());
        db.setNguoiCapNhat(req.getNguoiTao());

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
                .idCaLam(e.getIdCaLam() != null ? e.getIdCaLam().getId() : null)
                .tenCa(e.getIdCaLam() != null ? e.getIdCaLam().getTenCa() : null)
                .gioBatDau(e.getIdCaLam() != null ? e.getIdCaLam().getGioBatDau() : null)
                .gioKetThuc(e.getIdCaLam() != null ? e.getIdCaLam().getGioKetThuc() : null)
                .ngayLam(e.getNgayLam())
                .ghiChu(e.getGhiChu())
                .xoaMem(e.getXoaMem())
                .ngayTao(e.getNgayTao() != null
                        ? e.getNgayTao().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .build();
    }

    @Transactional
    public List<LichLamViecNhanVienResponse> importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestEx("Vui lòng chọn file Excel để upload");
        }

        List<ImportRowData> validRows = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> duplicateInFile = new HashSet<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            if (workbook.getNumberOfSheets() == 0) {
                throw new BadRequestEx("File Excel không có sheet dữ liệu");
            }

            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = parseHeaderRow(headerRow, evaluator);

            List<NhanVien> allNhanVien = nhanVienRepo.findAllByXoaMemFalseOrderByIdDesc();
            List<CaLam> allCaLam = caLamRepo.findAllByXoaMemFalseOrderByIdDesc();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                int excelRowNum = rowIndex + 1;

                if (isDataRowEmpty(row, columnMap, evaluator)) {
                    continue;
                }

                String maNhanVien = getCellString(row, columnMap, "MA_NHAN_VIEN", evaluator);
                String tenNhanVienInput = getCellString(row, columnMap, "TEN_NHAN_VIEN", evaluator);
                String tenCa = getCellString(row, columnMap, "TEN_CA", evaluator);
                String ghiChu = getCellString(row, columnMap, "GHI_CHU", evaluator);

                if (maNhanVien.isBlank()) {
                    errors.add("Dòng " + excelRowNum + ": Mã Nhân Viên không được để trống");
                    continue;
                }

                if (tenCa.isBlank()) {
                    errors.add("Dòng " + excelRowNum + ": Ca Làm không được để trống");
                    continue;
                }

                LocalDate ngayLam;
                try {
                    ngayLam = getCellDate(row, columnMap, "NGAY_LAM", evaluator);
                } catch (BadRequestEx ex) {
                    errors.add("Dòng " + excelRowNum + ": " + ex.getMessage());
                    continue;
                }

                NhanVien nhanVien = findNhanVienByMa(allNhanVien, maNhanVien);
                if (nhanVien == null) {
                    errors.add("Dòng " + excelRowNum + ": Mã Nhân Viên \"" + maNhanVien + "\" không tồn tại");
                    continue;
                }

                if (Integer.valueOf(1).equals(nhanVien.getIdQuyenHan())) {
                    errors.add("Dòng " + excelRowNum + ": Nhân viên \"" + maNhanVien + "\" là tài khoản ADMIN, không được import");
                    continue;
                }

                CaLam caLam = findCaLamByTen(allCaLam, tenCa);
                if (caLam == null) {
                    errors.add("Dòng " + excelRowNum + ": Ca Làm \"" + tenCa + "\" không tồn tại");
                    continue;
                }

                String duplicateKey = nhanVien.getId() + "|" + caLam.getId() + "|" + ngayLam;
                if (duplicateInFile.contains(duplicateKey)) {
                    continue;
                }
                duplicateInFile.add(duplicateKey);

                validRows.add(new ImportRowData(
                        excelRowNum,
                        nhanVien,
                        tenNhanVienInput,
                        caLam,
                        ngayLam,
                        ghiChu
                ));
            }

        } catch (BadRequestEx e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestEx("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new BadRequestEx(String.join("\n", errors));
        }

        List<LichLamViecNhanVienResponse> result = new ArrayList<>();

        for (ImportRowData row : validRows) {
            LichLamViec lichLamViec = findOrCreateLich(
                    row.getCaLam(),
                    row.getNgayLam(),
                    row.getGhiChu()
            );

            boolean daTonTaiPhanCong = lichNhanVienRepo.existsByLichLamViecAndNhanVien(
                    lichLamViec.getId(),
                    row.getNhanVien().getId()
            );

            if (daTonTaiPhanCong) {
                continue;
            }

            LichLamViecNhanVienRequest req = new LichLamViecNhanVienRequest();
            req.setIdLichLamViec(lichLamViec.getId());
            req.setIdNhanVien(row.getNhanVien().getId());
            req.setNguoiTao(NGUOI_TAO_MAC_DINH);

            LichLamViecNhanVienResponse created = lichLamViecNhanVienService.create(req);
            result.add(created);
        }

        return result;
    }

    private LichLamViec findOrCreateLich(CaLam caLam, LocalDate ngayLam, String ghiChu) {
        return repo.findTrungCa(ngayLam, caLam.getId())
                .orElseGet(() -> {
                    LichLamViec entity = new LichLamViec();
                    entity.setIdCaLam(caLam);
                    entity.setNgayLam(ngayLam);
                    entity.setGhiChu(ghiChu);
                    entity.setXoaMem(false);
                    entity.setNgayTao(Instant.now());
                    entity.setNguoiTao(NGUOI_TAO_MAC_DINH);
                    return repo.save(entity);
                });
    }

    private Map<String, Integer> parseHeaderRow(Row headerRow, FormulaEvaluator evaluator) {
        if (headerRow == null) {
            throw new BadRequestEx("File Excel không có dòng tiêu đề");
        }

        Map<String, Integer> map = new HashMap<>();

        for (int col = 0; col < headerRow.getLastCellNum(); col++) {
            Cell cell = headerRow.getCell(col);
            String rawHeader = getCellString(cell, evaluator);
            String key = normalizeText(rawHeader);

            if (key.equals("manhanvien") || key.equals("manv")) {
                map.put("MA_NHAN_VIEN", col);
            } else if (key.equals("tennhanvien") || key.equals("tennv") || key.equals("nhanvien")) {
                map.put("TEN_NHAN_VIEN", col);
            } else if (key.equals("calam") || key.equals("tenca") || key.equals("ca")) {
                map.put("TEN_CA", col);
            } else if (key.equals("ngaylam") || key.equals("ngay")) {
                map.put("NGAY_LAM", col);
            } else if (key.equals("ghichu") || key.equals("note")) {
                map.put("GHI_CHU", col);
            }
        }

        List<String> missing = new ArrayList<>();
        if (!map.containsKey("MA_NHAN_VIEN")) missing.add("Mã Nhân Viên");
        if (!map.containsKey("TEN_NHAN_VIEN")) missing.add("Tên Nhân Viên");
        if (!map.containsKey("TEN_CA")) missing.add("Ca Làm");
        if (!map.containsKey("NGAY_LAM")) missing.add("Ngày Làm");

        if (!missing.isEmpty()) {
            throw new BadRequestEx("Thiếu cột bắt buộc trong file Excel: " + String.join(", ", missing));
        }

        return map;
    }

    private boolean isDataRowEmpty(Row row, Map<String, Integer> columnMap, FormulaEvaluator evaluator) {
        if (row == null) {
            return true;
        }

        String maNhanVien = getCellString(row, columnMap, "MA_NHAN_VIEN", evaluator);
        String tenNhanVien = getCellString(row, columnMap, "TEN_NHAN_VIEN", evaluator);
        String tenCa = getCellString(row, columnMap, "TEN_CA", evaluator);
        String ngayLam = getCellString(row, columnMap, "NGAY_LAM", evaluator);
        String ghiChu = getCellString(row, columnMap, "GHI_CHU", evaluator);

        return maNhanVien.isBlank()
                && tenNhanVien.isBlank()
                && tenCa.isBlank()
                && ngayLam.isBlank()
                && ghiChu.isBlank();
    }

    private String getCellString(Row row, Map<String, Integer> columnMap, String key, FormulaEvaluator evaluator) {
        Integer colIndex = columnMap.get(key);
        if (colIndex == null || row == null) {
            return "";
        }
        return getCellString(row.getCell(colIndex), evaluator);
    }

    private String getCellString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }
        return dataFormatter.formatCellValue(cell, evaluator).trim();
    }

    private LocalDate getCellDate(Row row, Map<String, Integer> columnMap, String key, FormulaEvaluator evaluator) {
        Integer colIndex = columnMap.get(key);
        if (colIndex == null || row == null) {
            throw new BadRequestEx("Không tìm thấy cột Ngày Làm");
        }
        return getCellDate(row.getCell(colIndex), evaluator);
    }

    private LocalDate getCellDate(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            throw new BadRequestEx("Ngày Làm không được để trống");
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }

            String raw = getCellString(cell, evaluator);
            if (raw.isBlank()) {
                throw new BadRequestEx("Ngày Làm không được để trống");
            }

            String normalized = raw.trim().replace("/", "-");

            if (normalized.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                return LocalDate.parse(normalized, DateTimeFormatter.ofPattern("yyyy-M-d"));
            }

            if (normalized.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
                return LocalDate.parse(normalized, DateTimeFormatter.ofPattern("d-M-yyyy"));
            }

            if (normalized.matches("\\d+(\\.\\d+)?")) {
                double excelDate = Double.parseDouble(normalized);
                return DateUtil.getJavaDate(excelDate)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

        } catch (BadRequestEx e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestEx("Ngày Làm không đúng định dạng (YYYY-MM-DD hoặc DD/MM/YYYY)");
        }

        throw new BadRequestEx("Ngày Làm không đúng định dạng (YYYY-MM-DD hoặc DD/MM/YYYY)");
    }

    private NhanVien findNhanVienByMa(List<NhanVien> allNhanVien, String maNhanVien) {
        String target = normalizeText(maNhanVien);
        for (NhanVien nv : allNhanVien) {
            if (normalizeText(nv.getMaNhanVien()).equals(target)) {
                return nv;
            }
        }
        return null;
    }

    private CaLam findCaLamByTen(List<CaLam> allCaLam, String tenCa) {
        String target = normalizeText(tenCa);
        for (CaLam ca : allCaLam) {
            if (normalizeText(ca.getTenCa()).equals(target)) {
                return ca;
            }
        }
        return null;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D");

        return normalized
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }

    private static class ImportRowData {
        private final int rowNum;
        private final NhanVien nhanVien;
        private final String tenNhanVienInput;
        private final CaLam caLam;
        private final LocalDate ngayLam;
        private final String ghiChu;

        public ImportRowData(int rowNum,
                             NhanVien nhanVien,
                             String tenNhanVienInput,
                             CaLam caLam,
                             LocalDate ngayLam,
                             String ghiChu) {
            this.rowNum = rowNum;
            this.nhanVien = nhanVien;
            this.tenNhanVienInput = tenNhanVienInput;
            this.caLam = caLam;
            this.ngayLam = ngayLam;
            this.ghiChu = ghiChu;
        }

        public int getRowNum() {
            return rowNum;
        }

        public NhanVien getNhanVien() {
            return nhanVien;
        }

        public String getTenNhanVienInput() {
            return tenNhanVienInput;
        }

        public CaLam getCaLam() {
            return caLam;
        }

        public LocalDate getNgayLam() {
            return ngayLam;
        }

        public String getGhiChu() {
            return ghiChu;
        }
    }
}