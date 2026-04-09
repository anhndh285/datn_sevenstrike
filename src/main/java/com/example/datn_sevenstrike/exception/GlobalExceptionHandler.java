package com.example.datn_sevenstrike.exception;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundEx.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundEx ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestEx.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestEx ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Dữ liệu không hợp lệ")
                .orElse("Dữ liệu không hợp lệ");

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String rootMessage = getRootMessage(ex);
        String businessMessage = mapConstraintMessage(rootMessage);

        return buildResponse(HttpStatus.BAD_REQUEST, businessMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi hệ thống");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    private String getRootMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage() == null ? "" : root.getMessage();
    }

    private String mapConstraintMessage(String rawMessage) {
        String msg = rawMessage == null ? "" : rawMessage.toLowerCase(Locale.ROOT);

        if (containsAny(msg, "ux_chat_lieu_ten_norm_alive")) {
            return "Tên chất liệu đã tồn tại";
        }
        if (containsAny(msg, "ux_co_giay_ten_norm_alive")) {
            return "Tên cỏ giày đã tồn tại";
        }
        if (containsAny(msg, "ux_form_chan_ten_norm_alive")) {
            return "Tên form chân đã tồn tại";
        }
        if (containsAny(msg, "ux_kich_thuoc_ten_norm_alive")) {
            return "Tên kích thước đã tồn tại";
        }
        if (containsAny(msg, "ux_kich_thuoc_value_alive", "ux_kich_thuoc_gia_tri_active")) {
            return "Giá trị kích thước đã tồn tại";
        }
        if (containsAny(msg, "ux_loai_san_ten_norm_alive")) {
            return "Tên loại sân đã tồn tại";
        }
        if (containsAny(msg, "ux_phong_cach_choi_ten_norm_alive")) {
            return "Tên phong cách chơi đã tồn tại";
        }
        if (containsAny(msg, "ux_thuong_hieu_ten_norm_alive")) {
            return "Tên thương hiệu đã tồn tại";
        }
        if (containsAny(msg, "ux_vi_tri_thi_dau_ten_norm_alive")) {
            return "Tên vị trí thi đấu đã tồn tại";
        }
        if (containsAny(msg, "ux_xuat_xu_ten_norm_alive")) {
            return "Tên xuất xứ đã tồn tại";
        }
        if (containsAny(msg, "ux_san_pham_ten_norm_alive")) {
            return "Tên sản phẩm đã tồn tại";
        }
        if (containsAny(msg, "ux_mau_sac_ten_norm_alive")) {
            return "Tên màu sắc đã tồn tại";
        }
        if (containsAny(msg, "ux_mau_sac_hex_norm_alive", "ux_mau_sac_hex_alive")) {
            return "Mã màu HEX đã tồn tại";
        }
        if (containsAny(msg, "ux_ctsp_variant_alive")) {
            return "Biến thể sản phẩm đã tồn tại";
        }
        if (containsAny(msg, "ux_nv_ten_tai_khoan_alive")) {
            return "Tên tài khoản nhân viên đã tồn tại";
        }
        if (containsAny(msg, "ux_nv_email_alive")) {
            return "Email nhân viên đã tồn tại";
        }
        if (containsAny(msg, "ux_kh_ten_tai_khoan_alive")) {
            return "Tên tài khoản khách hàng đã tồn tại";
        }
        if (containsAny(msg, "ux_kh_email_alive")) {
            return "Email khách hàng đã tồn tại";
        }
        if (containsAny(msg, "ux_dckh_mac_dinh_alive")) {
            return "Khách hàng chỉ được có một địa chỉ mặc định";
        }
        if (containsAny(msg, "ux_anh_ctsp_dai_dien_alive")) {
            return "Mỗi chi tiết sản phẩm chỉ được có một ảnh đại diện";
        }
        if (containsAny(msg, "ux_pggcn_alive")) {
            return "Khách hàng đã có phiếu giảm giá cá nhân này";
        }
        if (containsAny(msg, "ux_pggct_alive")) {
            return "Khách hàng đã có trong danh sách phiếu giảm giá này";
        }
        if (containsAny(msg, "ux_ctdgg_alive")) {
            return "Chi tiết sản phẩm đã tồn tại trong đợt giảm giá";
        }
        if (containsAny(msg, "ux_llv_ca_ngay_alive")) {
            return "Ca làm trong ngày này đã tồn tại";
        }
        if (containsAny(msg, "ux_llvnv_alive")) {
            return "Nhân viên đã được phân vào lịch làm việc này";
        }
        if (containsAny(msg, "ux_gc_nv_dang_hoat_dong_alive")) {
            return "Nhân viên đang có ca hoạt động, không thể tạo thêm";
        }
        if (containsAny(msg, "ux_chuc_nang_ma_alive")) {
            return "Mã chức năng đã tồn tại";
        }
        if (containsAny(msg, "ux_qhcn_alive")) {
            return "Quyền hạn - chức năng này đã tồn tại";
        }
        if (containsAny(msg, "ux_tb_chong_trung_alive")) {
            return "Thông báo này đã tồn tại";
        }

        if (msg.contains("duplicate key") || msg.contains("cannot insert duplicate key")) {
            return "Dữ liệu đã tồn tại trong hệ thống";
        }

        return "Dữ liệu không hợp lệ hoặc đã tồn tại";
    }

    private boolean containsAny(String source, String... values) {
        if (source == null || values == null) {
            return false;
        }
        for (String value : values) {
            if (value != null && source.contains(value.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}