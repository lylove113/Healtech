package org.example.healtech.dao;

import org.example.healtech.model.BenhNhan;
import org.example.healtech.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý Bệnh Nhân (Đã gộp và chuyển sang static)
 */
public class BenhNhanDAO {

    // ✅ BỔ SUNG TỪ ĐOẠN 1 (Dùng cho KhamBenhController)
    /**
     * Lấy thông tin Tiền sử bệnh/Dị ứng của bệnh nhân
     * @param maBenhNhan Mã bệnh nhân
     * @return Chuỗi String dị ứng, hoặc "Không có"
     */
    public static String getThongTinDiUng(int maBenhNhan) {
        // Tên cột của bạn là "TienSuBenh"
        String sql = "SELECT TienSuBenh FROM benhnhan WHERE MaBenhNhan = ?";

        // ✅ Luôn dùng try-with-resources cho cả Connection và PreparedStatement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maBenhNhan);

            // ✅ ResultSet cũng nên nằm trong try-with-resources
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String diUng = rs.getString("TienSuBenh");
                    return (diUng == null || diUng.trim().isEmpty()) ? "Không có" : diUng;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không rõ"; // Trả về nếu có lỗi
    }

    // ===== CÁC PHƯƠNG THỨC TỪ ĐOẠN 2 (Đã sửa sang static) =====

    /**
     * ✅ Đã sửa: Thêm 'static' và 'try-with-resources'
     */
    public static List<BenhNhan> getAllBenhNhan() {
        List<BenhNhan> list = new ArrayList<>();
        String query = "SELECT * FROM BenhNhan ORDER BY MaBenhNhan ASC";

        // ✅ Sửa: Thêm Connection vào try-with-resources
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BenhNhan bn = mapResultSetToBenhNhan(rs);
                list.add(bn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi lấy danh sách bệnh nhân: " + e.getMessage());
        }
        return list;
    }

    /**
     * ✅ Đã sửa: Thêm 'static' và 'try-with-resources'
     */
    public static boolean addBenhNhan(BenhNhan benhNhan) {
        String query = "INSERT INTO BenhNhan (HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, TienSuBenh) VALUES (?, ?, ?, ?, ?, ?)";

        // ✅ Sửa: Thêm Connection vào try-with-resources
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, benhNhan.getHoTen());
            pstmt.setDate(2, Date.valueOf(benhNhan.getNgaySinh()));
            pstmt.setString(3, benhNhan.getGioiTinh());
            pstmt.setString(4, benhNhan.getDiaChi());
            pstmt.setString(5, benhNhan.getSoDienThoai());
            pstmt.setString(6, benhNhan.getTienSuBenh());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        benhNhan.setMaBenhNhan(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi thêm bệnh nhân: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Đã sửa: Thêm 'static' và 'try-with-resources'
     */
    public static boolean updateBenhNhan(BenhNhan benhNhan) {
        String query = "UPDATE BenhNhan SET HoTen=?, NgaySinh=?, GioiTinh=?, DiaChi=?, SoDienThoai=?, TienSuBenh=? WHERE MaBenhNhan=?";

        // ✅ Sửa: Thêm Connection vào try-with-resources
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, benhNhan.getHoTen());
            pstmt.setDate(2, Date.valueOf(benhNhan.getNgaySinh()));
            pstmt.setString(3, benhNhan.getGioiTinh());
            pstmt.setString(4, benhNhan.getDiaChi());
            pstmt.setString(5, benhNhan.getSoDienThoai());
            pstmt.setString(6, benhNhan.getTienSuBenh());
            pstmt.setInt(7, benhNhan.getMaBenhNhan());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi cập nhật bệnh nhân: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Đã sửa: Thêm 'static' và 'try-with-resources'
     */
    public static boolean deleteBenhNhan(int maBenhNhan) {
        String query = "DELETE FROM BenhNhan WHERE MaBenhNhan=?";

        // ✅ Sửa: Thêm Connection vào try-with-resources
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, maBenhNhan);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi xóa bệnh nhân: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Đã sửa: Thêm 'static' và 'try-with-resources'
     */
    public static List<BenhNhan> timKiemBenhNhan(String keyword) {
        List<BenhNhan> list = new ArrayList<>();
        String query = "SELECT * FROM BenhNhan WHERE HoTen LIKE ? OR SoDienThoai LIKE ? OR MaBenhNhan = ? ORDER BY MaBenhNhan ASC";

        // ✅ Sửa: Thêm Connection vào try-with-resources
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            try {
                pstmt.setInt(3, Integer.parseInt(keyword));
            } catch (NumberFormatException e) {
                pstmt.setInt(3, -1);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BenhNhan bn = mapResultSetToBenhNhan(rs);
                    list.add(bn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi tìm kiếm bệnh nhân: " + e.getMessage());
        }
        return list;
    }

    /**
     * ✅ Đã sửa: Thêm 'static'
     */
    private static BenhNhan mapResultSetToBenhNhan(ResultSet rs) throws SQLException {
        BenhNhan bn = new BenhNhan();
        bn.setMaBenhNhan(rs.getInt("MaBenhNhan"));
        bn.setHoTen(rs.getString("HoTen"));

        Date ngaySinh = rs.getDate("NgaySinh");
        if (ngaySinh != null) {
            bn.setNgaySinh(ngaySinh.toLocalDate());
        }

        bn.setGioiTinh(rs.getString("GioiTinh"));
        bn.setDiaChi(rs.getString("DiaChi"));
        bn.setSoDienThoai(rs.getString("SoDienThoai"));
        bn.setTienSuBenh(rs.getString("TienSuBenh"));

        // Kiểm tra xem cột NgayTao có tồn tại không
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            boolean hasNgayTao = false;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if ("NgayTao".equalsIgnoreCase(metaData.getColumnName(i))) {
                    hasNgayTao = true;
                    break;
                }
            }

            if (hasNgayTao) {
                Date ngayTao = rs.getDate("NgayTao");
                if (ngayTao != null) {
                    bn.setNgayTao(ngayTao.toLocalDate());
                }
            }
        } catch (SQLException e) {
            // Bỏ qua nếu cột không tồn tại, ví dụ: "Column 'NgayTao' not found."
            System.out.println("Lưu ý: Không tìm thấy cột 'NgayTao', bỏ qua việc map.");
        }

        return bn;
    }
}