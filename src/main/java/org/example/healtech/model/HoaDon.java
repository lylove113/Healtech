package org.example.healtech.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model này tương ứng với bảng 'HoaDon' trong CSDL.
 */
public class HoaDon {

    private int maHoaDon;
    private int maPhieuKham;
    private int maBenhNhan;
    private String tenBenhNhan; // Bổ sung từ đoạn 1: Dùng để hiển thị trên UI (Table)
    private LocalDate ngayTao;  // Bổ sung từ đoạn 1: Ngày tạo hóa đơn
    private LocalDateTime ngayThanhToan; // Chọn LocalDateTime để lưu cả giờ phút thanh toán chính xác
    private double tongTien;
    private String trangThai;

    // --- Constructors ---

    // 1. Constructor mặc định (cần thiết cho một số thư viện mapping)
    public HoaDon() {
    }

    // 2. Constructor đầy đủ (Dùng khi đọc dữ liệu từ CSDL lên bao gồm cả ID và Tên hiển thị)
    public HoaDon(int maHoaDon, int maPhieuKham, int maBenhNhan, String tenBenhNhan,
                  LocalDate ngayTao, LocalDateTime ngayThanhToan, double tongTien, String trangThai) {
        this.maHoaDon = maHoaDon;
        this.maPhieuKham = maPhieuKham;
        this.maBenhNhan = maBenhNhan;
        this.tenBenhNhan = tenBenhNhan;
        this.ngayTao = ngayTao;
        this.ngayThanhToan = ngayThanhToan;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    // 3. Constructor rút gọn (Dùng khi tạo mới object để insert vào CSDL - chưa có ID)
    public HoaDon(int maPhieuKham, int maBenhNhan, LocalDate ngayTao, double tongTien, String trangThai) {
        this.maPhieuKham = maPhieuKham;
        this.maBenhNhan = maBenhNhan;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    // --- Getters ---
    public int getMaHoaDon() { return maHoaDon; }
    public int getMaPhieuKham() { return maPhieuKham; }
    public int getMaBenhNhan() { return maBenhNhan; }
    public String getTenBenhNhan() { return tenBenhNhan; }
    public LocalDate getNgayTao() { return ngayTao; }
    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }
    public double getTongTien() { return tongTien; }
    public String getTrangThai() { return trangThai; }

    // --- Setters (Cần thiết để cập nhật dữ liệu sau khi lấy từ DB hoặc chỉnh sửa) ---
    public void setMaHoaDon(int maHoaDon) { this.maHoaDon = maHoaDon; }
    public void setMaPhieuKham(int maPhieuKham) { this.maPhieuKham = maPhieuKham; }
    public void setMaBenhNhan(int maBenhNhan) { this.maBenhNhan = maBenhNhan; }

    public void setTenBenhNhan(String tenBenhNhan) { this.tenBenhNhan = tenBenhNhan; }

    public void setNgayTao(LocalDate ngayTao) { this.ngayTao = ngayTao; }

    // Setter cập nhật ngày thanh toán (quan trọng cho logic thanh toán)
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }

    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    // Setter cập nhật trạng thái
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon=" + maHoaDon +
                ", tenBenhNhan='" + tenBenhNhan + '\'' +
                ", tongTien=" + tongTien +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}