package org.example.healtech.model;

import java.time.LocalDateTime;

/**
 * Đây là một lớp DTO (Data Transfer Object) - hay còn gọi là ViewModel.
 * Nó không đại diện cho một bảng trong DB, mà đại diện cho dữ liệu
 * chúng ta muốn HIỂN THỊ lên TableView sau khi đã JOIN.
 */
public class LichHenDisplay {
    private int maLichHen;
    private LocalDateTime thoiGianHen;
    private String lyDoKham;
    private String trangThai;
    private String tenBenhNhan; // Lấy từ bảng BenhNhan (JOIN)
    private String tenBacSi;    // Lấy từ bảng NhanVien (JOIN)

    // Constructor để nhận dữ liệu từ LichHenDAO
    public LichHenDisplay(int maLichHen, LocalDateTime thoiGianHen, String lyDoKham,
                          String trangThai, String tenBenhNhan, String tenBacSi) {
        this.maLichHen = maLichHen;
        this.thoiGianHen = thoiGianHen;
        this.lyDoKham = lyDoKham;
        this.trangThai = trangThai;
        this.tenBenhNhan = tenBenhNhan;
        this.tenBacSi = tenBacSi;
    }

    // Chỉ cần Getters, vì TableView chỉ đọc dữ liệu từ đây
    public int getMaLichHen() {
        return maLichHen;
    }

    public LocalDateTime getThoiGianHen() {
        return thoiGianHen;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }
}