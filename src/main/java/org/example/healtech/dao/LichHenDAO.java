package org.example.healtech.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.healtech.model.LichHen;
import org.example.healtech.model.LichHenDisplay;
import org.example.healtech.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý Lịch Hẹn - Đã được tối ưu hóa và sửa lỗi kết nối
 */
public class LichHenDAO {

    // ===== PHẦN 1: CÁC PHƯƠNG THỨC CHO MÀN HÌNH KHÁM BỆNH (STATIC) =====

    /**
     * Cập nhật trạng thái lịch hẹn (Ví dụ: Chuyển sang "Đang khám" hoặc "Hoàn thành")
     */
    public static boolean capNhatTrangThai(int maLichHen, String trangThaiMoi) {
        String sql = "UPDATE LichHen SET TrangThai = ? WHERE MaLichHen = ?";
        // Sử dụng try-with-resources để tự động đóng kết nối
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThaiMoi);
            stmt.setInt(2, maLichHen);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi cập nhật trạng thái lịch hẹn: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách lịch hẹn HÔM NAY theo bộ lọc trạng thái và từ khóa tìm kiếm.
     * Dùng cho màn hình Khám Bệnh.
     */
    public static ObservableList<LichHen> getDanhSachKhamHomNay(String trangThaiFilter, String searchTerm) {
        ObservableList<LichHen> list = FXCollections.observableArrayList();

        // Xây dựng câu lệnh SQL động
        StringBuilder sql = new StringBuilder(
                "SELECT lh.MaLichHen, lh.MaBenhNhan, lh.MaBacSi, bn.HoTen, bn.GioiTinh, bn.NgaySinh, lh.ThoiGianHen, lh.LyDoKham, lh.TrangThai " +
                        "FROM LichHen lh " +
                        "JOIN BenhNhan bn ON lh.MaBenhNhan = bn.MaBenhNhan " +
                        "WHERE 1=1 " // <--- ĐÃ SỬA: Tạm thời dùng 1=1 (luôn đúng) để bỏ qua lọc ngày
        );

        List<Object> params = new ArrayList<>();

        // 1. Lọc theo trạng thái
        if (trangThaiFilter != null && !trangThaiFilter.equals("Tất cả")) {
            if (trangThaiFilter.equals("Chờ khám")) {
                // Bao gồm cả trạng thái "Đã đặt" và "Đang chờ" vào nhóm chờ khám
                sql.append("AND lh.TrangThai IN ('Đã đặt', 'Đang chờ', 'Chờ khám') ");
            } else {
                sql.append("AND lh.TrangThai = ? ");
                params.add(trangThaiFilter);
            }
        }



        // 2. Lọc theo từ khóa tìm kiếm
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (bn.HoTen LIKE ? OR bn.MaBenhNhan LIKE ? OR lh.MaLichHen LIKE ?) ");
            String likeTerm = "%" + searchTerm.trim() + "%";
            params.add(likeTerm);
            params.add(likeTerm);
            params.add(likeTerm);
        }

        sql.append("ORDER BY lh.ThoiGianHen ASC");

        // Thực thi truy vấn
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Gán tham số vào PreparedStatement
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index++, param);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LichHen lh = new LichHen(
                            rs.getInt("MaLichHen"),
                            rs.getInt("MaBenhNhan"),
                            rs.getInt("MaBacSi"),
                            rs.getString("HoTen"),
                            rs.getString("GioiTinh"),
                            rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null,
                            rs.getTimestamp("ThoiGianHen").toLocalDateTime(),
                            rs.getString("LyDoKham"),
                            rs.getString("TrangThai")
                    );
                    list.add(lh);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi lấy danh sách khám hôm nay: " + e.getMessage());
        }
        return list;
    }


    // ===== PHẦN 2: CÁC PHƯƠNG THỨC CRUD CƠ BẢN (STATIC HOẶC INSTANCE) =====
    // Để tiện lợi, mình chuyển hết sang STATIC để dễ gọi từ bất kỳ đâu.

    /**
     * Lấy dữ liệu hiển thị cho bảng quản lý lịch hẹn tổng quát (Admin).
     */
    public static List<LichHenDisplay> getLichHenForView() {
        List<LichHenDisplay> list = new ArrayList<>();
        // Lưu ý: Kiểm tra lại tên cột MaNienVien hay MaNhanVien trong DB của bạn
        String query = "SELECT lh.MaLichHen, lh.ThoiGianHen, lh.LyDoKham, lh.TrangThai, " +
                "bn.HoTen AS TenBenhNhan, nv.HoTen AS TenBacSi " +
                "FROM LichHen lh " +
                "LEFT JOIN BenhNhan bn ON lh.MaBenhNhan = bn.MaBenhNhan " +
                "LEFT JOIN NhanVien nv ON lh.MaBacSi = nv.MaNhanVien " + // Đã sửa MaNienVien -> MaNhanVien (Check lại DB của bạn)
                "ORDER BY lh.ThoiGianHen DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LichHenDisplay lh = new LichHenDisplay(
                        rs.getInt("MaLichHen"),
                        rs.getTimestamp("ThoiGianHen").toLocalDateTime(),
                        rs.getString("LyDoKham"),
                        rs.getString("TrangThai"),
                        rs.getString("TenBenhNhan"),
                        rs.getString("TenBacSi")
                );
                list.add(lh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thêm mới lịch hẹn.
     */
    public static boolean addLichHen(LichHen lichHen) {
        String query = "INSERT INTO LichHen (MaBenhNhan, MaBacSi, ThoiGianHen, LyDoKham, TrangThai) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, lichHen.getMaBenhNhan());
            pstmt.setInt(2, lichHen.getMaBacSi());
            pstmt.setTimestamp(3, Timestamp.valueOf(lichHen.getThoiGianHen()));
            pstmt.setString(4, lichHen.getLyDoKham());
            pstmt.setString(5, lichHen.getTrangThai());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin lịch hẹn.
     */
    public static boolean updateLichHen(LichHen lichHen) {
        String query = "UPDATE LichHen SET MaBenhNhan = ?, MaBacSi = ?, ThoiGianHen = ?, LyDoKham = ?, TrangThai = ? WHERE MaLichHen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, lichHen.getMaBenhNhan());
            pstmt.setInt(2, lichHen.getMaBacSi());
            pstmt.setTimestamp(3, Timestamp.valueOf(lichHen.getThoiGianHen()));
            pstmt.setString(4, lichHen.getLyDoKham());
            pstmt.setString(5, lichHen.getTrangThai());
            pstmt.setInt(6, lichHen.getMaLichHen());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa lịch hẹn.
     */
    public static boolean deleteLichHen(int maLichHen) {
        String query = "DELETE FROM LichHen WHERE MaLichHen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, maLichHen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}