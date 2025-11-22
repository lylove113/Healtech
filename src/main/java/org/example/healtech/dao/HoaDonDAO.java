package org.example.healtech.dao;

import org.example.healtech.model.ChiTietDichVu;
import org.example.healtech.model.ChiTietThuoc;
import org.example.healtech.model.HoaDon;
import org.example.healtech.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    /**
     * 1. Lấy thông tin chi tiết thuốc
     */
    public List<ChiTietThuoc> getChiTietThuoc(int maPhieuKham) throws SQLException {
        List<ChiTietThuoc> danhSach = new ArrayList<>();

        String sql = "SELECT t.TenThuoc, t.DonViTinh, ctdt.SoLuong, t.GiaBan " +
                "FROM PhieuKhamBenh pkb " +
                "JOIN DonThuoc dt ON pkb.MaPhieuKham = dt.MaPhieuKham " +
                "JOIN ChiTietDonThuoc ctdt ON dt.MaDonThuoc = ctdt.MaDonThuoc " +
                "JOIN Thuoc t ON ctdt.MaThuoc = t.MaThuoc " +
                "WHERE pkb.MaPhieuKham = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maPhieuKham);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new ChiTietThuoc(
                            rs.getString("TenThuoc"),
                            rs.getString("DonViTinh"),
                            rs.getInt("SoLuong"),
                            rs.getDouble("GiaBan")
                    ));
                }
            }
        }
        return danhSach;
    }

    /**
     * 2. Lấy danh sách Chi Tiết Dịch Vụ (ĐÃ SỬA LOGIC SQL THẬT)
     * Giả định bạn có bảng: ChiTietSD_DichVu (hoặc ChiTietDichVu) và DichVu
     */
    /**
     * Lấy danh sách Chi Tiết Dịch Vụ (Đã cập nhật cho bảng DichVu & ChiTietDichVu mới)
     */
    public List<ChiTietDichVu> getChiTietDichVu(int maPhieuKham) throws SQLException {
        List<ChiTietDichVu> danhSach = new ArrayList<>();

        // Câu lệnh SQL này lấy tên dịch vụ và giá tiền thực tế đã lưu trong chi tiết
        // Nếu bạn chưa có cột ThanhTien trong ChiTietDichVu, hãy đổi 'ctdv.ThanhTien' thành 'dv.GiaDichVu'
        String sql = "SELECT dv.TenDichVu, ctdv.ThanhTien " +
                "FROM ChiTietDichVu ctdv " +
                "JOIN DichVu dv ON ctdv.MaDichVu = dv.MaDichVu " +
                "WHERE ctdv.MaPhieuKham = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maPhieuKham);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tenDV = rs.getString("TenDichVu");
                    double giaTien = rs.getDouble("ThanhTien");

                    // Thêm vào danh sách
                    danhSach.add(new ChiTietDichVu(tenDV, giaTien));
                }
            }
        } catch (SQLException e) {
            // Nếu bảng chưa tạo hoặc sai tên cột, nó sẽ báo lỗi rõ ràng ở đây
            System.err.println("Lỗi truy vấn bảng ChiTietDichVu: " + e.getMessage());
            // Không ném lỗi (throw) để chương trình vẫn chạy tiếp được phần thuốc
        }

        return danhSach;
    }

    /**
     * 3. Lấy Tên Bệnh Nhân
     */
    public String getTenBenhNhan(int maPhieuKham) throws SQLException {
        String sql = "SELECT bn.HoTen " +
                "FROM PhieuKhamBenh pkb " +
                "JOIN BenhNhan bn ON pkb.MaBenhNhan = bn.MaBenhNhan " +
                "WHERE pkb.MaPhieuKham = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maPhieuKham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("HoTen");
            }
        }
        return null;
    }

    /**
     * 4. Lấy Mã Bệnh Nhân
     */
    public int getMaBenhNhan(int maPhieuKham) throws SQLException {
        String sql = "SELECT MaBenhNhan FROM PhieuKhamBenh WHERE MaPhieuKham = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maPhieuKham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("MaBenhNhan");
            }
        }
        return -1;
    }

    /**
     * 5. Lưu Hóa Đơn
     */
    public boolean luuHoaDon(HoaDon hoaDon) throws SQLException {
        String sql = "INSERT INTO HoaDon (MaPhieuKham, MaBenhNhan, NgayThanhToan, TongTien, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, hoaDon.getMaPhieuKham());
            pstmt.setInt(2, hoaDon.getMaBenhNhan());

            // LƯU Ý QUAN TRỌNG: Xử lý ngày tháng
            if (hoaDon.getNgayThanhToan() != null) {
                // Nếu getNgayThanhToan() trả về LocalDateTime
                pstmt.setTimestamp(3, Timestamp.valueOf(hoaDon.getNgayThanhToan()));
            } else {
                // Nếu null thì lấy giờ hiện tại
                pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            }

            pstmt.setDouble(4, hoaDon.getTongTien());
            pstmt.setString(5, hoaDon.getTrangThai());

            return pstmt.executeUpdate() > 0;
        }
    }
}