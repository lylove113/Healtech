package org.example.healtech.dao;

import org.example.healtech.model.NhanVien;
import org.example.healtech.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    private Connection conn;

    public NhanVienDAO() {
        this.conn = DBConnection.getConnection();
    }

    public List<NhanVien> getAllBacSi() {
        List<NhanVien> list = new ArrayList<>();
        String query = "SELECT * FROM NhanVien WHERE ChucVu = 'Bác sĩ' ORDER BY HoTen";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                        rs.getInt("MaNhanVien"),
                        rs.getString("HoTen"),
                        rs.getString("ChuyenKhoa"),
                        rs.getString("ChucVu"),
                        rs.getString("SoDienThoai"),
                        rs.getString("Email")
                );
                list.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi lấy danh sách bác sĩ: " + e.getMessage());
        }
        return list;
    }
}
