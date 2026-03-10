// File: src/main/java/com/example/datn_sevenstrike/constants/TrangThaiHoaDon.java
package com.example.datn_sevenstrike.constants;

public enum TrangThaiHoaDon {
    CHUA_XAC_NHAN(1, "Chưa xác nhận"),
    DA_XAC_NHAN(2, "Đã xác nhận"),
    CHO_GIAO(3, "Chờ giao"),
    DANG_GIAO(4, "Đang giao"),
    DA_HOAN_THANH(5, "Đã hoàn thành"),
    DA_HUY(6, "Đã hủy"),
    YEU_CAU_HUY(7, "Yêu cầu hủy");

    public final int code;
    public final String label;

    TrangThaiHoaDon(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static TrangThaiHoaDon fromCode(Integer code) {
        if (code == null) return null;
        for (TrangThaiHoaDon v : values()) {
            if (v.code == code) return v;
        }
        return null;
    }

    public static boolean isTerminal(Integer code) {
        return code != null && (code == DA_HOAN_THANH.code || code == DA_HUY.code);
    }

    public static boolean isValid(Integer code) {
        return fromCode(code) != null;
    }
}