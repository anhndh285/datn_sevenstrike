package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.DotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.DotGiamGiaResponse;
import com.example.datn_sevenstrike.entity.DotGiamGia;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.DotGiamGiaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DotGiamGiaService {

    private final DotGiamGiaRepository repo;
    private final ModelMapper mapper;

    // =========================
    // READ
    // =========================
    public List<DotGiamGiaResponse> all() {
        // ✅ lấy raw (không ép order theo id nữa)
        List<DotGiamGia> raw = repo.findAllByXoaMemFalse();

        List<DotGiamGiaResponse> list = raw.stream()
                .map(this::toResponse)
                .toList();

        return sortByOverlapClusterAndPriority(list);
    }

    public List<DotGiamGiaResponse> allActive() {
        // ✅ lấy raw active
        List<DotGiamGia> raw = repo.findAllByXoaMemFalseAndTrangThaiTrue();

        List<DotGiamGiaResponse> list = raw.stream()
                .map(this::toResponse)
                .toList();

        return sortByOverlapClusterAndPriority(list);
    }

    public DotGiamGiaResponse one(Integer id) {
        DotGiamGia e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DotGiamGia id=" + id));
        return toResponse(e);
    }

    // =========================
    // CREATE / UPDATE / DELETE
    // =========================
    @Transactional
    public DotGiamGiaResponse create(DotGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        DotGiamGia e = mapper.map(req, DotGiamGia.class);
        e.setId(null);

        applyDefaults(e);
        // ✅ chốt nghiệp vụ: chỉ % => luôn false
        e.setLoaiGiamGia(false);

        // ✅ CHO PHÉP overlap thời gian => KHÔNG validate trùng thời gian
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public DotGiamGiaResponse update(Integer id, DotGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        DotGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DotGiamGia id=" + id));

        if (req.getTenDotGiamGia() != null) db.setTenDotGiamGia(req.getTenDotGiamGia());
        if (req.getGiaTriGiamGia() != null) db.setGiaTriGiamGia(req.getGiaTriGiamGia());
        if (req.getNgayBatDau() != null) db.setNgayBatDau(req.getNgayBatDau());
        if (req.getNgayKetThuc() != null) db.setNgayKetThuc(req.getNgayKetThuc());
        if (req.getMucUuTien() != null) db.setMucUuTien(req.getMucUuTien());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());

        applyDefaults(db);
        // ✅ chốt nghiệp vụ: luôn %
        db.setLoaiGiamGia(false);

        validate(db);
        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        DotGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy DotGiamGia id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    // =========================
    // VALIDATE / DEFAULTS
    // =========================
    private void applyDefaults(DotGiamGia e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getLoaiGiamGia() == null) e.setLoaiGiamGia(false);
        if (e.getMucUuTien() == null) e.setMucUuTien(0);
    }

    private void validate(DotGiamGia e) {
        if (e.getTenDotGiamGia() == null || e.getTenDotGiamGia().isBlank())
            throw new BadRequestEx("Thiếu ten_dot_giam_gia");

        if (e.getNgayBatDau() == null || e.getNgayKetThuc() == null)
            throw new BadRequestEx("Thiếu ngay_bat_dau/ngay_ket_thuc");

        if (e.getNgayKetThuc().isBefore(e.getNgayBatDau()))
            throw new BadRequestEx("Ngày kết thúc phải >= ngày bắt đầu");

        if (e.getGiaTriGiamGia() == null)
            throw new BadRequestEx("Thiếu gia_tri_giam_gia");

        if (e.getGiaTriGiamGia().compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestEx("Giá trị giảm phải >= 0");

        // ✅ vì chốt %: 0..100
        if (e.getGiaTriGiamGia().compareTo(new BigDecimal("100")) > 0)
            throw new BadRequestEx("Giảm % phải nằm trong 0..100");
    }

    private DotGiamGiaResponse toResponse(DotGiamGia e) {
        return mapper.map(e, DotGiamGiaResponse.class);
    }

    // =========================
    // SORT: overlap cluster + priority
    // =========================
    /**
     * Yêu cầu:
     * - Cho phép nhiều đợt overlap thời gian
     * - Trong cùng cụm overlap: ưu tiên tốt nhất trước:
     *   mucUuTien DESC -> giaTriGiamGia DESC -> id DESC
     * - Giữa các cụm khác nhau: mới -> cũ:
     *   ngayBatDau DESC -> ngayKetThuc DESC -> id DESC
     */
    private List<DotGiamGiaResponse> sortByOverlapClusterAndPriority(List<DotGiamGiaResponse> input) {
        if (input == null || input.isEmpty()) return Collections.emptyList();

        List<DotGiamGiaResponse> list = new ArrayList<>(input);

        // 1) sort baseline để "đi theo timeline" mới -> cũ, rồi gom cụm overlap ổn định
        list.sort((a, b) -> {
            int c1 = safeStart(b).compareTo(safeStart(a)); // DESC start
            if (c1 != 0) return c1;

            int c2 = safeEnd(b).compareTo(safeEnd(a)); // DESC end
            if (c2 != 0) return c2;

            return Integer.compare(safeId(b), safeId(a)); // DESC id
        });

        // 2) gom cụm overlap
        List<List<DotGiamGiaResponse>> clusters = new ArrayList<>();
        List<DotGiamGiaResponse> cur = new ArrayList<>();
        LocalDate curMinStart = null;
        LocalDate curMaxEnd = null;

        for (DotGiamGiaResponse d : list) {
            LocalDate ds = safeStart(d);
            LocalDate de = safeEnd(d);

            if (cur.isEmpty()) {
                cur.add(d);
                curMinStart = ds;
                curMaxEnd = de;
                continue;
            }

            // overlap nếu [ds, de] giao [curMinStart, curMaxEnd]
            boolean overlap = !de.isBefore(curMinStart) && !ds.isAfter(curMaxEnd);

            if (overlap) {
                cur.add(d);
                if (ds.isBefore(curMinStart)) curMinStart = ds;
                if (de.isAfter(curMaxEnd)) curMaxEnd = de;
            } else {
                clusters.add(cur);
                cur = new ArrayList<>();
                cur.add(d);
                curMinStart = ds;
                curMaxEnd = de;
            }
        }
        if (!cur.isEmpty()) clusters.add(cur);

        // 3) sort trong cụm theo "tốt nhất"
        Comparator<DotGiamGiaResponse> bestFirst = (a, b) -> {
            int p = Integer.compare(safePriority(b), safePriority(a)); // DESC mucUuTien
            if (p != 0) return p;

            int v = safeValue(b).compareTo(safeValue(a)); // DESC giaTri
            if (v != 0) return v;

            return Integer.compare(safeId(b), safeId(a)); // DESC id
        };

        // 4) flatten theo thứ tự cụm (cụm đã là mới -> cũ)
        List<DotGiamGiaResponse> out = new ArrayList<>(list.size());
        for (List<DotGiamGiaResponse> cluster : clusters) {
            cluster.sort(bestFirst);
            out.addAll(cluster);
        }
        return out;
    }

    // =========================
    // null-safe getters cho response
    // (nếu field name khác thì bạn đổi tại đây)
    // =========================
    private LocalDate safeStart(DotGiamGiaResponse x) {
        return x.getNgayBatDau() == null ? LocalDate.MIN : x.getNgayBatDau();
    }

    private LocalDate safeEnd(DotGiamGiaResponse x) {
        return x.getNgayKetThuc() == null ? LocalDate.MIN : x.getNgayKetThuc();
    }

    private int safePriority(DotGiamGiaResponse x) {
        Integer p = x.getMucUuTien();
        return p == null ? 0 : p;
    }

    private BigDecimal safeValue(DotGiamGiaResponse x) {
        BigDecimal v = x.getGiaTriGiamGia();
        return v == null ? BigDecimal.ZERO : v;
    }

    private int safeId(DotGiamGiaResponse x) {
        Integer id = x.getId();
        return id == null ? 0 : id;
    }
}
