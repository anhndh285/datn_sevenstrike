package com.example.datn_sevenstrike.service.client;

import com.example.datn_sevenstrike.constants.TrangThaiHoaDon;
import com.example.datn_sevenstrike.dto.client.*;
import com.example.datn_sevenstrike.entity.*;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.ghn.dto.request.GhnTinhPhiRequest;
import com.example.datn_sevenstrike.ghn.dto.response.GhnTinhPhiResponse;
import com.example.datn_sevenstrike.ghn.service.GhnService;
import com.example.datn_sevenstrike.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientOrderService {

    private final SanPhamRepository sanPhamRepo;
    private final ChiTietSanPhamRepository ctspRepo;
    private final AnhChiTietSanPhamRepository anhRepo;
    private final PhieuGiamGiaRepository phieuRepo;
    private final PhieuGiamGiaCaNhanRepository phieuCaNhanRepo;
    private final HoaDonRepository hoaDonRepo;
    private final HoaDonChiTietRepository hdctRepo;
    private final LichSuHoaDonRepository lsHdRepo;
    private final ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepo;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepo;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepo;
    private final EmailService emailService;
    private final EntityManager entityManager;
    private final GhnService ghnService;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    @Transactional(readOnly = true)
    public List<ProductClientDTO> getProducts() {
        List<SanPham> list = sanPhamRepo.findAllByXoaMemFalseAndTrangThaiKinhDoanhTrueOrderByIdDesc();
        return list.stream().map(this::mapToProductClientDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductClientDTO> getBestSellingProducts() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(12);
        List<Integer> topIds = hdctRepo.findBestSellingProductIds(startOfMonth, PageRequest.of(0, 8));

        List<ProductClientDTO> result = new ArrayList<>();
        Set<Integer> seen = new LinkedHashSet<>();
        for (Integer spId : topIds) {
            if (seen.add(spId)) {
                sanPhamRepo.findByIdAndXoaMemFalse(spId).ifPresent(sp -> result.add(mapToProductClientDTO(sp)));
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<ProductClientDTO> getNewArrivalProducts() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(12);
        List<SanPham> list = sanPhamRepo.findNewArrivals(threshold, PageRequest.of(0, 8));
        return list.stream().map(this::mapToProductClientDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDetailClientDTO getProductDetail(Integer id) {
        SanPham sp = sanPhamRepo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy sản phẩm id=" + id));

        return mapToProductDetailClientDTO(sp);
    }

    @Transactional(readOnly = true)
    public List<VoucherClientDTO> getVouchers() {
        LocalDate today = LocalDate.now();
        return phieuRepo.findAllByXoaMemFalseOrderByIdDesc().stream()
                .filter(p -> Boolean.TRUE.equals(p.getTrangThai()) && p.getSoLuongSuDung() > 0)
                .filter(p -> !p.getNgayBatDau().isAfter(today) && !p.getNgayKetThuc().isBefore(today))
                .map(p -> VoucherClientDTO.builder()
                        .id(p.getId())
                        .maPhieuGiamGia(p.getMaPhieuGiamGia())
                        .tenPhieuGiamGia(p.getTenPhieuGiamGia())
                        .loaiPhieuGiamGia(p.getLoaiPhieuGiamGia())
                        .giaTriGiamGia(p.getGiaTriGiamGia())
                        .soTienGiamToiDa(p.getSoTienGiamToiDa())
                        .hoaDonToiThieu(p.getHoaDonToiThieu())
                        .ngayKetThuc(p.getNgayKetThuc())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MyVoucherDTO> getMyCoupons(Integer customerId) {
        LocalDate today = LocalDate.now();
        Map<Integer, MyVoucherDTO> result = new LinkedHashMap<>();

        List<PhieuGiamGiaCaNhan> personalList = phieuCaNhanRepo.findAllByIdKhachHangFetch(customerId);
        for (PhieuGiamGiaCaNhan cn : personalList) {
            PhieuGiamGia p = cn.getPhieuGiamGia();
            if (p == null || Boolean.TRUE.equals(p.getXoaMem())) continue;
            if (!Boolean.TRUE.equals(p.getTrangThai())) continue;

            result.put(p.getId(), MyVoucherDTO.builder()
                    .id(p.getId())
                    .maPhieuGiamGia(p.getMaPhieuGiamGia())
                    .tenPhieuGiamGia(p.getTenPhieuGiamGia())
                    .loaiPhieuGiamGia(p.getLoaiPhieuGiamGia())
                    .giaTriGiamGia(p.getGiaTriGiamGia())
                    .soTienGiamToiDa(p.getSoTienGiamToiDa())
                    .hoaDonToiThieu(p.getHoaDonToiThieu())
                    .ngayBatDau(p.getNgayBatDau())
                    .ngayKetThuc(p.getNgayKetThuc())
                    .moTa(p.getMoTa())
                    .nguon("personal")
                    .daSuDung(cn.getDaSuDung())
                    .ngayNhan(cn.getNgayNhan())
                    .maPhieuGiamGiaCaNhan(cn.getMaPhieuGiamGiaCaNhan())
                    .idPhieuGiamGiaCaNhan(cn.getId())
                    .trangThaiHienThi(computeStatus(p, today, cn.getDaSuDung()))
                    .build());
        }

        List<PhieuGiamGia> publicList = phieuRepo.findAllByXoaMemFalseOrderByIdDesc();
        for (PhieuGiamGia p : publicList) {
            if (result.containsKey(p.getId())) continue;
            if (!Boolean.TRUE.equals(p.getTrangThai())) continue;
            if (p.getSoLuongSuDung() <= 0) continue;
            if (p.getNgayKetThuc().isBefore(today)) continue;

            result.put(p.getId(), MyVoucherDTO.builder()
                    .id(p.getId())
                    .maPhieuGiamGia(p.getMaPhieuGiamGia())
                    .tenPhieuGiamGia(p.getTenPhieuGiamGia())
                    .loaiPhieuGiamGia(p.getLoaiPhieuGiamGia())
                    .giaTriGiamGia(p.getGiaTriGiamGia())
                    .soTienGiamToiDa(p.getSoTienGiamToiDa())
                    .hoaDonToiThieu(p.getHoaDonToiThieu())
                    .ngayBatDau(p.getNgayBatDau())
                    .ngayKetThuc(p.getNgayKetThuc())
                    .moTa(p.getMoTa())
                    .nguon("public")
                    .daSuDung(false)
                    .trangThaiHienThi(computeStatus(p, today, false))
                    .build());
        }

        return new ArrayList<>(result.values());
    }

    private String computeStatus(PhieuGiamGia p, LocalDate today, Boolean daSuDung) {
        if (Boolean.TRUE.equals(daSuDung)) return "used";
        if (p.getNgayKetThuc().isBefore(today)) return "expired";
        if (p.getNgayBatDau().isAfter(today)) return "upcoming";
        if (p.getSoLuongSuDung() <= 0) return "exhausted";
        return "available";
    }

    @Transactional(readOnly = true)
    public List<ClientOrderHistoryDTO> getOrdersByCustomerId(Integer customerId) {
        if (customerId == null) return new ArrayList<>();
        List<HoaDon> list = hoaDonRepo.findAllByIdKhachHangAndXoaMemFalseOrderByIdDesc(customerId);
        return list.stream().map(this::mapToClientOrderHistoryDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientOrderDetailDTO getOrderDetail(Integer id) {
        HoaDon hd = hoaDonRepo.findById(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy hóa đơn id=" + id));

        List<HoaDonChiTiet> details = hdctRepo.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(hd.getId());
        List<ClientOrderItemDTO> items = details.stream().map(this::mapToClientOrderItemDTO).collect(Collectors.toList());

        List<LichSuHoaDon> history = lsHdRepo.findAllByIdHoaDonAndXoaMemFalseOrderByThoiGianAsc(hd.getId());

        List<ClientTimelineDTO> timeline = new ArrayList<>();
        for (LichSuHoaDon h : history) {
            TrangThaiHoaDon statusEnum = TrangThaiHoaDon.fromCode(h.getTrangThai());
            timeline.add(ClientTimelineDTO.builder()
                    .status(statusEnum != null ? statusEnum.label : "Unknown")
                    .description(h.getGhiChu())
                    .time(h.getThoiGian())
                    .completed(true)
                    .active(h.getTrangThai().equals(hd.getTrangThaiHienTai()))
                    .build());
        }

        TrangThaiHoaDon currentStatus = TrangThaiHoaDon.fromCode(hd.getTrangThaiHienTai());

        // ✅ FIX: Logic hiển thị phương thức thanh toán chính xác
        boolean isCK = isDonChuyenKhoan(hd.getId());
        String ptttText = "Thanh toán khi nhận hàng";
        if (isCK) {
            ptttText = hd.getNgayThanhToan() != null ? "Thanh toán trước (VNPay)" : "Chờ thanh toán (VNPay)";
        }

        // ✅ FIX: Compute tamTinh from actual items (entity tongTien may be stale after edits)
        BigDecimal tamTinhReal = details.stream()
                .map(d -> {
                    BigDecimal dg = d.getDonGia() != null ? d.getDonGia() : BigDecimal.ZERO;
                    int sl = d.getSoLuong() != null ? d.getSoLuong() : 0;
                    return dg.multiply(BigDecimal.valueOf(sl));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal giamGiaReal = tamTinhReal.subtract(hd.getTongTienSauGiam()).max(BigDecimal.ZERO);

        return ClientOrderDetailDTO.builder()
                .id(hd.getId())
                .maHoaDon(hd.getMaHoaDon())
                .ngayTao(hd.getNgayTao())
                .trangThai(currentStatus != null ? currentStatus.label : "")
                .trangThaiHienTai(hd.getTrangThaiHienTai())
                .tenNguoiNhan(hd.getTenKhachHang())
                .soDienThoai(hd.getSoDienThoaiKhachHang())
                .diaChi(hd.getDiaChiKhachHang())
                .tamTinh(tamTinhReal)
                .phiVanChuyen(hd.getPhiVanChuyen())
                .giamGia(giamGiaReal)
                .tongTien(hd.getTongTienSauGiam().add(hd.getPhiVanChuyen() != null ? hd.getPhiVanChuyen() : BigDecimal.ZERO))
                .items(items)
                .timeline(timeline)
                .daThanhToan(hd.getNgayThanhToan() != null)
                .phuongThucThanhToan(ptttText)
                .loaiThanhToan(isCK ? 1 : 0)
                .idKhachHang(hd.getIdKhachHang())
                .build();
    }

    private ClientOrderHistoryDTO mapToClientOrderHistoryDTO(HoaDon hd) {
        List<HoaDonChiTiet> details = hdctRepo.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(hd.getId());
        String thumb = null;
        String firstProductName = "";

        if (!details.isEmpty()) {
            HoaDonChiTiet first = details.get(0);
            ChiTietSanPham ctsp = ctspRepo.findById(first.getIdChiTietSanPham()).orElse(null);
            if (ctsp != null && ctsp.getSanPham() != null) {
                firstProductName = ctsp.getSanPham().getTenSanPham();
                List<AnhChiTietSanPham> imgs = anhRepo.findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(ctsp.getId());
                if (!imgs.isEmpty()) {
                    thumb = imgs.stream()
                            .filter(img -> Boolean.TRUE.equals(img.getLaAnhDaiDien()))
                            .findFirst()
                            .map(img -> getFullUrl(img.getDuongDanAnh()))
                            .orElse(getFullUrl(imgs.get(0).getDuongDanAnh()));
                }
            }
        }

        TrangThaiHoaDon status = TrangThaiHoaDon.fromCode(hd.getTrangThaiHienTai());

        return ClientOrderHistoryDTO.builder()
                .id(hd.getId())
                .maHoaDon(hd.getMaHoaDon())
                .ngayTao(hd.getNgayTao())
                .trangThai(status != null ? status.label : "")
                .tongTien(hd.getTongTienSauGiam().add(hd.getPhiVanChuyen() != null ? hd.getPhiVanChuyen() : BigDecimal.ZERO))
                .soLuongSanPham(details.size())
                .sanPhamDaiDien(firstProductName)
                .anhDaiDien(thumb)
                .build();
    }

    private ClientOrderItemDTO mapToClientOrderItemDTO(HoaDonChiTiet item) {
        ChiTietSanPham ctsp = ctspRepo.findById(item.getIdChiTietSanPham()).orElse(null);
        String name = "";
        String thumb = null;
        String variant = "";
        BigDecimal currentPrice = BigDecimal.ZERO;

        if (ctsp != null) {
            if (ctsp.getSanPham() != null) {
                name = ctsp.getSanPham().getTenSanPham();
            }

            variant = (ctsp.getMauSac() != null ? ctsp.getMauSac().getTenMauSac() : "") + " - " +
                    (ctsp.getKichThuoc() != null ? ctsp.getKichThuoc().getTenKichThuoc() : "");

            List<AnhChiTietSanPham> imgs = anhRepo.findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(ctsp.getId());
            if (!imgs.isEmpty()) {
                thumb = imgs.stream()
                        .filter(img -> Boolean.TRUE.equals(img.getLaAnhDaiDien()))
                        .findFirst()
                        .map(img -> getFullUrl(img.getDuongDanAnh()))
                        .orElse(getFullUrl(imgs.get(0).getDuongDanAnh()));
            }

            // Tính giá hiện tại để hiển thị so sánh (donGiaCu trong DTO)
            BigDecimal giaBan = ctsp.getGiaBan() != null ? ctsp.getGiaBan() : ctsp.getGiaNiemYet();
            currentPrice = giaBan != null ? giaBan : BigDecimal.ZERO;
            LocalDate today = LocalDate.now();
            Optional<ChiTietDotGiamGiaRepository.BestDotGiamGiaView> bestDiscount =
                    chiTietDotGiamGiaRepo.findBestActiveDotByCtspId(ctsp.getId(), today);
            if (bestDiscount.isPresent()) {
                BigDecimal pct = bestDiscount.get().getGiaTriGiamApDung();
                currentPrice = currentPrice.multiply(
                        BigDecimal.ONE.subtract(pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                ).setScale(0, RoundingMode.HALF_UP);
            }
        }

        return ClientOrderItemDTO.builder()
                .id(item.getId())
                .tenSanPham(name)
                .anhDaiDien(thumb)
                .phanLoai(variant)
                .donGia(item.getDonGia())
                .donGiaCu(currentPrice)
                .soLuong(item.getSoLuong())
                .thanhTien(item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .idChiTietSanPham(item.getIdChiTietSanPham())
                .tonKho(ctsp != null ? ctsp.getSoLuong() : 0)
                .build();
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BadRequestEx("Giỏ hàng trống");
        }

        BigDecimal tongTien = BigDecimal.ZERO;
        List<HoaDonChiTiet> hdcts = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // ✅ FIX BUG: Gộp tổng số lượng theo ID sản phẩm trước khi check tồn kho
        Map<Integer, Integer> totalQtyMap = new HashMap<>();
        for (OrderItemRequest itemReq : req.getItems()) {
            if (itemReq.getIdChiTietSanPham() == null) continue;
            if (itemReq.getSoLuong() == null || itemReq.getSoLuong() <= 0) continue;
            totalQtyMap.merge(itemReq.getIdChiTietSanPham(), itemReq.getSoLuong(), Integer::sum);
        }

        // Check tồn kho dựa trên tổng số lượng
        for (Map.Entry<Integer, Integer> entry : totalQtyMap.entrySet()) {
            Integer ctspId = entry.getKey();
            Integer totalQty = entry.getValue();

            ChiTietSanPham ctsp = ctspRepo.findByIdAndXoaMemFalse(ctspId)
                    .orElseThrow(() -> new BadRequestEx("Sản phẩm không tồn tại id=" + ctspId));

            if (ctsp.getSoLuong() < totalQty) {
                // ✅ FIX LỖI BIÊN DỊCH: Gọi getSanPham() trước khi getTenSanPham()
                String tenSp = (ctsp.getSanPham() != null) ? ctsp.getSanPham().getTenSanPham() : "Sản phẩm";
                throw new BadRequestEx("Sản phẩm " + tenSp + " không đủ hàng (Yêu cầu: " + totalQty + ", Tồn: " + ctsp.getSoLuong() + ")");
            }
        }

        for (OrderItemRequest itemReq : req.getItems()) {
            ChiTietSanPham ctsp = ctspRepo.findById(itemReq.getIdChiTietSanPham()).orElseThrow();
            BigDecimal price = ctsp.getGiaBan() != null ? ctsp.getGiaBan() : ctsp.getGiaNiemYet();
            Optional<ChiTietDotGiamGiaRepository.BestDotGiamGiaView> bestDiscountOrder =
                    chiTietDotGiamGiaRepo.findBestActiveDotByCtspId(ctsp.getId(), today);

            if (bestDiscountOrder.isPresent()) {
                BigDecimal pct = bestDiscountOrder.get().getGiaTriGiamApDung();
                price = price.multiply(
                        BigDecimal.ONE.subtract(
                                pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                        )
                )
                        .setScale(0, RoundingMode.HALF_UP);
            }

            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(itemReq.getSoLuong()));
            tongTien = tongTien.add(subTotal);

            HoaDonChiTiet hdct = HoaDonChiTiet.builder()
                    .idChiTietSanPham(ctsp.getId())
                    .soLuong(itemReq.getSoLuong())
                    .donGia(price)
                    .xoaMem(false)
                    .build();
            hdcts.add(hdct);
        }

        BigDecimal tienGiam = BigDecimal.ZERO;
        PhieuGiamGia voucher = null;

        if (req.getIdPhieuGiamGia() != null) {
            voucher = phieuRepo.findByIdAndXoaMemFalse(req.getIdPhieuGiamGia())
                    .orElseThrow(() -> new BadRequestEx("Voucher không tồn tại"));

            boolean laPhieuCaNhan = !phieuCaNhanRepo
                    .findAllByIdPhieuGiamGiaAndXoaMemFalseOrderByIdDesc(req.getIdPhieuGiamGia())
                    .isEmpty();

            if (laPhieuCaNhan && req.getIdKhachHang() == null) {
                throw new BadRequestEx("Phiếu giảm giá này chỉ dành cho khách hàng đã đăng nhập");
            }

            if (!Boolean.TRUE.equals(voucher.getTrangThai())
                    || voucher.getSoLuongSuDung() <= 0
                    || voucher.getNgayBatDau().isAfter(today)
                    || voucher.getNgayKetThuc().isBefore(today)) {
                throw new BadRequestEx("Voucher không khả dụng");
            }

            if (voucher.getHoaDonToiThieu() != null && tongTien.compareTo(voucher.getHoaDonToiThieu()) < 0) {
                throw new BadRequestEx("Đơn hàng chưa đạt giá trị tối thiểu của voucher");
            }

            if (Boolean.FALSE.equals(voucher.getLoaiPhieuGiamGia()) && voucher.getGiaTriGiamGia() != null) {
                tienGiam = tongTien.multiply(voucher.getGiaTriGiamGia())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                if (voucher.getSoTienGiamToiDa() != null && tienGiam.compareTo(voucher.getSoTienGiamToiDa()) > 0) {
                    tienGiam = voucher.getSoTienGiamToiDa();
                }
            } else {
                tienGiam = voucher.getGiaTriGiamGia() != null ? voucher.getGiaTriGiamGia() : BigDecimal.ZERO;
            }

            if (tienGiam.compareTo(tongTien) > 0) {
                tienGiam = tongTien;
            }

            voucher.setSoLuongSuDung(voucher.getSoLuongSuDung() - 1);
            phieuRepo.save(voucher);
        }

        BigDecimal phiVanChuyen;
        if (req.getGhnToDistrictId() != null && req.getGhnToWardCode() != null
                && !req.getGhnToWardCode().isBlank()) {
            try {
                GhnTinhPhiRequest ghnReq = new GhnTinhPhiRequest();
                ghnReq.setToDistrictId(req.getGhnToDistrictId());
                ghnReq.setToWardCode(req.getGhnToWardCode());
                ghnReq.setTongGiaTriHang(tongTien.longValue());
                GhnTinhPhiResponse ghnRes = ghnService.tinhPhiVanChuyen(ghnReq);
                phiVanChuyen = BigDecimal.valueOf(ghnRes.getTotal());
            } catch (Exception e) {
                phiVanChuyen = new BigDecimal("40000"); // fallback nếu GHN lỗi
            }
        } else {
            phiVanChuyen = new BigDecimal("40000");
        }
        BigDecimal thanhTien = tongTien.subtract(tienGiam);

        HoaDon hd = HoaDon.builder()
                .idKhachHang(req.getIdKhachHang())
                .tenKhachHang(req.getTenKhachHang())
                .soDienThoaiKhachHang(req.getSoDienThoai())
                .diaChiKhachHang(req.getDiaChi())
                .emailKhachHang(req.getEmail())
                .ghiChu(req.getGhiChu())
                .idPhieuGiamGia(voucher != null ? voucher.getId() : null)
                .tongTien(tongTien)
                .tongTienSauGiam(thanhTien)
                .phiVanChuyen(phiVanChuyen)
                .loaiDon(2)
                .trangThaiHienTai(1)
                .ngayTao(LocalDateTime.now())
                .ngayCapNhat(LocalDateTime.now())
                .xoaMem(false)
                .build();

        hd = hoaDonRepo.save(hd);

        for (HoaDonChiTiet item : hdcts) {
            item.setIdHoaDon(hd.getId());
            hdctRepo.save(item);
        }

        // ✅ FIX BUG: Xác định phương thức thanh toán (ID) từ loaiThanhToan (0/1) hoặc idPhuongThucThanhToan
        Integer ptttId = req.getIdPhuongThucThanhToan();
        Integer loaiThanhToan = req.getLoaiThanhToan(); // 0: COD, 1: VNPay/Chuyển khoản

        // 🔍 DEBUG LOG
        System.out.println("=== CREATE ORDER DEBUG ===");
        System.out.println("loaiThanhToan: " + loaiThanhToan);
        System.out.println("idPhuongThucThanhToan: " + ptttId);

        if (ptttId == null) {
            List<PhuongThucThanhToan> allPttt = phuongThucThanhToanRepo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();
            System.out.println("Danh sách phương thức thanh toán:");
            for (PhuongThucThanhToan p : allPttt) {
                System.out.println("  - ID: " + p.getId() + ", Name: " + p.getTenPhuongThucThanhToan());
            }

            if (loaiThanhToan == null || loaiThanhToan == 0) {
                // Fallback: COD/Tiền mặt (mặc định)
                for (PhuongThucThanhToan p : allPttt) {
                    String name = p.getTenPhuongThucThanhToan().toUpperCase();
                    if (name.contains("TIỀN MẶT") || name.contains("COD")) {
                        System.out.println("✅ Tìm thấy COD method: " + p.getTenPhuongThucThanhToan() + " (ID=" + p.getId() + ")");
                        ptttId = p.getId();
                        break;
                    }
                }
            } else if (loaiThanhToan == 1) {
                // VNPay/Chuyển khoản: tìm VNPAY hoặc CHUYỂN KHOÁN (case insensitive)
                for (PhuongThucThanhToan p : allPttt) {
                    String name = p.getTenPhuongThucThanhToan();
                    String nameUpper = name.toUpperCase();
                    // ⚠️ Match case-insensitive: VNPAY, VNPay, vnpay, CHUYỂN KHOÁN, chuyển khoán, etc.
                    if (nameUpper.contains("VNPAY") || nameUpper.contains("CHUYỂN KHOÁN") || nameUpper.contains("BANKING") || nameUpper.contains("CHUYỂN")) {
                        System.out.println("✅ Tìm thấy VNPAY method: " + name + " (ID=" + p.getId() + ")");
                        ptttId = p.getId();
                        break;
                    }
                }
                // ⚠️ Nếu khách chọn VNPay (1) mà không tìm thấy ID phương thức -> Báo lỗi ngay
                if (ptttId == null) {
                    System.out.println("❌ ERROR: Không tìm thấy phương thức VNPAY/CHUYỂN KHOÁN!");
                    System.out.println("Danh sách phương thức hiện có:");
                    for (PhuongThucThanhToan p : allPttt) {
                        System.out.println("  - ID=" + p.getId() + ", Name=" + p.getTenPhuongThucThanhToan());
                    }
                    throw new BadRequestEx("Hệ thống chưa cấu hình phương thức thanh toán VNPAY/Chuyển khoán. Vui lòng liên hệ Admin.");
                }
            } else if (loaiThanhToan == 2) {
                // Momo
                for (PhuongThucThanhToan p : allPttt) {
                    if (p.getTenPhuongThucThanhToan().toUpperCase().contains("MOMO")) {
                        ptttId = p.getId();
                        break;
                    }
                }
                if (ptttId == null) {
                    throw new BadRequestEx("Hệ thống chưa cấu hình phương thức thanh toán MOMO. Vui lòng liên hệ Admin.");
                }
            } else if (loaiThanhToan == 3) {
                // Zalopay
                for (PhuongThucThanhToan p : allPttt) {
                    if (p.getTenPhuongThucThanhToan().toUpperCase().contains("ZALOPAY")) {
                        ptttId = p.getId();
                        break;
                    }
                }
                if (ptttId == null) {
                    throw new BadRequestEx("Hệ thống chưa cấu hình phương thức thanh toán ZALOPAY. Vui lòng liên hệ Admin.");
                }
            } else if (loaiThanhToan == 4) {
                // VietQR
                for (PhuongThucThanhToan p : allPttt) {
                    if (p.getTenPhuongThucThanhToan().toUpperCase().contains("VIETQR")) {
                        ptttId = p.getId();
                        break;
                    }
                }
                if (ptttId == null) {
                    throw new BadRequestEx("Hệ thống chưa cấu hình phương thức thanh toán VIETQR. Vui lòng liên hệ Admin.");
                }
            }
        }

        // ✅ Tạo giao dịch thanh toán ngay để đánh dấu loại đơn
        // ⚠️ QUAN TRỌNG: Phải có GiaoDichThanhToan để isDonChuyenKhoan() hoạt động đúng
        if (ptttId != null) {
            GiaoDichThanhToan gd = new GiaoDichThanhToan();
            gd.setIdHoaDon(hd.getId());
            gd.setIdPhuongThucThanhToan(ptttId);
            gd.setSoTien(thanhTien);
            gd.setTrangThai("khoi_tao");
            LocalDateTime now = LocalDateTime.now();
            gd.setThoiGianCapNhat(now);
            gd.setNguoiCapNhat(req.getIdKhachHang());
            gd.setXoaMem(false);
            gd.setGhiChu("Khởi tạo đơn hàng online");
            giaoDichThanhToanRepo.save(gd);
            System.out.println("✅ Saved GiaoDichThanhToan: id=" + gd.getId() + ", ptttId=" + ptttId);
        } else {
            // ⚠️ Fallback: Nếu vẫn không tìm được phương thức, tạo COD mặc định
            List<PhuongThucThanhToan> allPttt = phuongThucThanhToanRepo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();
            for (PhuongThucThanhToan p : allPttt) {
                String name = p.getTenPhuongThucThanhToan().toUpperCase();
                if (name.contains("TIỀN MẶT") || name.contains("COD")) {
                    GiaoDichThanhToan gd = new GiaoDichThanhToan();
                    gd.setIdHoaDon(hd.getId());
                    gd.setIdPhuongThucThanhToan(p.getId());
                    gd.setSoTien(thanhTien);
                    gd.setTrangThai("khoi_tao");
                    LocalDateTime now = LocalDateTime.now();
                    gd.setThoiGianCapNhat(now);
                    gd.setNguoiCapNhat(req.getIdKhachHang());
                    gd.setXoaMem(false);
                    gd.setGhiChu("Khởi tạo đơn hàng online (mặc định COD)");
                    giaoDichThanhToanRepo.save(gd);
                    break;
                }
            }
        }

        LichSuHoaDon ls = LichSuHoaDon.builder()
                .idHoaDon(hd.getId())
                .trangThai(1)
                .ghiChu("Khách hàng đặt hàng online")
                .xoaMem(false)
                .build();
        lsHdRepo.save(ls);

        // ⚠️ QUAN TRỌNG: Flush để đảm bảo GiaoDichThanhToan được lưu vào DB ngay
        entityManager.flush();
        entityManager.refresh(hd);

        if (hd.getEmailKhachHang() != null && !hd.getEmailKhachHang().isBlank()) {
            emailService.sendOrderConfirmation(hd);
        }

        return OrderResponse.builder()
                .id(hd.getId())
                .maHoaDon(hd.getMaHoaDon())
                .message("Đặt hàng thành công")
                .build();
    }

    @Transactional
    public void cancelOrderOnPaymentFailure(Integer hoaDonId) {
        if (hoaDonId == null) return;
        HoaDon hd = hoaDonRepo.findById(hoaDonId).orElse(null);
        if (hd == null || hd.getTrangThaiHienTai() != 1) return;

        hd.setTrangThaiHienTai(6); // DA_HUY
        hd.setNgayCapNhat(LocalDateTime.now());
        hoaDonRepo.save(hd);

        if (hd.getIdPhieuGiamGia() != null) {
            phieuRepo.restoreOne(hd.getIdPhieuGiamGia());
        }

        List<GiaoDichThanhToan> gds = giaoDichThanhToanRepo.findAllByIdHoaDon(hoaDonId);
        for (GiaoDichThanhToan gd : gds) {
            gd.setTrangThai("that_bai");
            gd.setThoiGianCapNhat(LocalDateTime.now());
        }
        giaoDichThanhToanRepo.saveAll(gds);
    }

    private ProductClientDTO mapToProductClientDTO(SanPham sp) {
        List<ChiTietSanPham> variants = ctspRepo.findAllByIdSanPhamAndXoaMemFalseOrderByIdDesc(sp.getId());
        LocalDate today = LocalDate.now();

        String thumb = null;
        for (ChiTietSanPham v : variants) {
            List<AnhChiTietSanPham> imgs = anhRepo.findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(v.getId());
            for (AnhChiTietSanPham img : imgs) {
                if (Boolean.TRUE.equals(img.getLaAnhDaiDien())) {
                    thumb = getFullUrl(img.getDuongDanAnh());
                    break;
                }
            }
            if (thumb == null && !imgs.isEmpty()) {
                thumb = getFullUrl(imgs.get(0).getDuongDanAnh());
            }
            if (thumb != null) {
                break;
            }
        }

        List<VariantClientDTO> variantDTOs = variants.stream().map(v -> {
            BigDecimal giaBan = v.getGiaBan() != null ? v.getGiaBan() : v.getGiaNiemYet();
            BigDecimal giaGoc = null;
            BigDecimal giaSauGiam = null;
            Integer phanTramGiam = null;

            Optional<ChiTietDotGiamGiaRepository.BestDotGiamGiaView> bestDiscount =
                    chiTietDotGiamGiaRepo.findBestActiveDotByCtspId(v.getId(), today);

            if (bestDiscount.isPresent()) {
                BigDecimal pct = bestDiscount.get().getGiaTriGiamApDung();
                giaGoc = giaBan;
                giaSauGiam = giaBan.multiply(
                        BigDecimal.ONE.subtract(
                                pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                        )
                )
                        .setScale(0, RoundingMode.HALF_UP);
                phanTramGiam = pct.intValue();
            }

            return VariantClientDTO.builder()
                    .id(v.getId())
                    .tenMauSac(v.getMauSac() != null ? v.getMauSac().getTenMauSac() : "")
                    .maMauHex(v.getMauSac() != null ? v.getMauSac().getMaMauHex() : null)
                    .tenKichThuoc(v.getKichThuoc() != null ? v.getKichThuoc().getTenKichThuoc() : "")
                    .tenLoaiSan(v.getLoaiSan() != null ? v.getLoaiSan().getTenLoaiSan() : "")
                    .tenFormChan(v.getFormChan() != null ? v.getFormChan().getTenFormChan() : "")
                    .giaBan(giaBan)
                    .soLuong(v.getSoLuong())
                    .giaGoc(giaGoc)
                    .giaSauGiam(giaSauGiam)
                    .phanTramGiam(phanTramGiam)
                    .ngayKetThuc(bestDiscount.isPresent() ? bestDiscount.get().getNgayKetThuc() : null)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal min = variantDTOs.stream()
                .map(v -> v.getGiaSauGiam() != null ? v.getGiaSauGiam() : v.getGiaBan())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal max = variantDTOs.stream()
                .map(VariantClientDTO::getGiaBan)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        VariantClientDTO cheapestDisc = variantDTOs.stream()
                .filter(v -> v.getGiaSauGiam() != null)
                .min(Comparator.comparing(VariantClientDTO::getGiaSauGiam))
                .orElse(null);

        boolean hangCoSan = variantDTOs.stream()
                .anyMatch(v -> v.getSoLuong() != null && v.getSoLuong() > 0);

        List<String> kichThuocCoSan = variantDTOs.stream()
                .filter(v -> v.getSoLuong() != null && v.getSoLuong() > 0)
                .map(VariantClientDTO::getTenKichThuoc)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .sorted(Comparator.comparingDouble(s -> {
                    try {
                        return Double.parseDouble(s);
                    } catch (Exception e) {
                        return 0.0;
                    }
                }))
                .collect(Collectors.toList());

        return ProductClientDTO.builder()
                .id(sp.getId())
                .maSanPham(sp.getMaSanPham())
                .tenSanPham(sp.getTenSanPham())
                .tenThuongHieu(sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : "")
                .tenXuatXu(sp.getXuatXu() != null ? sp.getXuatXu().getTenXuatXu() : "")
                .tenViTriThiDau(sp.getViTriThiDau() != null ? sp.getViTriThiDau().getTenViTri() : "")
                .tenPhongCachChoi(sp.getPhongCachChoi() != null ? sp.getPhongCachChoi().getTenPhongCach() : "")
                .tenCoGiay(sp.getCoGiay() != null ? sp.getCoGiay().getTenCoGiay() : "")
                .tenChatLieu(sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : "")
                .giaThapNhat(min)
                .giaCaoNhat(max)
                .giaGocThapNhat(cheapestDisc != null ? cheapestDisc.getGiaGoc() : null)
                .giaSauGiamThapNhat(cheapestDisc != null ? cheapestDisc.getGiaSauGiam() : null)
                .phanTramGiam(cheapestDisc != null ? cheapestDisc.getPhanTramGiam() : null)
                .ngayKetThucGiamGia(cheapestDisc != null ? cheapestDisc.getNgayKetThuc() : null)
                .hangCoSan(hangCoSan)
                .kichThuocCoSan(kichThuocCoSan)
                .anhDaiDien(thumb)
                .moTaNgan(sp.getMoTaNgan())
                .variants(variantDTOs)
                .build();
    }

    private ProductDetailClientDTO mapToProductDetailClientDTO(SanPham sp) {
        List<ChiTietSanPham> variants = ctspRepo.findAllByIdSanPhamAndXoaMemFalseOrderByIdDesc(sp.getId());
        LocalDate today = LocalDate.now();

        String thumb = null;
        Set<String> imgSet = new LinkedHashSet<>();
        List<VariantClientDTO> variantDTOs = new ArrayList<>();

        for (ChiTietSanPham v : variants) {
            List<AnhChiTietSanPham> imgs = anhRepo.findAllByIdChiTietSanPhamAndXoaMemFalseOrderByIdDesc(v.getId());
            String variantThumb = null;

            for (AnhChiTietSanPham img : imgs) {
                imgSet.add(getFullUrl(img.getDuongDanAnh()));
                if (Boolean.TRUE.equals(img.getLaAnhDaiDien()) && thumb == null) {
                    thumb = getFullUrl(img.getDuongDanAnh());
                }
                if (Boolean.TRUE.equals(img.getLaAnhDaiDien())) {
                    variantThumb = getFullUrl(img.getDuongDanAnh());
                }
            }

            if (thumb == null && !imgs.isEmpty()) {
                thumb = getFullUrl(imgs.get(0).getDuongDanAnh());
            }
            if (variantThumb == null && !imgs.isEmpty()) {
                variantThumb = getFullUrl(imgs.get(0).getDuongDanAnh());
            }

            BigDecimal giaBan = v.getGiaBan() != null ? v.getGiaBan() : v.getGiaNiemYet();
            BigDecimal giaGoc = null;
            BigDecimal giaSauGiam = null;
            Integer phanTramGiam = null;

            Optional<ChiTietDotGiamGiaRepository.BestDotGiamGiaView> bestDiscount2 =
                    chiTietDotGiamGiaRepo.findBestActiveDotByCtspId(v.getId(), today);

            if (bestDiscount2.isPresent()) {
                BigDecimal pct = bestDiscount2.get().getGiaTriGiamApDung();
                giaGoc = giaBan;
                giaSauGiam = giaBan.multiply(
                        BigDecimal.ONE.subtract(
                                pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                        )
                )
                        .setScale(0, RoundingMode.HALF_UP);
                phanTramGiam = pct.intValue();
            }

            variantDTOs.add(VariantClientDTO.builder()
                    .id(v.getId())
                    .tenMauSac(v.getMauSac() != null ? v.getMauSac().getTenMauSac() : "")
                    .maMauHex(v.getMauSac() != null ? v.getMauSac().getMaMauHex() : null)
                    .tenKichThuoc(v.getKichThuoc() != null ? v.getKichThuoc().getTenKichThuoc() : "")
                    .tenLoaiSan(v.getLoaiSan() != null ? v.getLoaiSan().getTenLoaiSan() : "")
                    .tenFormChan(v.getFormChan() != null ? v.getFormChan().getTenFormChan() : "")
                    .giaBan(giaBan)
                    .soLuong(v.getSoLuong())
                    .anhDaiDien(variantThumb)
                    .giaGoc(giaGoc)
                    .giaSauGiam(giaSauGiam)
                    .phanTramGiam(phanTramGiam)
                    .build());
        }

        if (thumb == null && !imgSet.isEmpty()) {
            thumb = imgSet.iterator().next();
        }

        BigDecimal min = variantDTOs.stream()
                .map(v -> v.getGiaSauGiam() != null ? v.getGiaSauGiam() : v.getGiaBan())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal max = variantDTOs.stream()
                .map(VariantClientDTO::getGiaBan)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        VariantClientDTO cheapestDisc = variantDTOs.stream()
                .filter(v -> v.getGiaSauGiam() != null)
                .min(Comparator.comparing(VariantClientDTO::getGiaSauGiam))
                .orElse(null);

        return ProductDetailClientDTO.builder()
                .id(sp.getId())
                .tenSanPham(sp.getTenSanPham())
                .tenThuongHieu(sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : "")
                .giaThapNhat(min)
                .giaCaoNhat(max)
                .giaGocThapNhat(cheapestDisc != null ? cheapestDisc.getGiaGoc() : null)
                .giaSauGiamThapNhat(cheapestDisc != null ? cheapestDisc.getGiaSauGiam() : null)
                .phanTramGiam(cheapestDisc != null ? cheapestDisc.getPhanTramGiam() : null)
                .anhDaiDien(thumb)
                .moTaNgan(sp.getMoTaNgan())
                .maSanPham(sp.getMaSanPham())
                .moTaChiTiet(sp.getMoTaChiTiet())
                .tenXuatXu(sp.getXuatXu() != null ? sp.getXuatXu().getTenXuatXu() : "")
                .tenViTriThiDau(sp.getViTriThiDau() != null ? sp.getViTriThiDau().getTenViTri() : "")
                .tenPhongCachChoi(sp.getPhongCachChoi() != null ? sp.getPhongCachChoi().getTenPhongCach() : "")
                .tenCoGiay(sp.getCoGiay() != null ? sp.getCoGiay().getTenCoGiay() : "")
                .tenChatLieu(sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : "")
                .images(new ArrayList<>(imgSet))
                .variants(variantDTOs)
                .build();
    }

    private String getFullUrl(String path) {
        if (path == null || path.isBlank()) return null;
        if (path.startsWith("http")) return path;

        String normalized = path.replace("\\", "/");

        int idx = normalized.indexOf("/uploads/");
        if (idx >= 0) {
            normalized = normalized.substring(idx);
        }

        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        return backendUrl + normalized;
    }

    private boolean isDonChuyenKhoan(Integer hoaDonId) {
        List<GiaoDichThanhToan> gds = giaoDichThanhToanRepo.findAllByIdHoaDon(hoaDonId);
        for (GiaoDichThanhToan gd : gds) {
            if (gd.getIdPhuongThucThanhToan() != null) {
                PhuongThucThanhToan pt = phuongThucThanhToanRepo.findById(gd.getIdPhuongThucThanhToan()).orElse(null);
                if (pt != null && pt.getTenPhuongThucThanhToan() != null) {
                    String name = pt.getTenPhuongThucThanhToan().toLowerCase();
                    if (name.contains("chuyển khoản") || name.contains("vnpay") || name.contains("banking")
                            || name.contains("momo") || name.contains("zalopay") || name.contains("vietqr")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}