package org.example.healtech.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Model Thuoc - Kết hợp POJO (Đoạn 1) và Lớp nghiệp vụ (Đoạn 2)
 * Sử dụng BigDecimal cho tiền tệ (từ Đoạn 1)
 * Bổ sung logic nghiệp vụ và validation (từ Đoạn 2)
 */
public class Thuoc {
    private int maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private int soLuongTon;
    private BigDecimal donGia; // ✅ Lấy từ Đoạn 1: Dùng BigDecimal cho tiền tệ
    private String moTa;       // ✅ Lấy từ Đoạn 1: Bổ sung mô tả
    private LocalDate ngayTao;

    // ===== CONSTRUCTORS =====
    public Thuoc() {
        this.ngayTao = LocalDate.now(); // Mặc định là ngày hiện tại
        this.donGia = BigDecimal.ZERO; // Mặc định giá là 0
        this.soLuongTon = 0;
    }

    /**
     * Constructor rút gọn (Tạo mới)
     */
    public Thuoc(String tenThuoc, String donViTinh, int soLuongTon, BigDecimal donGia, String moTa) {
        this(); // Gọi constructor mặc định
        this.setTenThuoc(tenThuoc); // Sử dụng setter để validate
        this.setDonViTinh(donViTinh); // Sử dụng setter để validate
        this.setSoLuongTon(soLuongTon); // Sử dụng setter để validate
        this.setDonGia(donGia); // Sử dụng setter để validate
        this.moTa = moTa;
    }

    /**
     * Constructor đầy đủ (Đọc từ DB)
     */
    public Thuoc(int maThuoc, String tenThuoc, String donViTinh, int soLuongTon, BigDecimal donGia, String moTa, LocalDate ngayTao) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.donGia = donGia;
        this.moTa = moTa;
        this.ngayTao = ngayTao;
        validate(); // Đảm bảo dữ liệu đọc lên cũng hợp lệ
    }

    // ===== GETTERS & SETTERS (với Validation từ Đoạn 2) =====
    public int getMaThuoc() { return maThuoc; }
    public void setMaThuoc(int maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) {
        if (tenThuoc == null || tenThuoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thuốc không được để trống");
        }
        this.tenThuoc = tenThuoc.trim();
    }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) {
        if (donViTinh == null || donViTinh.trim().isEmpty()) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống");
        }
        this.donViTinh = donViTinh.trim();
    }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) {
            throw new IllegalArgumentException("Số lượng tồn không thể âm");
        }
        this.soLuongTon = soLuongTon;
    }

    // --- Cập nhật cho BigDecimal ---
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) {
        if (donGia == null || donGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá không thể âm");
        }
        this.donGia = donGia;
    }
    // --------------------------------

    // --- Getter/Setter cho Mô Tả (từ Đoạn 1) ---
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    // ------------------------------------------

    public LocalDate getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDate ngayTao) {
        if (ngayTao != null && ngayTao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày tạo không thể ở tương lai");
        }
        this.ngayTao = ngayTao;
    }

    // ===== BUSINESS METHODS (Giữ nguyên từ Đoạn 2) =====

    public boolean isConHang() { return soLuongTon > 0; }
    public boolean isSapHetHang() { return soLuongTon > 0 && soLuongTon < 10; }
    public boolean isHetHang() { return soLuongTon == 0; }

    public void nhapHang(int soLuongNhap) {
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0");
        }
        this.soLuongTon += soLuongNhap;
    }

    public void xuatHang(int soLuongXuat) {
        if (soLuongXuat <= 0) {
            throw new IllegalArgumentException("Số lượng xuất phải lớn hơn 0");
        }
        if (soLuongXuat > soLuongTon) {
            throw new IllegalArgumentException("Số lượng xuất vượt quá tồn kho. Tồn kho hiện tại: " + soLuongTon);
        }
        this.soLuongTon -= soLuongXuat;
    }

    // --- Cập nhật cho BigDecimal ---
    public BigDecimal tinhTongGiaTriTonKho() {
        if (this.donGia == null) return BigDecimal.ZERO;
        return this.donGia.multiply(new BigDecimal(soLuongTon));
    }
    // --------------------------------

    public boolean isThuocMoi() {
        if (ngayTao == null) return false;
        return ngayTao.isAfter(LocalDate.now().minusDays(7));
    }

    // ===== VALIDATION METHODS =====
    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void validate() {
        setTenThuoc(this.tenThuoc); // Tận dụng logic đã viết
        setDonViTinh(this.donViTinh);
        setSoLuongTon(this.soLuongTon);
        setDonGia(this.donGia);
        setNgayTao(this.ngayTao);
    }

    // ===== UTILITY METHODS =====
    @Override
    public String toString() {
        return String.format("%s - %s - Tồn: %d - Giá: %s",
                tenThuoc, donViTinh, soLuongTon, getDonGiaFormatted());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Thuoc thuoc = (Thuoc) obj;
        return maThuoc == thuoc.maThuoc && maThuoc != 0; // Chỉ so sánh mã thuốc nếu đã có
    }

    @Override
    public int hashCode() {
        return Objects.hash(maThuoc);
    }

    public Thuoc copy() {
        return new Thuoc(maThuoc, tenThuoc, donViTinh, soLuongTon, donGia, moTa, ngayTao);
    }

    public String toDisplayString() {
        return String.format("""
            📋 THÔNG TIN THUỐC
            ├─ Mã thuốc: %d
            ├─ Tên thuốc: %s
            ├─ Đơn vị tính: %s
            ├─ Mô tả: %s
            ├─ Số lượng tồn: %d
            ├─ Đơn giá: %s
            ├─ Tổng giá trị tồn kho: %s
            ├─ Trạng thái: %s
            └─ Ngày tạo: %s
            """,
                maThuoc, tenThuoc, donViTinh,
                moTa != null ? moTa : "Không có", // Thêm mô tả
                soLuongTon, getDonGiaFormatted(), // Cập nhật format
                getTongGiaTriTonKhoFormatted(), // Cập nhật format
                getTrangThai(),
                ngayTao != null ? ngayTao.toString() : "Chưa xác định");
    }

    public String getTrangThai() {
        if (isHetHang()) return "🔴 Hết hàng";
        if (isSapHetHang()) return "🟡 Sắp hết hàng";
        return "🟢 Còn hàng";
    }

    // --- Cập nhật cho BigDecimal ---
    public String getDonGiaFormatted() {
        return String.format("%,.0f VND", donGia);
    }

    public String getTongGiaTriTonKhoFormatted() {
        return String.format("%,.0f VND", tinhTongGiaTriTonKho());
    }
    // --------------------------------

    // ===== BUILDER PATTERN (Cập nhật) =====
    public static class Builder {
        private String tenThuoc;
        private String donViTinh;
        private int soLuongTon = 0;
        private BigDecimal donGia = BigDecimal.ZERO;
        private String moTa = "";

        public Builder tenThuoc(String tenThuoc) {
            this.tenThuoc = tenThuoc;
            return this;
        }
        public Builder donViTinh(String donViTinh) {
            this.donViTinh = donViTinh;
            return this;
        }
        public Builder soLuongTon(int soLuongTon) {
            this.soLuongTon = soLuongTon;
            return this;
        }
        public Builder donGia(BigDecimal donGia) {
            this.donGia = donGia;
            return this;
        }
        public Builder moTa(String moTa) {
            this.moTa = moTa;
            return this;
        }
        public Thuoc build() {
            return new Thuoc(tenThuoc, donViTinh, soLuongTon, donGia, moTa);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}