package org.example.healtech.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ✅ ĐÃ SỬA: Tên database chính xác theo hình bạn gửi là 'ql_phongkham'
    private static final String URL = "jdbc:mysql://localhost:3306/ql_phongkham";

    private static final String USER = "root"; // User mặc định của XAMPP
    private static final String PASS = "";     // Mật khẩu mặc định của XAMPP (để trống)

    public static Connection getConnection() {
        try {
            // Nạp Driver (giúp tránh lỗi ClassNotFoundException trên một số phiên bản Java cũ)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo kết nối
            return DriverManager.getConnection(URL, USER, PASS);

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Lỗi: Không tìm thấy thư viện MySQL JDBC Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối CSDL!");
            System.err.println("   -> URL: " + URL);
            System.err.println("   -> User: " + USER);
            System.err.println("   -> Kiểm tra xem XAMPP (MySQL) đã bật chưa?");
            e.printStackTrace();
        }
        return null;
    }
}