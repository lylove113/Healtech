package org.example.healtech.dao;

import javafx.collections.FXCollections; // Cần import
import javafx.collections.ObservableList; // Cần import
import org.example.healtech.model.PhieuKham; // Giả sử bạn có model PhieuKham
import org.example.healtech.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;

public class PhieuKhamDAO {

    // --- PHƯƠNG THỨC CŨ CỦA BẠN (Rất tốt, giữ nguyên) ---
    public static int taoPhieuKham(int maLichHen, int maBenhNhan, int maBacSi, String trieuChung, String chuanDoan) {
        // --- BƯỚC 1: KIỂM TRA XEM ĐÃ CÓ PHIẾU KHÁM NÀY CHƯA? ---
        String checkSql = "SELECT MaPhieuKham FROM PhieuKhamBenh WHERE MaLichHen = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, maLichHen);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // NẾU CÓ RỒI: Lấy ID cũ ra
                int idCu = rs.getInt("MaPhieuKham");
                System.out.println("DEBUG: Tìm thấy phiếu khám cũ (ID: " + idCu + "). Đang cập nhật thông tin mới...");

                // Cập nhật lại Triệu chứng & Chẩn đoán mới nhất (đề phòng bác sĩ sửa lại lời chẩn đoán)
                updatePhieuKham(conn, idCu, trieuChung, chuanDoan);

                return idCu; // Trả về ID cũ để dùng tiếp
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // --- BƯỚC 2: NẾU CHƯA CÓ THÌ MỚI INSERT (TẠO MỚI) ---
        String insertSql = "INSERT INTO PhieuKhamBenh (MaLichHen, MaBenhNhan, MaBacSi, NgayKham, TrieuChung, ChuanDoan) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, maLichHen);
            stmt.setInt(2, maBenhNhan);
            stmt.setInt(3, maBacSi);
            stmt.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            stmt.setString(5, trieuChung);
            stmt.setString(6, chuanDoan);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idMoi = rs.getInt(1);
                        System.out.println("DEBUG: Đã tạo phiếu khám MỚI (ID: " + idMoi + ")");
                        return idMoi;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi tạo phiếu khám: " + e.getMessage());
        }
        return -1; // Trả về -1 nếu lỗi
    }

    // --- HÀM PHỤ ĐỂ CẬP NHẬT (Copy thêm hàm này vào bên dưới hàm trên) ---
    private static void updatePhieuKham(Connection conn, int maPhieuKham, String trieuChung, String chuanDoan) {
        String sql = "UPDATE PhieuKhamBenh SET TrieuChung = ?, ChuanDoan = ? WHERE MaPhieuKham = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trieuChung);
            stmt.setString(2, chuanDoan);
            stmt.setInt(3, maPhieuKham);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- 1. BỔ SUNG: LẤY LỊCH SỬ KHÁM BỆNH ---
    /**
     * Lấy danh sách (tóm tắt) các lần khám trước đây của 1 bệnh nhân
     * Dùng để hiển thị trong 1 bảng/list Lịch sử khám
     * @param maBenhNhan Mã của bệnh nhân
     * @return Danh sách các phiếu khám cũ
     */
    public static ObservableList<PhieuKham> getLichSuKham(int maBenhNhan) {
        ObservableList<PhieuKham> lichSu = FXCollections.observableArrayList();
        // Lấy các thông tin tóm tắt để hiển thị list, sắp xếp mới nhất lên đầu
        String sql = "SELECT MaPhieuKham, NgayKham, ChuanDoan FROM PhieuKhamBenh " +
                "WHERE MaBenhNhan = ? ORDER BY NgayKham DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maBenhNhan);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Giả sử bạn có model PhieuKham và constructor phù hợp
                // Nếu không, bạn cần tạo constructor cho PhieuKham
                PhieuKham pk = new PhieuKham();
                pk.setMaPhieuKham(rs.getInt("MaPhieuKham"));
                pk.setNgayKham(rs.getDate("NgayKham").toLocalDate());
                pk.setChuanDoan(rs.getString("ChuanDoan"));
                lichSu.add(pk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lichSu;
    }

    // --- 2. BỔ SUNG: LẤY CHI TIẾT 1 PHIẾU KHÁM CŨ ---
    /**
     * Lấy chi tiết triệu chứng và chẩn đoán của 1 phiếu khám cụ thể
     * Dùng khi bác sĩ bấm vào 1 dòng trong list Lịch sử khám
     * @param maPhieuKham Mã phiếu khám
     * @return Đối tượng PhieuKham đầy đủ
     */
    public static PhieuKham getChiTietPhieuKham(int maPhieuKham) {
        String sql = "SELECT TrieuChung, ChuanDoan FROM PhieuKhamBenh WHERE MaPhieuKham = ?";
        PhieuKham pk = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maPhieuKham);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                pk = new PhieuKham();
                pk.setTrieuChung(rs.getString("TrieuChung"));
                pk.setChuanDoan(rs.getString("ChuanDoan"));
                // Bạn có thể lấy thêm các trường khác nếu cần
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pk; // Trả về null nếu không tìm thấy
    }
}