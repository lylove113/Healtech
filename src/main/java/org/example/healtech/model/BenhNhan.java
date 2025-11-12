package org.example.healtech.model;

import java.time.LocalDate;

public class BenhNhan {
    private int maBenhNhan;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String tienSuBenh;

    // Constructor rỗng
    public BenhNhan() {
    }

    // Constructor đầy đủ (dùng khi đọc từ DB)
    public BenhNhan(int maBenhNhan, String hoTen, LocalDate ngaySinh, String gioiTinh, String diaChi, String soDienThoai, String tienSuBenh) {
        this.maBenhNhan = maBenhNhan;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.tienSuBenh = tienSuBenh;
    }

    // --- Getters and Setters ---
    // (Bạn sẽ cần tất cả)

    public int getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(int maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    // ... (Thêm các Getters/Setters còn lại cho GioiTinh, DiaChi, v.v...)

    // QUAN TRỌNG: ComboBox trong Controller đã được
    // cấu hình để gọi getHoTen(), không phải toString()
}