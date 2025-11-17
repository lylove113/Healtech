package org.example.healtech.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model NhanVien được thiết kế để tương thích với JavaFX TableView
 * (Sử dụng Properties thay vì kiểu dữ liệu gốc).
 */
public class NhanVien {

    // --- Properties ---
    // Sử dụng Property để binding dữ liệu trực tiếp với cột trong TableView (Real-time update)
    private final IntegerProperty maNhanVien = new SimpleIntegerProperty();
    private final StringProperty hoTen = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty matKhau = new SimpleStringProperty();
    private final StringProperty chucVu = new SimpleStringProperty();
    private final StringProperty chuyenKhoa = new SimpleStringProperty();
    private final StringProperty soDienThoai = new SimpleStringProperty();

    // --- Constructors ---

    /**
     * Constructor mặc định.
     */
    public NhanVien() {
    }

    /**
     * Constructor đầy đủ (Dùng khi DAO đọc dữ liệu từ CSDL lên).
     */
    public NhanVien(int maNhanVien, String hoTen, String email, String matKhau,
                    String chucVu, String chuyenKhoa, String soDienThoai) {
        this.maNhanVien.set(maNhanVien);
        this.hoTen.set(hoTen);
        this.email.set(email);
        this.matKhau.set(matKhau);
        this.chucVu.set(chucVu);
        this.chuyenKhoa.set(chuyenKhoa);
        this.soDienThoai.set(soDienThoai);
    }

    /**
     * Constructor rút gọn (Dùng khi tạo mới nhân viên, VD: đăng ký).
     * Tự động gán các giá trị mặc định để tránh bị null.
     */
    public NhanVien(String hoTen, String email, String matKhau) {
        this.hoTen.set(hoTen);
        this.email.set(email);
        this.matKhau.set(matKhau);

        // Gán giá trị mặc định
        this.chucVu.set("Nhân viên");
        this.chuyenKhoa.set("Chưa xác định");
        this.soDienThoai.set("");
    }

    // --- Getters & Setters (Truy cập giá trị thường - int, String) ---

    public int getMaNhanVien() { return maNhanVien.get(); }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien.set(maNhanVien); }

    public String getHoTen() { return hoTen.get(); }
    public void setHoTen(String hoTen) { this.hoTen.set(hoTen); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public String getMatKhau() { return matKhau.get(); }
    public void setMatKhau(String matKhau) { this.matKhau.set(matKhau); }

    public String getChucVu() { return chucVu.get(); }
    public void setChucVu(String chucVu) { this.chucVu.set(chucVu); }

    public String getChuyenKhoa() { return chuyenKhoa.get(); }
    public void setChuyenKhoa(String chuyenKhoa) { this.chuyenKhoa.set(chuyenKhoa); }

    public String getSoDienThoai() { return soDienThoai.get(); }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai.set(soDienThoai); }

    // --- Property Methods (Truy cập đối tượng Property) ---
    // Dùng để binding với TableColumn. Ví dụ: column.setCellValueFactory(...)

    public IntegerProperty maNhanVienProperty() { return maNhanVien; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty emailProperty() { return email; }
    public StringProperty matKhauProperty() { return matKhau; }
    public StringProperty chucVuProperty() { return chucVu; }
    public StringProperty chuyenKhoaProperty() { return chuyenKhoa; }
    public StringProperty soDienThoaiProperty() { return soDienThoai; }

    /**
     * Hiển thị tên trong ComboBox hoặc log (tránh in ra địa chỉ bộ nhớ).
     */
    @Override
    public String toString() {
        String cv = (chucVu.get() != null && !chucVu.get().isEmpty()) ? chucVu.get() : "Chưa xác định";
        String ten = (hoTen.get() != null) ? hoTen.get() : "Chưa có tên";
        return ten + " (" + cv + ")";
    }
}