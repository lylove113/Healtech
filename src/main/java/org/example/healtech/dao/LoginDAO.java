package org.example.healtech.dao;

import org.example.healtech.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    public static boolean checkLogin(String email, String password) {
        String sql = "SELECT * FROM NhanVien WHERE Email = ? AND MatKhau = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Nếu có dòng trả về -> đúng tài khoản
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra đăng nhập: " + e.getMessage());
            return false;
        }
    }
}
