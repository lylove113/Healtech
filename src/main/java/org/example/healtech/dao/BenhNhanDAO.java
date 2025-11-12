package org.example.healtech.dao;

import org.example.healtech.model.BenhNhan;
import org.example.healtech.util.DBConnection; // Giả sử tên file kết nối của bạn là DBConnection

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BenhNhanDAO {

    private Connection conn;

    public BenhNhanDAO() {
            this.conn = DBConnection.getConnection();
    }

    /**
     * Phương thức chính mà LichHenController cần
     * để lấp đầy ComboBox
     */
    public List<BenhNhan> getAllBenhNhan() {
        List<BenhNhan> list = new ArrayList<>();
        String query = "SELECT * FROM BenhNhan ORDER BY HoTen";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BenhNhan bn = new BenhNhan(
                        rs.getInt("MaBenhNhan"),
                        rs.getString("HoTen"),
                        rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null,
                        rs.getString("GioiTinh"),
                        rs.getString("DiaChi"),
                        rs.getString("SoDienThoai"),
                        rs.getString("TienSuBenh")
                );
                list.add(bn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage());
        }
        return list;
    }

    // (Bạn có thể thêm các hàm add, update, delete cho Module Quản lý Bệnh Nhân sau)
}