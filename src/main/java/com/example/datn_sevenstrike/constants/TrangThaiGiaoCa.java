package com.example.datn_sevenstrike.constants;

public enum TrangThaiGiaoCa {
    DANG_HOAT_DONG(0, "Đang hoạt động"),
    DA_DONG_CA(1, "Đã đóng ca");

    public final int code;
    public final String label;

    TrangThaiGiaoCa(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static TrangThaiGiaoCa fromCode(Integer code) {
        if (code == null) return null;
        for (TrangThaiGiaoCa v : values()) {
            if (v.code == code) return v;
        }
        return null;
    }

    // ✅ Thêm để dùng: TrangThaiGiaoCa.DANG_HOAT_DONG.codeEquals(gc.getTrangThai())
    public boolean codeEquals(Integer code) {
        return code != null && this.code == code;
    }
}
