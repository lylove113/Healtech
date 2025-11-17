package org.example.healtech.dao;

import org.example.healtech.model.ChiTietDonThuoc;
import org.example.healtech.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChiTietDonThuocDAO {

    /**
     * Thêm một loại thuốc vào chi tiết đơn thuốc.
     * @param ct Đối tượng ChiTietDonThuoc chứa thông tin (maDonThuoc, maThuoc, soLuong...)
     * @return true nếu thêm thành công, false nếu lỗi
     */
    public static boolean themChiTiet(ChiTietDonThuoc ct) {
        // Tên cột trong CSDL của bạn có thể là "LieuDung"
        String sql = "INSERT INTO chitietdonthuoc (MaDonThuoc, MaThuoc, SoLuong, LieuDung) VALUES (?, ?, ?, ?)";
        // (Nếu bạn có thêm cột CachDung, hãy thêm vào đây)

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ct.getMaDonThuoc());
            stmt.setInt(2, ct.getMaThuoc());
            stmt.setInt(3, ct.getSoLuong());
            stmt.setString(4, ct.getLieuDung());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}