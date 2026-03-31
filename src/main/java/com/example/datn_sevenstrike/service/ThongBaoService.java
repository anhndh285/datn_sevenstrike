package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.chat.entity.PhienChat;
import com.example.datn_sevenstrike.chat.entity.TinNhan;
import com.example.datn_sevenstrike.chat.repository.PhienChatRepository;
import com.example.datn_sevenstrike.chat.repository.TinNhanRepository;
import com.example.datn_sevenstrike.constants.ThongBaoConstants;
import com.example.datn_sevenstrike.dto.response.ThongBaoResponse;
import com.example.datn_sevenstrike.dto.response.ThongBaoTongQuanResponse;
import com.example.datn_sevenstrike.entity.ChiTietSanPham;
import com.example.datn_sevenstrike.entity.HoaDon;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.entity.ThongBao;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import com.example.datn_sevenstrike.repository.ThongBaoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final NhanVienRepository nhanVienRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhienChatRepository phienChatRepository;
    private final TinNhanRepository tinNhanRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PersistenceContext
    private EntityManager em;

    @Value("${thongbao.ton-kho.nguong-rat-thap:2}")
    private Integer nguongTonKhoRatThap;

    @Value("${thongbao.don-cho-xac-nhan.phut:10}")
    private Integer phutDonChoXacNhan;

    @Value("${thongbao.chat.qua-han.phut:5}")
    private Integer phutChatQuaHan;

    @Transactional(readOnly = true)
    public List<ThongBaoResponse> layDanhSach(Integer idNhanVien, Boolean chiLayChuaDoc, Integer gioiHan) {
        int limit = (gioiHan == null || gioiHan <= 0) ? 30 : Math.min(gioiHan, 50);

        List<ThongBao> ds = Boolean.TRUE.equals(chiLayChuaDoc)
                ? thongBaoRepository.findTop50ByIdNhanVienNhanAndDaDocFalseAndXoaMemFalseOrderByThoiGianTaoDesc(idNhanVien)
                : thongBaoRepository.findTop50ByIdNhanVienNhanAndXoaMemFalseOrderByThoiGianTaoDesc(idNhanVien);

        return ds.stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ThongBaoTongQuanResponse layTongQuan(Integer idNhanVien) {
        return ThongBaoTongQuanResponse.builder()
                .idNhanVien(idNhanVien)
                .soThongBaoChuaDoc(thongBaoRepository.countByIdNhanVienNhanAndDaDocFalseAndXoaMemFalse(idNhanVien))
                .soThongBaoChuaXuLy(thongBaoRepository.countByIdNhanVienNhanAndDaXuLyFalseAndXoaMemFalse(idNhanVien))
                .build();
    }

    @Transactional
    public ThongBaoResponse danhDauDaDoc(Integer idThongBao, Integer idNhanVien) {
        LocalDateTime now = LocalDateTime.now();
        thongBaoRepository.danhDauDaDoc(idThongBao, idNhanVien, idNhanVien, now);

        ThongBao tb = thongBaoRepository.findByIdAndIdNhanVienNhanAndXoaMemFalse(idThongBao, idNhanVien)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy thông báo"));

        pushDemChuaDoc(idNhanVien);
        return toResponse(tb);
    }

    @Transactional
    public void danhDauTatCaDaDoc(Integer idNhanVien) {
        thongBaoRepository.danhDauTatCaDaDoc(idNhanVien, idNhanVien, LocalDateTime.now());
        pushDemChuaDoc(idNhanVien);
    }

    @Transactional
    public ThongBaoResponse danhDauDaXuLy(Integer idThongBao, Integer idNhanVien) {
        LocalDateTime now = LocalDateTime.now();
        int updated = thongBaoRepository.danhDauDaXuLy(idThongBao, idNhanVien, idNhanVien, now);
        if (updated == 0) {
            throw new NotFoundEx("Không tìm thấy thông báo cần xử lý");
        }

        ThongBao tb = thongBaoRepository.findByIdAndIdNhanVienNhanAndXoaMemFalse(idThongBao, idNhanVien)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy thông báo"));

        return toResponse(tb);
    }

    @Transactional
    public void taoThongBaoDonTrucTuyenMoi(HoaDon hd) {
        if (hd == null || hd.getId() == null) return;

        taoChoTatCaAdminNeuChuaTrung(
                ThongBaoConstants.DON_TRUC_TUYEN_MOI,
                ThongBaoConstants.MUC_DO_CANH_BAO,
                "Đơn online mới",
                "Có đơn online mới " + safe(hd.getMaHoaDon(), "#" + hd.getId())
                        + " từ khách " + safe(hd.getTenKhachHang(), "Khách hàng") + ".",
                ThongBaoConstants.DOI_TUONG_HOA_DON,
                hd.getId(),
                "DON_TRUC_TUYEN_MOI_" + hd.getId(),
                jsonHoaDon(hd),
                null
        );
    }

    @Transactional
    public void taoThongBaoDonCoYeuCauHuy(HoaDon hd, String lyDo) {
        if (hd == null || hd.getId() == null) return;

        String noiDung = "Đơn " + safe(hd.getMaHoaDon(), "#" + hd.getId())
                + " có yêu cầu hủy từ khách " + safe(hd.getTenKhachHang(), "Khách hàng") + ".";
        if (lyDo != null && !lyDo.isBlank()) {
            noiDung += " Lý do: " + lyDo.trim();
        }

        taoChoTatCaAdminNeuChuaTrung(
                ThongBaoConstants.DON_CO_YEU_CAU_HUY,
                ThongBaoConstants.MUC_DO_NGHIEM_TRONG,
                "Đơn có yêu cầu hủy",
                noiDung,
                ThongBaoConstants.DOI_TUONG_HOA_DON,
                hd.getId(),
                "DON_CO_YEU_CAU_HUY_" + hd.getId(),
                jsonHoaDon(hd),
                null
        );
    }

    @Transactional
    public void taoThongBaoThanhToanThatBai(HoaDon hd, String ghiChuThem) {
        if (hd == null || hd.getId() == null) return;

        String noiDung = "Thanh toán cho đơn " + safe(hd.getMaHoaDon(), "#" + hd.getId()) + " thất bại.";
        if (ghiChuThem != null && !ghiChuThem.isBlank()) {
            noiDung += " " + ghiChuThem.trim();
        }

        taoChoTatCaAdminNeuChuaTrung(
                ThongBaoConstants.THANH_TOAN_THAT_BAI,
                ThongBaoConstants.MUC_DO_NGHIEM_TRONG,
                "Thanh toán thất bại",
                noiDung,
                ThongBaoConstants.DOI_TUONG_HOA_DON,
                hd.getId(),
                "THANH_TOAN_THAT_BAI_" + hd.getId(),
                jsonHoaDon(hd),
                null
        );
    }

    @Transactional
    public void taoThongBaoTinNhanMoi(PhienChat phien, TinNhan tinNhan) {
        if (!laPhienCanTiepNhan(phien)) return;
        if (!laTinNhanKhach(tinNhan)) return;

        String loai = safe(phien.getLoai(), "KHACH_HANG").trim().toUpperCase();
        String tieuDe;
        String noiDung;

        if ("NOI_BO".equals(loai)) {
            tieuDe = "Tin nhắn nội bộ mới";
            noiDung = "Phiên chat nội bộ #" + phien.getId() + " có tin nhắn mới từ "
                    + safe(tinNhan.getTenNguoiGui(), safe(phien.getTenKhach(), "Nhân viên"))
                    + ": " + rutGonNoiDung(tinNhan.getNoiDung());
        } else {
            tieuDe = "Có tin nhắn mới";
            noiDung = "Khách " + safe(phien.getTenKhach(), "Khách hàng")
                    + " vừa gửi tin nhắn ở phiên #" + phien.getId()
                    + ": " + rutGonNoiDung(tinNhan.getNoiDung());
        }

        taoChoTatCaAdminNeuChuaTrung(
                ThongBaoConstants.TIN_NHAN_MOI,
                ThongBaoConstants.MUC_DO_CANH_BAO,
                tieuDe,
                noiDung,
                ThongBaoConstants.DOI_TUONG_PHIEN_CHAT,
                phien.getId(),
                "TIN_NHAN_MOI_" + phien.getId() + "_" + tinNhan.getId(),
                "{\"idPhienChat\":" + phien.getId() + ",\"idTinNhan\":" + tinNhan.getId() + "}",
                null
        );
    }

    @Transactional
    public void kiemTraVaTaoCanhBaoTonKhoChoCtsp(Integer idChiTietSanPham) {
        if (idChiTietSanPham == null) return;

        ChiTietSanPham ctsp = chiTietSanPhamRepository.findByIdAndXoaMemFalse(idChiTietSanPham).orElse(null);
        if (ctsp == null) return;
        if (Boolean.FALSE.equals(ctsp.getTrangThai())) return;

        int soLuong = ctsp.getSoLuong() == null ? 0 : ctsp.getSoLuong();
        String nhan = buildCtspLabel(ctsp);

        if (soLuong <= 0) {
            taoChoTatCaAdminNeuChuaTrung(
                    ThongBaoConstants.BIEN_THE_HET_HANG,
                    ThongBaoConstants.MUC_DO_NGHIEM_TRONG,
                    "Biến thể hết hàng",
                    nhan + " đã hết hàng.",
                    ThongBaoConstants.DOI_TUONG_CHI_TIET_SAN_PHAM,
                    ctsp.getId(),
                    "BIEN_THE_HET_HANG_" + ctsp.getId(),
                    "{\"idChiTietSanPham\":" + ctsp.getId() + ",\"soLuong\":0}",
                    null
            );
            return;
        }

        if (soLuong <= nguongTonKhoRatThap) {
            taoChoTatCaAdminNeuChuaTrung(
                    ThongBaoConstants.BIEN_THE_SAP_HET_HANG,
                    ThongBaoConstants.MUC_DO_CANH_BAO,
                    "Biến thể sắp hết hàng",
                    nhan + " chỉ còn " + soLuong + " sản phẩm trong kho.",
                    ThongBaoConstants.DOI_TUONG_CHI_TIET_SAN_PHAM,
                    ctsp.getId(),
                    "BIEN_THE_SAP_HET_HANG_" + ctsp.getId(),
                    "{\"idChiTietSanPham\":" + ctsp.getId() + ",\"soLuong\":" + soLuong + "}",
                    null
            );
        }
    }

    @Transactional
    public void danhDauThongBaoDonDaDuocXuLy(Integer idHoaDon, Integer nguoiCapNhat) {
        if (idHoaDon == null) return;
        thongBaoRepository.danhDauDaXuLyTheoDoiTuongVaLoai(
                ThongBaoConstants.DOI_TUONG_HOA_DON,
                idHoaDon,
                List.of(
                        ThongBaoConstants.DON_CHO_XAC_NHAN,
                        ThongBaoConstants.DON_CO_YEU_CAU_HUY,
                        ThongBaoConstants.THANH_TOAN_THANH_CONG_CHUA_DONG_BO
                ),
                nguoiCapNhat,
                LocalDateTime.now()
        );
    }

    @Transactional
    public void danhDauThongBaoChatDaXuLy(Integer idPhienChat, Integer nguoiCapNhat) {
        if (idPhienChat == null) return;
        thongBaoRepository.danhDauDaXuLyTheoDoiTuongVaLoai(
                ThongBaoConstants.DOI_TUONG_PHIEN_CHAT,
                idPhienChat,
                List.of(
                        ThongBaoConstants.TIN_NHAN_MOI,
                        ThongBaoConstants.KHACH_CHO_PHAN_HOI_QUA_HAN
                ),
                nguoiCapNhat,
                LocalDateTime.now()
        );
    }

    @Transactional
    public void danhDauThongBaoThanhToanLechTrangThaiDaXuLy(Integer idHoaDon, Integer nguoiCapNhat) {
        if (idHoaDon == null) return;
        thongBaoRepository.danhDauDaXuLyTheoDoiTuongVaLoai(
                ThongBaoConstants.DOI_TUONG_HOA_DON,
                idHoaDon,
                List.of(ThongBaoConstants.THANH_TOAN_THANH_CONG_CHUA_DONG_BO),
                nguoiCapNhat,
                LocalDateTime.now()
        );
    }

    @Transactional
    public void quetDonChoXacNhan() {
        String sql = """
                select hd.id, hd.ma_hoa_don, hd.ten_khach_hang, hd.ngay_tao
                from dbo.hoa_don hd
                where hd.xoa_mem = 0
                  and hd.loai_don = 2
                  and hd.trang_thai_hien_tai = 1
                  and hd.ngay_tao <= dateadd(minute, -:soPhut, sysdatetime())
                order by hd.ngay_tao asc, hd.id asc
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("soPhut", phutDonChoXacNhan)
                .getResultList();

        for (Object[] row : rows) {
            Integer idHoaDon = toInt(row[0]);
            String maHoaDon = toStr(row[1]);
            String tenKhach = toStr(row[2]);
            LocalDateTime ngayTao = toDateTime(row[3]);

            String noiDung = "Đơn " + safe(maHoaDon, "#" + idHoaDon)
                    + " của khách " + safe(tenKhach, "Khách hàng")
                    + " đang chờ xác nhận " + tinhSoPhutCho(ngayTao) + " phút.";

            taoChoTatCaAdminNeuChuaTrung(
                    ThongBaoConstants.DON_CHO_XAC_NHAN,
                    ThongBaoConstants.MUC_DO_CANH_BAO,
                    "Đơn đang chờ xác nhận",
                    noiDung,
                    ThongBaoConstants.DOI_TUONG_HOA_DON,
                    idHoaDon,
                    "DON_CHO_XAC_NHAN_" + idHoaDon,
                    "{\"idHoaDon\":" + idHoaDon + "}",
                    null
            );
        }
    }

    @Transactional
    public void quetThanhToanThanhCongChuaDongBo() {
        String sql = """
                select distinct
                    hd.id,
                    hd.ma_hoa_don,
                    gd.id,
                    gd.ma_giao_dich_thanh_toan,
                    coalesce(gd.thoi_gian_cap_nhat, gd.thoi_gian_tao) as moc_thoi_gian
                from dbo.giao_dich_thanh_toan gd
                join dbo.hoa_don hd
                  on hd.id = gd.id_hoa_don
                 and hd.xoa_mem = 0
                where gd.xoa_mem = 0
                  and lower(ltrim(rtrim(gd.trang_thai))) = 'thanh_cong'
                  and hd.ngay_thanh_toan is null
                  and coalesce(gd.thoi_gian_cap_nhat, gd.thoi_gian_tao) <= dateadd(minute, -2, sysdatetime())
                order by moc_thoi_gian asc, gd.id asc
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        for (Object[] row : rows) {
            Integer idHoaDon = toInt(row[0]);
            String maHoaDon = toStr(row[1]);
            Integer idGiaoDich = toInt(row[2]);
            String maGiaoDich = toStr(row[3]);

            taoChoTatCaAdminNeuChuaTrung(
                    ThongBaoConstants.THANH_TOAN_THANH_CONG_CHUA_DONG_BO,
                    ThongBaoConstants.MUC_DO_NGHIEM_TRONG,
                    "Thanh toán thành công nhưng đơn chưa đồng bộ trạng thái",
                    "Đơn " + safe(maHoaDon, "#" + idHoaDon)
                            + " có giao dịch thành công " + safe(maGiaoDich, "#" + idGiaoDich)
                            + " nhưng chưa cập nhật ngày thanh toán.",
                    ThongBaoConstants.DOI_TUONG_HOA_DON,
                    idHoaDon,
                    "THANH_TOAN_THANH_CONG_CHUA_DONG_BO_" + idHoaDon,
                    "{\"idHoaDon\":" + idHoaDon + ",\"idGiaoDich\":" + idGiaoDich + "}",
                    null
            );
        }
    }

    @Transactional
    public void quetGiaoDichBatThuong() {
        quetGiaoDichThanhCongTrungLap();
        quetGiaoDichThatBaiNhieuLan();
    }

    @Transactional
    public void quetTonKho() {
        String sql = """
                select ctsp.id
                from dbo.chi_tiet_san_pham ctsp
                where ctsp.xoa_mem = 0
                  and ctsp.trang_thai = 1
                  and ctsp.so_luong <= :nguongTonKho
                order by ctsp.so_luong asc, ctsp.id asc
                """;

        @SuppressWarnings("unchecked")
        List<Object> rows = em.createNativeQuery(sql)
                .setParameter("nguongTonKho", nguongTonKhoRatThap)
                .getResultList();

        for (Object row : rows) {
            kiemTraVaTaoCanhBaoTonKhoChoCtsp(toInt(row));
        }
    }

    @Transactional
    public void quetKhachChoPhanHoiQuaHan() {
        List<PhienChat> sessions = phienChatRepository
                .findByLoaiAndTrangThaiOrderByThoiGianBatDauDesc("KHACH_HANG", "CHO_NHAN_VIEN");

        for (PhienChat phien : sessions) {
            if (!laPhienCanTiepNhan(phien)) continue;

            List<TinNhan> tinNhans = tinNhanRepository.findByPhienChat_IdOrderByThoiGianAsc(phien.getId());
            if (tinNhans == null || tinNhans.isEmpty()) continue;

            TinNhan lastKhach = tinNhans.stream()
                    .filter(Objects::nonNull)
                    .filter(this::laTinNhanKhach)
                    .max(Comparator.comparing(TinNhan::getThoiGian, Comparator.nullsLast(LocalDateTime::compareTo)))
                    .orElse(null);

            if (lastKhach == null || lastKhach.getThoiGian() == null) continue;
            if (Duration.between(lastKhach.getThoiGian(), LocalDateTime.now()).toMinutes() < phutChatQuaHan) continue;

            boolean daCoNhanVienTraLoiSauDo = tinNhans.stream()
                    .filter(Objects::nonNull)
                    .filter(t -> t.getThoiGian() != null && lastKhach.getThoiGian() != null)
                    .anyMatch(t -> "NHAN_VIEN".equalsIgnoreCase(safe(t.getNguoiGui(), ""))
                            && t.getThoiGian().isAfter(lastKhach.getThoiGian()));

            if (daCoNhanVienTraLoiSauDo) continue;

            taoChoTatCaAdminNeuChuaTrung(
                    ThongBaoConstants.KHACH_CHO_PHAN_HOI_QUA_HAN,
                    ThongBaoConstants.MUC_DO_NGHIEM_TRONG,
                    "Khách chờ phản hồi quá hạn",
                    "Khách " + safe(phien.getTenKhach(), "Khách hàng")
                            + " đang chờ phản hồi ở phiên #" + phien.getId()
                            + " quá " + phutChatQuaHan + " phút.",
                    ThongBaoConstants.DOI_TUONG_PHIEN_CHAT,
                    phien.getId(),
                    "KHACH_CHO_PHAN_HOI_QUA_HAN_" + phien.getId(),
                    "{\"idPhienChat\":" + phien.getId() + ",\"idTinNhanCuoi\":" + lastKhach.getId() + "}",
                    null
            );
        }
    }

    private void quetGiaoDichThanhCongTrungLap() {
        String sql = """
                select
                    hd.id,
                    hd.ma_hoa_don,
                    count(gd.id) as so_lan_thanh_cong,
                    sum(gd.so_tien) as tong_thanh_toan,
                    hd.tong_tien_sau_giam
                from dbo.giao_dich_thanh_toan gd
                join dbo.hoa_don hd
                  on hd.id = gd.id_hoa_don
                 and hd.xoa_mem = 0
                where gd.xoa_mem = 0
                  and lower(ltrim(rtrim(gd.trang_thai))) = 'thanh_cong'
                group by hd.id, hd.ma_hoa_don, hd.tong_tien_sau_giam
                having count(gd.id) > 1 or sum(gd.so_tien) > hd.tong_tien_sau_giam
                order by hd.id desc
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        for (Object[] row : rows) {
            Integer idHoaDon = toInt(row[0]);
            String maHoaDon = toStr(row[1]);
            Integer soLan = toInt(row[2]);

            taoChoTatCaAdminNeuChuaTrung(
                    ThongBaoConstants.GIAO_DICH_BAT_THUONG,
                    ThongBaoConstants.MUC_DO_NGHIEM_TRONG,
                    "Có giao dịch bất thường cần kiểm tra",
                    "Đơn " + safe(maHoaDon, "#" + idHoaDon)
                            + " có dấu hiệu thanh toán bất thường (" + safe(soLan, 0) + " giao dịch thành công).",
                    ThongBaoConstants.DOI_TUONG_HOA_DON,
                    idHoaDon,
                    "GIAO_DICH_BAT_THUONG_THANH_CONG_" + idHoaDon,
                    "{\"idHoaDon\":" + idHoaDon + ",\"soLanThanhCong\":" + safe(soLan, 0) + "}",
                    null
            );
        }
    }

    private void quetGiaoDichThatBaiNhieuLan() {
        String sql = """
                select
                    hd.id,
                    hd.ma_hoa_don,
                    count(gd.id) as so_lan_that_bai
                from dbo.giao_dich_thanh_toan gd
                join dbo.hoa_don hd
                  on hd.id = gd.id_hoa_don
                 and hd.xoa_mem = 0
                where gd.xoa_mem = 0
                  and lower(ltrim(rtrim(gd.trang_thai))) = 'that_bai'
                  and coalesce(gd.thoi_gian_cap_nhat, gd.thoi_gian_tao) >= dateadd(minute, -60, sysdatetime())
                group by hd.id, hd.ma_hoa_don
                having count(gd.id) >= 3
                order by hd.id desc
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        for (Object[] row : rows) {
            Integer idHoaDon = toInt(row[0]);
            String maHoaDon = toStr(row[1]);
            Integer soLan = toInt(row[2]);

            taoChoTatCaAdminNeuChuaTrung(
                    ThongBaoConstants.GIAO_DICH_BAT_THUONG,
                    ThongBaoConstants.MUC_DO_CANH_BAO,
                    "Có giao dịch bất thường cần kiểm tra",
                    "Đơn " + safe(maHoaDon, "#" + idHoaDon)
                            + " có " + safe(soLan, 0) + " giao dịch thất bại trong 60 phút gần nhất.",
                    ThongBaoConstants.DOI_TUONG_HOA_DON,
                    idHoaDon,
                    "GIAO_DICH_BAT_THUONG_THAT_BAI_" + idHoaDon,
                    "{\"idHoaDon\":" + idHoaDon + ",\"soLanThatBai\":" + safe(soLan, 0) + "}",
                    null
            );
        }
    }

    private void taoChoTatCaAdminNeuChuaTrung(
            String loaiThongBao,
            Integer mucDo,
            String tieuDe,
            String noiDung,
            String loaiDoiTuong,
            Integer idDoiTuong,
            String khoaChongTrung,
            String duLieuBoSung,
            Integer nguoiTao
    ) {
        List<Integer> adminIds = layIdAdminDangHoatDong();
        if (adminIds.isEmpty()) return;

        for (Integer adminId : adminIds) {
            taoChoMotNhanVienNeuChuaTrung(
                    adminId,
                    loaiThongBao,
                    mucDo,
                    tieuDe,
                    noiDung,
                    loaiDoiTuong,
                    idDoiTuong,
                    khoaChongTrung,
                    duLieuBoSung,
                    nguoiTao
            );
        }
    }

    private void taoChoMotNhanVienNeuChuaTrung(
            Integer idNhanVienNhan,
            String loaiThongBao,
            Integer mucDo,
            String tieuDe,
            String noiDung,
            String loaiDoiTuong,
            Integer idDoiTuong,
            String khoaChongTrung,
            String duLieuBoSung,
            Integer nguoiTao
    ) {
        if (idNhanVienNhan == null) return;

        if (khoaChongTrung != null && !khoaChongTrung.isBlank()) {
            boolean daTonTai = thongBaoRepository
                    .existsByIdNhanVienNhanAndKhoaChongTrungAndXoaMemFalse(idNhanVienNhan, khoaChongTrung);
            if (daTonTai) return;
        }

        ThongBao tb = ThongBao.builder()
                .idNhanVienNhan(idNhanVienNhan)
                .loaiThongBao(loaiThongBao)
                .mucDo(mucDo)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .loaiDoiTuongLienQuan(loaiDoiTuong)
                .idDoiTuongLienQuan(idDoiTuong)
                .khoaChongTrung(khoaChongTrung)
                .duLieuBoSung(duLieuBoSung)
                .daDoc(false)
                .daXuLy(false)
                .xoaMem(false)
                .nguoiTao(nguoiTao)
                .thoiGianCapNhat(LocalDateTime.now())
                .build();

        try {
            ThongBao saved = thongBaoRepository.saveAndFlush(tb);
            ThongBao reloaded = thongBaoRepository.findById(saved.getId()).orElse(saved);
            pushThongBaoMoi(reloaded);
            pushDemChuaDoc(idNhanVienNhan);
        } catch (Exception ex) {
            log.debug("Bỏ qua thông báo trùng khóa {} cho nhân viên {}", khoaChongTrung, idNhanVienNhan);
        }
    }

    private List<Integer> layIdAdminDangHoatDong() {
        List<NhanVien> list = nhanVienRepository.findAllAdminActive();
        if (list == null || list.isEmpty()) return new ArrayList<>();
        return list.stream()
                .filter(Objects::nonNull)
                .map(NhanVien::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private void pushThongBaoMoi(ThongBao tb) {
        if (tb == null || tb.getIdNhanVienNhan() == null) return;
        messagingTemplate.convertAndSend(
                ThongBaoConstants.TOPIC_PREFIX + tb.getIdNhanVienNhan(),
                toResponse(tb)
        );
    }

    private void pushDemChuaDoc(Integer idNhanVien) {
        if (idNhanVien == null) return;
        long count = thongBaoRepository.countByIdNhanVienNhanAndDaDocFalseAndXoaMemFalse(idNhanVien);
        messagingTemplate.convertAndSend(
                ThongBaoConstants.TOPIC_PREFIX + idNhanVien + ThongBaoConstants.TOPIC_DEM_CHUA_DOC_SUFFIX,
                count
        );
    }

    private ThongBaoResponse toResponse(ThongBao tb) {
        return ThongBaoResponse.builder()
                .id(tb.getId())
                .maThongBao(tb.getMaThongBao())
                .idNhanVienNhan(tb.getIdNhanVienNhan())
                .loaiThongBao(tb.getLoaiThongBao())
                .mucDo(tb.getMucDo())
                .mucDoLabel(toMucDoLabel(tb.getMucDo()))
                .tieuDe(tb.getTieuDe())
                .noiDung(tb.getNoiDung())
                .loaiDoiTuongLienQuan(tb.getLoaiDoiTuongLienQuan())
                .idDoiTuongLienQuan(tb.getIdDoiTuongLienQuan())
                .daDoc(tb.getDaDoc())
                .daXuLy(tb.getDaXuLy())
                .thoiGianTao(tb.getThoiGianTao())
                .thoiGianDoc(tb.getThoiGianDoc())
                .thoiGianXuLy(tb.getThoiGianXuLy())
                .duLieuBoSung(tb.getDuLieuBoSung())
                .build();
    }

    private String toMucDoLabel(Integer mucDo) {
        if (mucDo == null) return "Thông tin";
        return switch (mucDo) {
            case ThongBaoConstants.MUC_DO_NGHIEM_TRONG -> "Nghiêm trọng";
            case ThongBaoConstants.MUC_DO_CANH_BAO -> "Cảnh báo";
            default -> "Thông tin";
        };
    }

    private String buildCtspLabel(ChiTietSanPham ctsp) {
        if (ctsp == null) return "Biến thể";

        List<String> parts = new ArrayList<>();
        if (ctsp.getSanPham() != null && ctsp.getSanPham().getTenSanPham() != null) {
            parts.add(ctsp.getSanPham().getTenSanPham().trim());
        }
        if (ctsp.getMauSac() != null && ctsp.getMauSac().getTenMauSac() != null) {
            parts.add("Màu " + ctsp.getMauSac().getTenMauSac().trim());
        }
        if (ctsp.getKichThuoc() != null && ctsp.getKichThuoc().getTenKichThuoc() != null) {
            parts.add("Size " + ctsp.getKichThuoc().getTenKichThuoc().trim());
        }
        if (ctsp.getLoaiSan() != null && ctsp.getLoaiSan().getTenLoaiSan() != null) {
            parts.add(ctsp.getLoaiSan().getTenLoaiSan().trim());
        }

        return parts.isEmpty() ? safe(ctsp.getMaChiTietSanPham(), "CTSP #" + ctsp.getId()) : String.join(" | ", parts);
    }

    private String jsonHoaDon(HoaDon hd) {
        if (hd == null || hd.getId() == null) return null;
        return "{\"idHoaDon\":" + hd.getId() + ",\"maHoaDon\":\"" + safe(hd.getMaHoaDon(), "") + "\"}";
    }

    private String rutGonNoiDung(String noiDung) {
        String text = safe(noiDung, "").replaceAll("\\s+", " ").trim();
        if (text.length() <= 120) return text;
        return text.substring(0, 117) + "...";
    }

    private long tinhSoPhutCho(LocalDateTime moc) {
        if (moc == null) return 0;
        long phut = Duration.between(moc, LocalDateTime.now()).toMinutes();
        return Math.max(phut, 0);
    }

    private boolean laPhienCanTiepNhan(PhienChat phien) {
        if (phien == null || phien.getId() == null) return false;
        if (phien.getNhanVien() != null) return false;

        String trangThai = safe(phien.getTrangThai(), "").trim().toUpperCase();
        return "CHO_NHAN_VIEN".equals(trangThai);
    }

    private boolean laTinNhanKhach(TinNhan tinNhan) {
        if (tinNhan == null) return false;
        return "KHACH".equalsIgnoreCase(safe(tinNhan.getNguoiGui(), ""));
    }

    private String toStr(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime toDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) return ((java.sql.Timestamp) value).toLocalDateTime();
        return null;
    }

    private String safe(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        return value.trim();
    }

    private Integer safe(Integer value, Integer fallback) {
        return value == null ? fallback : value;
    }
}