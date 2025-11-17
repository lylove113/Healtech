package org.example.healtech.model;

import java.time.LocalDate;

public class DonThuoc {

    private int maDonThuoc;
    private int maPhieuKham;
    private int maBacSi;
    private LocalDate ngayKeDon;
    private String loiDan;

    // Constructor rỗng
    public DonThuoc() {
    }

    // Getters and Setters

    public int getMaDonThuoc() {
        return maDonThuoc;
    }

    public void setMaDonThuoc(int maDonThuoc) {
        this.maDonThuoc = maDonThuoc;
    }

    public int getMaPhieuKham() {
        return maPhieuKham;
    }

    public void setMaPhieuKham(int maPhieuKham) {
        this.maPhieuKham = maPhieuKham;
    }

    public int getMaBacSi() {
        return maBacSi;
    }

    public void setMaBacSi(int maBacSi) {
        this.maBacSi = maBacSi;
    }

    public LocalDate getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(LocalDate ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public String getLoiDan() {
        return loiDan;
    }

    public void setLoiDan(String loiDan) {
        this.loiDan = loiDan;
    }
}