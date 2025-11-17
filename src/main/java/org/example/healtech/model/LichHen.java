package org.example.healtech.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model LichHen kết hợp giữa dữ liệu lưu trữ (Entity) và dữ liệu hiển thị (DTO).
 */
public class LichHen {

    // --- Các trường khớp với bảng LichHen trong CSDL ---
    private int maLichHen;
    private int maBenhNhan;
    private int maBacSi;        // Bổ sung từ đoạn 2: Cần thiết để biết khám bác sĩ nào
    private LocalDateTime thoiGianHen;
    private String lyDoKham;
    private String trangThai;

    // --- Các trường bổ sung để hiển thị trên UI (Lấy từ bảng BenhNhan qua JOIN) ---
    private String tenBenhNhan; // Bổ sung từ đoạn 1
    private String gioitinh;    // Bổ sung từ đoạn 1
    private LocalDate ngaySinh; // Bổ sung từ đoạn 1

    // ================= Constructors =================

    // 1. Constructor mặc định
    public LichHen() {
    }

    // 2. Constructor dùng để THÊM MỚI (Insert vào DB)
    // Khi thêm mới, ta chưa có ID lịch hẹn và chưa cần thông tin chi tiết bệnh nhân để hiển thị
    public LichHen(int maBenhNhan, int maBacSi, LocalDateTime thoiGianHen, String lyDoKham) {
        this.maBenhNhan = maBenhNhan;
        this.maBacSi = maBacSi;
        this.thoiGianHen = thoiGianHen;
        this.lyDoKham = lyDoKham;
        this.trangThai = "Đã đặt"; // Trạng thái mặc định
    }

    // 3. Constructor ĐẦY ĐỦ (Dùng khi SELECT từ DB lên để hiển thị ra bảng)
    // Bao gồm cả thông tin của bảng Lịch Hẹn và thông tin Bệnh nhân đã Join
    public LichHen(int maLichHen, int maBenhNhan, int maBacSi, String tenBenhNhan, String gioitinh,
                   LocalDate ngaySinh, LocalDateTime thoiGianHen, String lyDoKham, String trangThai) {
        this.maLichHen = maLichHen;
        this.maBenhNhan = maBenhNhan;
        this.maBacSi = maBacSi;
        this.tenBenhNhan = tenBenhNhan;
        this.gioitinh = gioitinh;
        this.ngaySinh = ngaySinh;
        this.thoiGianHen = thoiGianHen;
        this.lyDoKham = lyDoKham;
        this.trangThai = trangThai;
    }

    // ================= Getters & Setters =================

    public int getMaLichHen() { return maLichHen; }
    public void setMaLichHen(int maLichHen) { this.maLichHen = maLichHen; }

    public int getMaBenhNhan() { return maBenhNhan; }
    public void setMaBenhNhan(int maBenhNhan) { this.maBenhNhan = maBenhNhan; }

    public int getMaBacSi() { return maBacSi; }
    public void setMaBacSi(int maBacSi) { this.maBacSi = maBacSi; }

    public LocalDateTime getThoiGianHen() { return thoiGianHen; }
    public void setThoiGianHen(LocalDateTime thoiGianHen) { this.thoiGianHen = thoiGianHen; }

    public String getLyDoKham() { return lyDoKham; }
    public void setLyDoKham(String lyDoKham) { this.lyDoKham = lyDoKham; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    // --- Getters/Setters cho các trường hiển thị ---

    public String getTenBenhNhan() { return tenBenhNhan; }
    public void setTenBenhNhan(String tenBenhNhan) { this.tenBenhNhan = tenBenhNhan; }

    public String getGioitinh() { return gioitinh; }
    public void setGioitinh(String gioitinh) { this.gioitinh = gioitinh; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    // Helper: Tính năm sinh hoặc tuổi để hiển thị cho gọn trên Table
    public int getNamSinh() {
        return ngaySinh != null ? ngaySinh.getYear() : 0;
    }

    // ================= ToString =================
    @Override
    public String toString() {
        return "LichHen{" +
                "maLH=" + maLichHen +
                ", bn='" + tenBenhNhan + '\'' +
                ", time=" + thoiGianHen +
                ", status='" + trangThai + '\'' +
                '}';
    }
}