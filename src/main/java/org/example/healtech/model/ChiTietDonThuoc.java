package org.example.healtech.model;

public class ChiTietDonThuoc {

    private int maChiTiet;
    private int maDonThuoc;
    private int maThuoc;
    private int soLuong;
    private String lieuDung;
    private String cachDung;

    // Constructor rỗng
    public ChiTietDonThuoc() {
    }

    // Getters and Setters

    public int getMaChiTiet() {
        return maChiTiet;
    }

    public void setMaChiTiet(int maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public int getMaDonThuoc() {
        return maDonThuoc;
    }

    public void setMaDonThuoc(int maDonThuoc) {
        this.maDonThuoc = maDonThuoc;
    }

    public int getMaThuoc() {
        return maThuoc;
    }

    public void setMaThuoc(int maThuoc) {
        this.maThuoc = maThuoc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getLieuDung() {
        return lieuDung;
    }

    public void setLieuDung(String lieuDung) {
        this.lieuDung = lieuDung;
    }

    public String getCachDung() {
        return cachDung;
    }

    public void setCachDung(String cachDung) {
        this.cachDung = cachDung;
    }
}