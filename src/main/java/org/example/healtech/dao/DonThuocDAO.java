package org.example.healtech.dao;

import org.example.healtech.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class DonThuocDAO {

    /**
     * Tạo một đơn thuốc mới và trả về MaDonThuoc vừa tạo.
     * @param maPhieuKham Mã phiếu khám liên kết
     * @param maBacSi Mã bác sĩ kê đơn
     * @param loiDan Lời dặn của bác sĩ
     * @return MaDonThuoc mới (hoặc -1 nếu lỗi)
     */
    public static int taoDonThuoc(int maPhieuKham, int maBacSi, String loiDan) {
        String sql = "INSERT INTO donthuoc (MaPhieuKham, MaBacSi, NgayKeDon, LoiDan) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, maPhieuKham);
            stmt.setInt(2, maBacSi);
            stmt.setDate(3, Date.valueOf(LocalDate.now())); // Ngày hiện tại
            stmt.setString(4, loiDan);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về MaDonThuoc vừa tạo
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Lỗi
    }
}