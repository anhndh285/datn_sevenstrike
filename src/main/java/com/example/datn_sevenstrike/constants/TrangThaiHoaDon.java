package com.example.datn_sevenstrike.constants;

public enum TrangThaiHoaDon {
    CHO_XAC_NHAN(1, "Chờ xác nhận"),
    CHO_GIAO_HANG(2, "Chờ giao hàng"),
    DANG_VAN_CHUYEN(3, "Đang vận chuyển"),
    DA_GIAO_HANG(4, "Đã giao hàng"),
    HOAN_THANH(5, "Hoàn thành");

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
        return code != null && code == HOAN_THANH.code;
    }
}
