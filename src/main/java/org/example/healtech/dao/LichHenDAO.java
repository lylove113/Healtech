package org.example.healtech.dao;

import org.example.healtech.model.LichHen;
import org.example.healtech.model.LichHenDisplay; // <- (Bạn cần tạo file này như tôi đã gửi trước đó)
import org.example.healtech.util.DBConnection; // <- (Giả sử file util của bạn tên là DBConnection)

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LichHenDAO {

    // Bỏ 'static' và dùng một biến connection cho đối tượng
    private Connection conn;

    // Constructor để khởi tạo connection
    public LichHenDAO() {
            this.conn = DBConnection.getConnection();
    }

    /**
     * PHƯƠNG THỨC QUAN TRỌNG NHẤT
     * Lấy dữ liệu JOIN để hiển thị lên TableView (gồm Tên BN, Tên BS)
     */
    public List<LichHenDisplay> getLichHenForView() {
        List<LichHenDisplay> list = new ArrayList<>();
        // Câu lệnh JOIN
        String query = "SELECT lh.MaLichHen, lh.ThoiGianHen, lh.LyDoKham, lh.TrangThai, " +
                "bn.HoTen AS TenBenhNhan, nv.HoTen AS TenBacSi " +
                "FROM LichHen lh " +
                "LEFT JOIN BenhNhan bn ON lh.MaBenhNhan = bn.MaBenhNhan " +
                "LEFT JOIN NhanVien nv ON lh.MaBacSi = nv.MaNhanVien " +
                "ORDER BY lh.ThoiGianHen DESC";

        // Không dùng try-with-resources cho 'conn' ở đây
        try (Statement stmt = conn.createStatement();
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
            System.err.println("❌ Lỗi khi lấy danh sách lịch hẹn (JOIN): " + e.getMessage());
        }
        return list;
    }

    // Thêm mới lịch hẹn (dùng model LichHen cơ bản)
    public boolean addLichHen(LichHen lichHen) {
        String query = "INSERT INTO LichHen (MaBenhNhan, MaBacSi, ThoiGianHen, LyDoKham, TrangThai) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, lichHen.getMaBenhNhan());
            pstmt.setInt(2, lichHen.getMaBacSi());
            pstmt.setTimestamp(3, Timestamp.valueOf(lichHen.getThoiGianHen()));
            pstmt.setString(4, lichHen.getLyDoKham());
            pstmt.setString(5, lichHen.getTrangThai());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi thêm lịch hẹn: " + e.getMessage());
            return false;
        }
    }

    /**
     * Phương thức update đầy đủ (Thay thế cho updateTrangThai)
     */
    public boolean updateLichHen(LichHen lichHen) {
        String query = "UPDATE LichHen SET MaBenhNhan = ?, MaBacSi = ?, ThoiGianHen = ?, LyDoKham = ?, TrangThai = ? WHERE MaLichHen = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, lichHen.getMaBenhNhan());
            pstmt.setInt(2, lichHen.getMaBacSi());
            pstmt.setTimestamp(3, Timestamp.valueOf(lichHen.getThoiGianHen()));
            pstmt.setString(4, lichHen.getLyDoKham());
            pstmt.setString(5, lichHen.getTrangThai());
            pstmt.setInt(6, lichHen.getMaLichHen()); // MaLichHen ở cuối cho mệnh đề WHERE

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi cập nhật lịch hẹn: " + e.getMessage());
            return false;
        }
    }

    // Xóa lịch hẹn
    public boolean deleteLichHen(int maLichHen) {
        String query = "DELETE FROM LichHen WHERE MaLichHen = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, maLichHen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi xóa lịch hẹn: " + e.getMessage());
            return false;
        }
    }
}