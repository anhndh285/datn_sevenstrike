package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaResponse;
import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import com.example.datn_sevenstrike.entity.PhieuGiamGiaChiTiet;
import com.example.datn_sevenstrike.entity.KhachHang;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaRepository;
import com.example.datn_sevenstrike.repository.KhachHangRepository;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaChiTietRepository;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class PhieuGiamGiaService {

    private final PhieuGiamGiaRepository repo;
    private final KhachHangRepository khachHangRepo;
    private final PhieuGiamGiaChiTietRepository chiTietRepo;
    private final ModelMapper mapper;
    private final JavaMailSender mailSender;

    public List<PhieuGiamGiaResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public PhieuGiamGiaResponse one(Integer id) {
        PhieuGiamGia e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));
        return toResponse(e);
    }

    @Transactional
    public PhieuGiamGiaResponse create(PhieuGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");
        PhieuGiamGia e = mapper.map(req, PhieuGiamGia.class);
        e.setId(null);
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getLoaiPhieuGiamGia() == null) e.setLoaiPhieuGiamGia(0);

        validate(e);
        PhieuGiamGia saved = repo.saveAndFlush(e);

        if (Integer.valueOf(1).equals(saved.getLoaiPhieuGiamGia()) && req.getIdKhachHangs() != null) {
            saveVoucherDetails(saved, req.getIdKhachHangs(), true);
        }
        return toResponse(saved);
    }

    @Transactional
    public PhieuGiamGiaResponse update(Integer id, PhieuGiamGiaRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");
        PhieuGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));

        if (req.getTenPhieuGiamGia() != null) db.setTenPhieuGiamGia(req.getTenPhieuGiamGia());
        if (req.getLoaiPhieuGiamGia() != null) db.setLoaiPhieuGiamGia(req.getLoaiPhieuGiamGia());
        if (req.getGiaTriGiamGia() != null) db.setGiaTriGiamGia(req.getGiaTriGiamGia());
        if (req.getSoTienGiamToiDa() != null) db.setSoTienGiamToiDa(req.getSoTienGiamToiDa());
        if (req.getHoaDonToiThieu() != null) db.setHoaDonToiThieu(req.getHoaDonToiThieu());
        if (req.getSoLuongSuDung() != null) db.setSoLuongSuDung(req.getSoLuongSuDung());
        if (req.getNgayBatDau() != null) db.setNgayBatDau(req.getNgayBatDau());
        if (req.getNgayKetThuc() != null) db.setNgayKetThuc(req.getNgayKetThuc());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getMoTa() != null) db.setMoTa(req.getMoTa());

        validate(db);
        PhieuGiamGia saved = repo.saveAndFlush(db);

        if (Integer.valueOf(1).equals(saved.getLoaiPhieuGiamGia())) {
            chiTietRepo.deleteByPhieuGiamGia(saved);
            if (req.getIdKhachHangs() != null) {
                saveVoucherDetails(saved, req.getIdKhachHangs(), false);
            }
        } else {
            chiTietRepo.deleteByPhieuGiamGia(saved);
        }

        return toResponse(saved);
    }

    private void saveVoucherDetails(PhieuGiamGia saved, List<Long> idKhachHangs, boolean shouldSendEmail) {
        List<String> listEmail = new ArrayList<>();
        for (Long idKh : idKhachHangs) {
            khachHangRepo.findById(idKh.intValue()).ifPresent(kh -> {
                PhieuGiamGiaChiTiet ct = PhieuGiamGiaChiTiet.builder()
                        .phieuGiamGia(saved)
                        .khachHang(kh)
                        .build();
                chiTietRepo.save(ct);
                if (kh.getEmail() != null) listEmail.add(kh.getEmail());
            });
        }
        if (shouldSendEmail && !listEmail.isEmpty()) {
            sendEmailVoucher(listEmail, saved);
        }
    }

    private void validate(PhieuGiamGia e) {
        if (e.getTenPhieuGiamGia() == null || e.getTenPhieuGiamGia().isBlank())
            throw new BadRequestEx("Tên phiếu không được để trống");
        if (e.getNgayBatDau() == null || e.getNgayKetThuc() == null)
            throw new BadRequestEx("Thiếu ngày bắt đầu/kết thúc");
        if (e.getNgayKetThuc().isBefore(e.getNgayBatDau()))
            throw new BadRequestEx("Ngày kết thúc phải sau ngày bắt đầu");
        if (e.getSoLuongSuDung() == null || e.getSoLuongSuDung() < 0)
            throw new BadRequestEx("Số lượng sử dụng phải >= 0");

        LocalDate now = LocalDate.now();
        if (now.isBefore(e.getNgayBatDau())) {
            e.setTrangThai(2);
        } else if (now.isAfter(e.getNgayKetThuc())) {
            e.setTrangThai(0);
        } else {
            if (e.getTrangThai() == null || (e.getTrangThai() != 0)) {
                e.setTrangThai(1);
            }
        }
    }

    @Async
    protected void sendEmailVoucher(List<String> emails, PhieuGiamGia voucher) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String hienThiGiam = (voucher.getGiaTriGiamGia() != null && voucher.getGiaTriGiamGia().compareTo(BigDecimal.ZERO) > 0)
                    ? voucher.getGiaTriGiamGia() + "%"
                    : formatter.format(voucher.getSoTienGiamToiDa() != null ? voucher.getSoTienGiamToiDa() : BigDecimal.ZERO) + " VNĐ";

            ClassPathResource htmlResource = new ClassPathResource("voucher_email_template.html");
            String htmlTemplate;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(htmlResource.getInputStream(), "UTF-8"))) {
                htmlTemplate = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            String finalHtmlContent = htmlTemplate
                    .replace("{{MA_GIAM_GIA}}", voucher.getMaPhieuGiamGia())
                    .replace("{{GIA_TRI_GIAM}}", hienThiGiam)
                    .replace("{{HOA_DON_TOI_THIEU}}", formatter.format(voucher.getHoaDonToiThieu()))
                    .replace("{{NGAY_KET_THUC}}", voucher.getNgayKetThuc().format(dateFormatter))
                    .replace("{{CURRENT_YEAR}}", String.valueOf(Year.now().getValue()));

            for (String email : emails) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(email);
                helper.setSubject("Mã giảm giá đặc biệt từ SevenStrike");
                helper.setText(finalHtmlContent, true);
                mailSender.send(message);
            }
        } catch (Exception ex) {
            System.err.println("Lỗi gửi mail: " + ex.getMessage());
        }
    }

    @Transactional
    public void delete(Integer id) {
        PhieuGiamGia db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy PhieuGiamGia id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private PhieuGiamGiaResponse toResponse(PhieuGiamGia e) {
        return mapper.map(e, PhieuGiamGiaResponse.class);
    }

    // Hàm trả về List Integer cho ID khách hàng
    public List<Integer> getCustomerIdsByVoucher(Integer voucherId) {
        PhieuGiamGia p = repo.findById(voucherId).orElse(null);
        if (p == null) return new ArrayList<>();
        return chiTietRepo.findAllByPhieuGiamGia(p)
                .stream()
                .map(ct -> ct.getKhachHang().getId())
                .collect(Collectors.toList());
    }
}