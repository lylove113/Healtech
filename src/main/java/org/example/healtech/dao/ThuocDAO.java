package org.example.healtech.dao;

import org.example.healtech.model.Thuoc;
import org.example.healtech.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý Thuốc - Đã được chuyển đổi hoàn toàn sang STATIC
 */
public class ThuocDAO {

    // --- LẤY TẤT CẢ THUỐC ---
    public static List<Thuoc> getAllThuoc() {
        List<Thuoc> list = new ArrayList<>();
        String query = "SELECT MaThuoc, TenThuoc, DonViTinh, SoLuongTon, GiaBan FROM Thuoc ORDER BY MaThuoc ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Thuoc thuoc = mapResultSetToThuoc(rs);
                list.add(thuoc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi lấy danh sách thuốc: " + e.getMessage());
        }
        return list;
    }

    // --- THÊM THUỐC MỚI ---
    public static boolean addThuoc(Thuoc thuoc) {
        String query = "INSERT INTO Thuoc (TenThuoc, DonViTinh, SoLuongTon, GiaBan) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, thuoc.getTenThuoc());
            pstmt.setString(2, thuoc.getDonViTinh());
            pstmt.setInt(3, thuoc.getSoLuongTon());
            pstmt.setBigDecimal(4, thuoc.getDonGia());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        thuoc.setMaThuoc(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi thêm thuốc: " + e.getMessage());
            return false;
        }
    }

    // --- CẬP NHẬT THÔNG TIN THUỐC ---
    public static boolean updateThuoc(Thuoc thuoc) {
        String query = "UPDATE Thuoc SET TenThuoc=?, DonViTinh=?, SoLuongTon=?, GiaBan=? WHERE MaThuoc=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, thuoc.getTenThuoc());
            pstmt.setString(2, thuoc.getDonViTinh());
            pstmt.setInt(3, thuoc.getSoLuongTon());
            pstmt.setBigDecimal(4, thuoc.getDonGia());
            pstmt.setInt(5, thuoc.getMaThuoc());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi cập nhật thuốc: " + e.getMessage());
            return false;
        }
    }

    // --- XÓA THUỐC ---
    public static boolean deleteThuoc(int maThuoc) {
        String query = "DELETE FROM Thuoc WHERE MaThuoc=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, maThuoc);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi xóa thuốc: " + e.getMessage());
            return false;
        }
    }

    // --- TÌM KIẾM THUỐC ---
    public static List<Thuoc> timKiemThuoc(String keyword) {
        List<Thuoc> list = new ArrayList<>();
        String query = "SELECT MaThuoc, TenThuoc, DonViTinh, SoLuongTon, GiaBan FROM Thuoc WHERE TenThuoc LIKE ? OR MaThuoc = ? ORDER BY MaThuoc ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + keyword + "%");

            // Kiểm tra nếu keyword là số thì tìm theo ID, nếu không thì gán -1 để bỏ qua
            try {
                pstmt.setInt(2, Integer.parseInt(keyword));
            } catch (NumberFormatException e) {
                pstmt.setInt(2, -1);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Thuoc thuoc = mapResultSetToThuoc(rs);
                    list.add(thuoc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi tìm kiếm thuốc: " + e.getMessage());
        }
        return list;
    }

    // --- CẬP NHẬT SỐ LƯỢNG TỒN (Đã sửa lỗi thiếu return) ---
    /**
     * Cập nhật số lượng tồn kho mới cho thuốc.
     * @param maThuoc ID thuốc cần sửa
     * @param soLuongMoi Số lượng tồn kho MỚI (đã tính toán xong)
     */
    public static boolean capNhatSoLuongTon(int maThuoc, int soLuongMoi) {
        String query = "UPDATE Thuoc SET SoLuongTon = ? WHERE MaThuoc = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, soLuongMoi);
            pstmt.setInt(2, maThuoc);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi cập nhật số lượng tồn: " + e.getMessage());
            return false;
        }
    }

    // --- HÀM PHỤ TRỢ: Map dữ liệu từ SQL sang Object ---
    private static Thuoc mapResultSetToThuoc(ResultSet rs) throws SQLException {
        Thuoc thuoc = new Thuoc();
        thuoc.setMaThuoc(rs.getInt("MaThuoc"));
        thuoc.setTenThuoc(rs.getString("TenThuoc"));
        thuoc.setDonViTinh(rs.getString("DonViTinh"));
        thuoc.setSoLuongTon(rs.getInt("SoLuongTon"));
        thuoc.setDonGia(rs.getBigDecimal("GiaBan"));

        // Gán ngày tạo là ngày hiện tại (vì DB có thể không lưu hoặc logic UI cần vậy)
        thuoc.setNgayTao(LocalDate.now());

        return thuoc;
    }
}