package org.example.healtech.model;

public class ChiTietDichVu {
    private String tenDichVu;
    private Double phiDichVu; // Hoặc giaDichVu, tùy bạn đặt, nhưng phải thống nhất

    public ChiTietDichVu(String tenDichVu, Double phiDichVu) {
        this.tenDichVu = tenDichVu;
        this.phiDichVu = phiDichVu;
    }

    // --- CÁC GETTER BẮT BUỘC (TableView dùng cái này để lấy dữ liệu) ---

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    // Quan trọng: Tên getter phải khớp với PropertyValueFactory
    public Double getPhiDichVu() {
        return phiDichVu;
    }

    public void setPhiDichVu(Double phiDichVu) {
        this.phiDichVu = phiDichVu;
    }
}