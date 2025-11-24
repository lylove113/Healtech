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


    private final IntegerProperty maNhanVien = new SimpleIntegerProperty();
    private final StringProperty hoTen = new SimpleStringProperty();
    private final StringProperty chuyenKhoa = new SimpleStringProperty();
    private final StringProperty chucVu = new SimpleStringProperty();
    private final StringProperty soDienThoai = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty matKhau = new SimpleStringProperty();

    public NhanVien() {}

    public NhanVien(int maNhanVien, String hoTen, String chuyenKhoa,
                    String chucVu, String soDienThoai, String email, String matKhau) {
        this.maNhanVien.set(maNhanVien);
        this.hoTen.set(hoTen);
        this.chuyenKhoa.set(chuyenKhoa);
        this.chucVu.set(chucVu);
        this.soDienThoai.set(soDienThoai);
        this.email.set(email);
        this.matKhau.set(matKhau);
    }


    public NhanVien(String hoTen, String email, String matKhau) {
        this.hoTen.set(hoTen);
        this.email.set(email);
        this.matKhau.set(matKhau);
    }


    public int getMaNhanVien() {
        return maNhanVien.get();
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien.set(maNhanVien);
    }

    public String getHoTen() {
        return hoTen.get();
    }

    public void setHoTen(String hoTen) {
        this.hoTen.set(hoTen);
    }

    public String getChuyenKhoa() {
        return chuyenKhoa.get();
    }

    public void setChuyenKhoa(String chuyenKhoa) {
        this.chuyenKhoa.set(chuyenKhoa);
    }

    public String getChucVu() {
        return chucVu.get();
    }

    public void setChucVu(String chucVu) {
        this.chucVu.set(chucVu);
    }

    public String getSoDienThoai() {
        return soDienThoai.get();
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai.set(soDienThoai);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getMatKhau() {
        return matKhau.get();
    }

    public void setMatKhau(String matKhau) {
        this.matKhau.set(matKhau);
    }


    public IntegerProperty maNhanVienProperty() {
        return maNhanVien;
    }

    public StringProperty hoTenProperty() {
        return hoTen;
    }

    public StringProperty chuyenKhoaProperty() {
        return chuyenKhoa;
    }

    public StringProperty chucVuProperty() {
        return chucVu;
    }

    public StringProperty soDienThoaiProperty() {
        return soDienThoai;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty matKhauProperty() {
        return matKhau;
    }


    @Override
    public String toString() {
        return hoTen.get() + " (" + (chucVu.get() != null ? chucVu.get() : "Chưa xác định") + ")";
    }
}