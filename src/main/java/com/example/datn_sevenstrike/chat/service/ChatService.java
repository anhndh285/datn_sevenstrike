package com.example.datn_sevenstrike.chat.service;

import com.example.datn_sevenstrike.chat.dto.DongPhienRequest;
import com.example.datn_sevenstrike.chat.dto.GuiTinNhanRequest;
import com.example.datn_sevenstrike.chat.dto.KhoiTaoPhienRequest;
import com.example.datn_sevenstrike.chat.dto.PhienChatDTO;
import com.example.datn_sevenstrike.chat.dto.TinNhanDTO;
import com.example.datn_sevenstrike.chat.entity.PhienChat;
import com.example.datn_sevenstrike.chat.entity.TinNhan;
import com.example.datn_sevenstrike.chat.repository.PhienChatRepository;
import com.example.datn_sevenstrike.chat.repository.TinNhanRepository;
import com.example.datn_sevenstrike.entity.KhachHang;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.repository.KhachHangRepository;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final PhienChatRepository phienChatRepo;
    private final TinNhanRepository tinNhanRepo;
    private final KhachHangRepository khachHangRepo;
    private final NhanVienRepository nhanVienRepo;
    private final GeminiService geminiService;
    private final SimpMessagingTemplate messagingTemplate;

    public static final String TRANG_THAI_BOT = "BOT_DANG_XU_LY";
    public static final String TRANG_THAI_CHO_NV = "CHO_NHAN_VIEN";
    public static final String TRANG_THAI_DANG_XU_LY = "DANG_XU_LY";
    public static final String TRANG_THAI_DA_DONG = "DA_DONG";

    public static final String LOAI_KHACH_HANG = "KHACH_HANG";
    public static final String LOAI_NOI_BO = "NOI_BO";

    @Transactional
    public PhienChatDTO khoiTaoPhien(KhoiTaoPhienRequest req) {
        if (req == null) {
            throw new RuntimeException("Yêu cầu khởi tạo phiên chat không hợp lệ");
        }

        String loai = normalizeLoai(req.getLoai());
        String tenKhachMacDinh = LOAI_NOI_BO.equals(loai) ? "Nhân viên" : "Khách vãng lai";

        log.info("[CHAT_SERVICE] Bắt đầu khởi tạo phiên - loai={}, tenKhach={}, khachHangId={}, nhanVienId={}",
                loai, req.getTenKhach(), req.getKhachHangId(), req.getNhanVienId());

        PhienChat phien = new PhienChat();
        phien.setTrangThai(TRANG_THAI_BOT);
        phien.setThoiGianBatDau(LocalDateTime.now());
        phien.setLoai(loai);
        phien.setTenKhach(normalizeText(req.getTenKhach(), tenKhachMacDinh));

        if (LOAI_NOI_BO.equals(loai)) {
            if (req.getNhanVienId() != null) {
                Optional<NhanVien> optionalNhanVien = nhanVienRepo.findById(req.getNhanVienId());
                if (optionalNhanVien.isPresent()) {
                    NhanVien nv = optionalNhanVien.get();
                    phien.setNhanVien(nv);
                    phien.setTenKhach(normalizeText(nv.getTenNhanVien(), phien.getTenKhach()));
                } else {
                    log.warn("[CHAT_SERVICE] Không tìm thấy nhân viên id={} khi tạo phiên nội bộ", req.getNhanVienId());
                }
            }
        } else {
            if (req.getKhachHangId() != null) {
                Optional<KhachHang> optionalKhachHang = khachHangRepo.findById(req.getKhachHangId());
                if (optionalKhachHang.isPresent()) {
                    phien.setKhachHang(optionalKhachHang.get());
                } else {
                    log.warn("[CHAT_SERVICE] Không tìm thấy khách hàng id={} khi tạo phiên khách hàng", req.getKhachHangId());
                }
            }
        }

        log.info("[CHAT_SERVICE] Trước saveAndFlush phien_chat - tenKhach={}, loai={}, trangThai={}",
                phien.getTenKhach(), phien.getLoai(), phien.getTrangThai());

        PhienChat saved = phienChatRepo.saveAndFlush(phien);

        log.info("[CHAT_SERVICE] Đã lưu phien_chat id={}", saved.getId());

        String loiChao = LOAI_NOI_BO.equals(loai)
                ? "Xin chào " + saved.getTenKhach() + "! Tôi là trợ lý AI nội bộ SevenStrike. "
                + "Tôi có thể hỗ trợ bạn về quy trình bán hàng, hóa đơn, lịch làm việc và chính sách nội bộ. Bạn cần hỗ trợ gì?"
                : "Xin chào! Tôi là trợ lý AI của SevenStrike. Tôi có thể giúp bạn về sản phẩm, đơn hàng, và chính sách mua hàng. Bạn cần hỗ trợ gì?";

        TinNhan loiChaoTin = TinNhan.builder()
                .phienChat(saved)
                .nguoiGui("BOT")
                .tenNguoiGui("SevenStrike AI")
                .noiDung(loiChao)
                .thoiGian(LocalDateTime.now())
                .build();

        log.info("[CHAT_SERVICE] Trước saveAndFlush tin_nhan mở đầu cho phienChatId={}", saved.getId());

        tinNhanRepo.saveAndFlush(loiChaoTin);

        log.info("[CHAT_SERVICE] Đã lưu tin nhắn mở đầu cho phienChatId={}", saved.getId());

        PhienChatDTO dto = toDTO(saved);

        log.info("[CHAT_SERVICE] Khởi tạo phiên thành công - phienChatId={}", dto.getId());

        return dto;
    }

    @Transactional
    public void xuLyTinNhanKhach(Integer phienChatId, GuiTinNhanRequest req) {
        PhienChat phien = phienChatRepo.findById(phienChatId)
                .orElseThrow(() -> new RuntimeException("Phiên chat không tồn tại: " + phienChatId));

        TinNhan tinNhanKhach = TinNhan.builder()
                .phienChat(phien)
                .nguoiGui("KHACH")
                .tenNguoiGui(req.getTenNguoiGui() != null ? req.getTenNguoiGui() : phien.getTenKhach())
                .noiDung(req.getNoiDung())
                .thoiGian(LocalDateTime.now())
                .build();
        TinNhan savedKhach = tinNhanRepo.save(tinNhanKhach);

        TinNhanDTO khachDTO = toTinNhanDTO(savedKhach);
        messagingTemplate.convertAndSend("/topic/chat/" + phienChatId, khachDTO);

        if (TRANG_THAI_BOT.equals(phien.getTrangThai())) {
            boolean isNoiBo = LOAI_NOI_BO.equals(phien.getLoai());
            String noiDung = req.getNoiDung() != null ? req.getNoiDung().trim() : "";

            boolean isDirectEscalate =
                    noiDung.contains("Tôi cần gặp Admin") ||
                            noiDung.contains("Tôi muốn nói chuyện với nhân viên hỗ trợ");

            if (isDirectEscalate) {
                xuLyEscalate(phien, isNoiBo);
            } else {
                String geminiReply = isNoiBo
                        ? geminiService.hoiGeminiNoiBo(noiDung)
                        : geminiService.hoiGemini(noiDung);

                if ("CHUYEN_NHAN_VIEN".equals(geminiReply)) {
                    xuLyEscalate(phien, isNoiBo);
                } else {
                    TinNhan botReply = TinNhan.builder()
                            .phienChat(phien)
                            .nguoiGui("BOT")
                            .tenNguoiGui("SevenStrike AI")
                            .noiDung(geminiReply)
                            .thoiGian(LocalDateTime.now())
                            .build();
                    TinNhan savedBot = tinNhanRepo.save(botReply);
                    messagingTemplate.convertAndSend("/topic/chat/" + phienChatId, toTinNhanDTO(savedBot));
                }
            }
        }
    }

    private void xuLyEscalate(PhienChat phien, boolean isNoiBo) {
        phien.setTrangThai(TRANG_THAI_CHO_NV);
        phienChatRepo.save(phien);

        String thongBao = isNoiBo
                ? "Yêu cầu của bạn cần sự phê duyệt của Admin. Đang kết nối với Admin, vui lòng chờ..."
                : "Tôi đã kết nối bạn với nhân viên hỗ trợ. Vui lòng chờ trong giây lát...";

        TinNhan botNotify = TinNhan.builder()
                .phienChat(phien)
                .nguoiGui("BOT")
                .tenNguoiGui("SevenStrike AI")
                .noiDung(thongBao)
                .thoiGian(LocalDateTime.now())
                .build();
        TinNhan savedNotify = tinNhanRepo.save(botNotify);
        messagingTemplate.convertAndSend("/topic/chat/" + phien.getId(), toTinNhanDTO(savedNotify));

        String notifyTopic = isNoiBo ? "/topic/admin/noibo-notifications" : "/topic/admin/notifications";
        messagingTemplate.convertAndSend(notifyTopic, toDTO(phien));
    }

    @Transactional
    public void nhanVienGuiTin(Integer phienChatId, GuiTinNhanRequest req) {
        PhienChat phien = phienChatRepo.findById(phienChatId)
                .orElseThrow(() -> new RuntimeException("Phiên chat không tồn tại: " + phienChatId));

        TinNhan tin = TinNhan.builder()
                .phienChat(phien)
                .nguoiGui("NHAN_VIEN")
                .tenNguoiGui(req.getTenNguoiGui())
                .noiDung(req.getNoiDung())
                .thoiGian(LocalDateTime.now())
                .build();
        TinNhan saved = tinNhanRepo.save(tin);
        messagingTemplate.convertAndSend("/topic/chat/" + phienChatId, toTinNhanDTO(saved));
    }

    @Transactional
    public PhienChatDTO nhanVienNhanPhien(Integer phienChatId, Integer nhanVienId) {
        PhienChat phien = phienChatRepo.findById(phienChatId)
                .orElseThrow(() -> new RuntimeException("Phiên chat không tồn tại"));

        if (nhanVienId != null) {
            nhanVienRepo.findById(nhanVienId).ifPresent(phien::setNhanVien);
        }
        phien.setTrangThai(TRANG_THAI_DANG_XU_LY);
        PhienChat saved = phienChatRepo.save(phien);

        String tenNV = saved.getNhanVien() != null
                ? normalizeText(saved.getNhanVien().getTenNhanVien(), "Nhân viên")
                : "Nhân viên";
        String maNV = saved.getNhanVien() != null ? String.valueOf(saved.getNhanVien().getId()) : "";

        TinNhan notify = TinNhan.builder()
                .phienChat(saved)
                .nguoiGui("BOT")
                .tenNguoiGui("SevenStrike AI")
                .noiDung("Nhân viên " + tenNV + " (Mã: " + maNV + ") đã tiếp nhận hỗ trợ bạn.")
                .thoiGian(LocalDateTime.now())
                .build();
        TinNhan savedNotify = tinNhanRepo.save(notify);
        messagingTemplate.convertAndSend("/topic/chat/" + phienChatId, toTinNhanDTO(savedNotify));

        boolean isNoiBo = LOAI_NOI_BO.equals(saved.getLoai());
        String notifyTopic = isNoiBo ? "/topic/admin/noibo-notifications" : "/topic/admin/notifications";
        messagingTemplate.convertAndSend(notifyTopic, toDTO(saved));

        return toDTO(saved);
    }

    @Transactional
    public void dongPhien(Integer phienChatId) {
        dongPhien(phienChatId, null);
    }

    @Transactional
    public void dongPhien(Integer phienChatId, DongPhienRequest req) {
        PhienChat phien = phienChatRepo.findById(phienChatId)
                .orElseThrow(() -> new RuntimeException("Phiên chat không tồn tại"));

        if (req != null && "NHAN_VIEN".equals(req.getVaiTro())) {
            Integer nvId = phien.getNhanVien() != null ? phien.getNhanVien().getId() : null;
            if (!Objects.equals(nvId, req.getNguoiDongId())) {
                throw new RuntimeException("Không có quyền đóng phiên này");
            }
        }

        phien.setTrangThai(TRANG_THAI_DA_DONG);
        phien.setThoiGianKetThuc(LocalDateTime.now());
        phienChatRepo.save(phien);

        TinNhan notify = TinNhan.builder()
                .phienChat(phien)
                .nguoiGui("BOT")
                .tenNguoiGui("SevenStrike AI")
                .noiDung("Phiên hỗ trợ đã kết thúc. Cảm ơn bạn đã liên hệ với SevenStrike!")
                .thoiGian(LocalDateTime.now())
                .build();
        TinNhan saved = tinNhanRepo.save(notify);
        messagingTemplate.convertAndSend("/topic/chat/" + phienChatId, toTinNhanDTO(saved));

        String notifyTopic = LOAI_NOI_BO.equals(phien.getLoai())
                ? "/topic/admin/noibo-notifications"
                : "/topic/admin/notifications";
        messagingTemplate.convertAndSend(notifyTopic, toDTO(phien));
    }

    public List<PhienChatDTO> layDanhSachPhien(String trangThai, String loai) {
        List<PhienChat> list;
        if (loai != null && !loai.isBlank()) {
            list = (trangThai != null && !trangThai.isBlank())
                    ? phienChatRepo.findByLoaiAndTrangThaiOrderByThoiGianBatDauDesc(loai, trangThai)
                    : phienChatRepo.findByLoaiAndTrangThaiNotOrderByThoiGianBatDauDesc(loai, TRANG_THAI_DA_DONG);
        } else {
            list = (trangThai != null && !trangThai.isBlank())
                    ? phienChatRepo.findByTrangThaiOrderByThoiGianBatDauDesc(trangThai)
                    : phienChatRepo.findByTrangThaiNotOrderByThoiGianBatDauDesc(TRANG_THAI_DA_DONG);
        }
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<PhienChatDTO> findById(Integer id) {
        return phienChatRepo.findById(id).map(this::toDTO);
    }

    public List<TinNhanDTO> layTinNhan(Integer phienChatId) {
        return tinNhanRepo.findByPhienChat_IdOrderByThoiGianAsc(phienChatId)
                .stream()
                .map(this::toTinNhanDTO)
                .collect(Collectors.toList());
    }

    private PhienChatDTO toDTO(PhienChat p) {
        List<TinNhan> tinNhans = tinNhanRepo.findByPhienChat_IdOrderByThoiGianAsc(p.getId());
        String tinNhanCuoi = tinNhans.isEmpty() ? null : tinNhans.get(tinNhans.size() - 1).getNoiDung();

        return PhienChatDTO.builder()
                .id(p.getId())
                .tenKhach(p.getTenKhach())
                .loai(p.getLoai())
                .trangThai(p.getTrangThai())
                .thoiGianBatDau(p.getThoiGianBatDau())
                .tinNhanCuoi(tinNhanCuoi)
                .soTinNhan(tinNhans.size())
                .nhanVienId(p.getNhanVien() != null ? p.getNhanVien().getId() : null)
                .build();
    }

    private TinNhanDTO toTinNhanDTO(TinNhan t) {
        return TinNhanDTO.builder()
                .id(t.getId())
                .phienChatId(t.getPhienChat().getId())
                .nguoiGui(t.getNguoiGui())
                .tenNguoiGui(t.getTenNguoiGui())
                .noiDung(t.getNoiDung())
                .thoiGian(t.getThoiGian())
                .build();
    }

    private String normalizeLoai(String loai) {
        if (loai == null || loai.isBlank()) {
            return LOAI_KHACH_HANG;
        }
        return loai.trim().toUpperCase();
    }

    private String normalizeText(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }
}