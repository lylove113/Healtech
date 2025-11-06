package org.example.healtech.dao;

import org.example.healtech.model.NhanVien;
import org.example.healtech.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterDAO {

    public boolean registerUser(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (HoTen, Email, MatKhau, ChucVu, ChuyenKhoa, SoDienThoai) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nv.getHoTen());
            stmt.setString(2, nv.getEmail());
            stmt.setString(3, nv.getMatKhau());
            stmt.setString(4, nv.getChucVu());
            stmt.setString(5, nv.getChuyenKhoa());
            stmt.setString(6, nv.getSoDienThoai());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đăng ký: " + e.getMessage());
            return false;
        }
    }
}
