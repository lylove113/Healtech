package org.example.healtech.model;

import java.time.LocalDateTime;

public class LichHen {
    private int maLichHen;
    private int maBenhNhan;
    private int maBacSi; // Cột MaBacSi trong DB
    private LocalDateTime thoiGianHen; // DATETIME trong DB
    private String lyDoKham; // LyDoKham trong DB
    private String trangThai; // TrangThai trong DB

    // ===== Constructors =====

    public LichHen() {
    }

    // Constructor dùng khi thêm mới lịch hẹn
    public LichHen(int maBenhNhan, int maBacSi, LocalDateTime thoiGianHen, String lyDoKham) {
        this.maBenhNhan = maBenhNhan;
        this.maBacSi = maBacSi;
        this.thoiGianHen = thoiGianHen;
        this.lyDoKham = lyDoKham;
        this.trangThai = "Đã đặt"; // Theo mặc định trong DB
    }

    // Constructor đầy đủ (khi lấy từ database)
    public LichHen(int maLichHen, int maBenhNhan, int maBacSi,
                   LocalDateTime thoiGianHen, String lyDoKham, String trangThai) {
        this.maLichHen = maLichHen;
        this.maBenhNhan = maBenhNhan;
        this.maBacSi = maBacSi;
        this.thoiGianHen = thoiGianHen;
        this.lyDoKham = lyDoKham;
        this.trangThai = trangThai;
    }

    // ===== Getters & Setters =====

    public int getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(int maLichHen) {
        this.maLichHen = maLichHen;
    }

    public int getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(int maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public int getMaBacSi() {
        return maBacSi;
    }

    public void setMaBacSi(int maBacSi) {
        this.maBacSi = maBacSi;
    }

    public LocalDateTime getThoiGianHen() {
        return thoiGianHen;
    }

    public void setThoiGianHen(LocalDateTime thoiGianHen) {
        this.thoiGianHen = thoiGianHen;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // ===== ToString =====
    @Override
    public String toString() {
        return "LichHen{" +
                "maLichHen=" + maLichHen +
                ", maBenhNhan=" + maBenhNhan +
                ", maBacSi=" + maBacSi +
                ", thoiGianHen=" + thoiGianHen +
                ", lyDoKham='" + lyDoKham + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
