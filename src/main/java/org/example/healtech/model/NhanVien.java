package org.example.healtech.model;

public class NhanVien {
    private int maNhanVien;
    private String hoTen;
    private String email;
    private String matKhau;
    private String chucVu;
    private String chuyenKhoa;
    private String soDienThoai;

    public NhanVien() {
    }

    public NhanVien(String hoTen, String email, String matKhau) {
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.chucVu = "Nhân viên";
        this.chuyenKhoa = "Chưa xác định";
        this.soDienThoai = "";
    }

    // ✅ Constructor đầy đủ dùng cho DAO
    public NhanVien(int maNhanVien, String hoTen, String chuyenKhoa, String chucVu, String soDienThoai, String email) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.chuyenKhoa = chuyenKhoa;
        this.chucVu = chucVu;
        this.soDienThoai = soDienThoai;
        this.email = email;
    }

    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public String getChuyenKhoa() { return chuyenKhoa; }
    public void setChuyenKhoa(String chuyenKhoa) { this.chuyenKhoa = chuyenKhoa; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
}
