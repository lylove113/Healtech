package org.example.healtech.model;

import java.time.LocalDate;

/**
 * Đây là lớp Model (POJO) đại diện cho một Phiếu Khám Bệnh.
 * Nó chứa dữ liệu được lấy từ bảng PhieuKhamBenh trong CSDL.
 */
public class PhieuKham {

    // --- Thuộc tính ---
    // Phải khớp với các cột trong bảng PhieuKhamBenh
    private int maPhieuKham;
    private int maLichHen;
    private int maBenhNhan;
    private int maBacSi;
    private LocalDate ngayKham;
    private String trieuChung;
    private String chuanDoan;
    // Bạn có thể thêm các thuộc tính khác như SinhHieu, KetLuan... nếu CSDL có

    // --- Constructor ---

    /**
     * Constructor rỗng (Default constructor)
     * Rất quan trọng để các thư viện (như JavaFX) và DAO có thể tạo đối tượng
     */
    public PhieuKham() {
    }

    /**
     * Constructor đầy đủ để tạo đối tượng nhanh
     */
    public PhieuKham(int maPhieuKham, int maLichHen, int maBenhNhan, int maBacSi, LocalDate ngayKham, String trieuChung, String chuanDoan) {
        this.maPhieuKham = maPhieuKham;
        this.maLichHen = maLichHen;
        this.maBenhNhan = maBenhNhan;
        this.maBacSi = maBacSi;
        this.ngayKham = ngayKham;
        this.trieuChung = trieuChung;
        this.chuanDoan = chuanDoan;
    }

    // --- Getters và Setters ---
    // Cần thiết để DAO và JavaFX (PropertyValueFactory) truy cập dữ liệu

    public int getMaPhieuKham() {
        return maPhieuKham;
    }

    public void setMaPhieuKham(int maPhieuKham) {
        this.maPhieuKham = maPhieuKham;
    }

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

    public LocalDate getNgayKham() {
        return ngayKham;
    }

    public void setNgayKham(LocalDate ngayKham) {
        this.ngayKham = ngayKham;
    }

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String trieuChung) {
        this.trieuChung = trieuChung;
    }

    public String getChuanDoan() {
        return chuanDoan;
    }

    public void setChuanDoan(String chuanDoan) {
        this.chuanDoan = chuanDoan;
    }
}