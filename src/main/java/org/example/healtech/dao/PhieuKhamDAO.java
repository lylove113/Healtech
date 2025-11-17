package org.example.healtech.dao;

import javafx.collections.FXCollections; // Cần import
import javafx.collections.ObservableList; // Cần import
import org.example.healtech.model.PhieuKham; // Giả sử bạn có model PhieuKham
import org.example.healtech.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;

public class PhieuKhamDAO {

    // --- PHƯƠNG THỨC CŨ CỦA BẠN (Rất tốt, giữ nguyên) ---
    public static int taoPhieuKham(int maLichHen, int maBenhNhan, int maBacSi, String trieuChung, String chuanDoan) {
        String sql = "INSERT INTO PhieuKhamBenh (MaLichHen, MaBenhNhan, MaBacSi, NgayKham, TrieuChung, ChuanDoan) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, maLichHen);
            stmt.setInt(2, maBenhNhan);
            stmt.setInt(3, maBacSi);
            stmt.setDate(4, Date.valueOf(LocalDate.now()));
            stmt.setString(5, trieuChung);
            stmt.setString(6, chuanDoan);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về MaPhieuKham
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Lỗi
    }

    // --- 1. BỔ SUNG: LẤY LỊCH SỬ KHÁM BỆNH ---
    /**
     * Lấy danh sách (tóm tắt) các lần khám trước đây của 1 bệnh nhân
     * Dùng để hiển thị trong 1 bảng/list Lịch sử khám
     * @param maBenhNhan Mã của bệnh nhân
     * @return Danh sách các phiếu khám cũ
     */
    public static ObservableList<PhieuKham> getLichSuKham(int maBenhNhan) {
        ObservableList<PhieuKham> lichSu = FXCollections.observableArrayList();
        // Lấy các thông tin tóm tắt để hiển thị list, sắp xếp mới nhất lên đầu
        String sql = "SELECT MaPhieuKham, NgayKham, ChuanDoan FROM PhieuKhamBenh " +
                "WHERE MaBenhNhan = ? ORDER BY NgayKham DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maBenhNhan);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Giả sử bạn có model PhieuKham và constructor phù hợp
                // Nếu không, bạn cần tạo constructor cho PhieuKham
                PhieuKham pk = new PhieuKham();
                pk.setMaPhieuKham(rs.getInt("MaPhieuKham"));
                pk.setNgayKham(rs.getDate("NgayKham").toLocalDate());
                pk.setChuanDoan(rs.getString("ChuanDoan"));
                lichSu.add(pk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lichSu;
    }

    // --- 2. BỔ SUNG: LẤY CHI TIẾT 1 PHIẾU KHÁM CŨ ---
    /**
     * Lấy chi tiết triệu chứng và chẩn đoán của 1 phiếu khám cụ thể
     * Dùng khi bác sĩ bấm vào 1 dòng trong list Lịch sử khám
     * @param maPhieuKham Mã phiếu khám
     * @return Đối tượng PhieuKham đầy đủ
     */
    public static PhieuKham getChiTietPhieuKham(int maPhieuKham) {
        String sql = "SELECT TrieuChung, ChuanDoan FROM PhieuKhamBenh WHERE MaPhieuKham = ?";
        PhieuKham pk = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maPhieuKham);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                pk = new PhieuKham();
                pk.setTrieuChung(rs.getString("TrieuChung"));
                pk.setChuanDoan(rs.getString("ChuanDoan"));
                // Bạn có thể lấy thêm các trường khác nếu cần
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pk; // Trả về null nếu không tìm thấy
    }
}