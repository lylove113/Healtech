package org.example.healtech.dao;

import org.example.healtech.model.ChiTietDichVu; // <-- THÊM IMPORT MỚI
import org.example.healtech.model.ChiTietThuoc;
import org.example.healtech.model.HoaDon;
import org.example.healtech.util.DBConnection; // <-- Sử dụng lớp của bạn

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO (Data Access Object) cho các nghiệp vụ liên quan đến Hóa Đơn.
 * Sử dụng DBConnection để kết nối CSDL QL_PhongKham.
 */
public class HoaDonDAO {

    /**
     * Lấy thông tin chi tiết thuốc của một phiếu khám.
     * JOIN PhieuKhamBenh -> DonThuoc -> ChiTietDonThuoc -> Thuoc.
     */
    public List<ChiTietThuoc> getChiTietThuoc(int maPhieuKham) throws SQLException {
        List<ChiTietThuoc> danhSach = new ArrayList<>();

        String sql = "SELECT t.TenThuoc, t.DonViTinh, ctdt.SoLuong, t.GiaBan " +
                "FROM PhieuKhamBenh pkb " +
                "JOIN DonThuoc dt ON pkb.MaPhieuKham = dt.MaPhieuKham " +
                "JOIN ChiTietDonThuoc ctdt ON dt.MaDonThuoc = ctdt.MaDonThuoc " +
                "JOIN Thuoc t ON ctdt.MaThuoc = t.MaThuoc " +
                "WHERE pkb.MaPhieuKham = ?";

        // try-with-resources sẽ tự động đóng Connection và PreparedStatement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maPhieuKham);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tenThuoc = rs.getString("TenThuoc");
                    String donViTinh = rs.getString("DonViTinh");
                    int soLuong = rs.getInt("SoLuong");
                    double giaBan = rs.getDouble("GiaBan");

                    // Tạo đối tượng model ChiTietThuoc
                    danhSach.add(new ChiTietThuoc(tenThuoc, donViTinh, soLuong, giaBan));
                }
            }
        }
        return danhSach;
    }

    /**
     * Lấy Tên Bệnh Nhân dựa trên Mã Phiếu Khám.
     * JOIN PhieuKhamBenh -> BenhNhan.
     */
    public String getTenBenhNhan(int maPhieuKham) throws SQLException {
        String tenBenhNhan = null;
        String sql = "SELECT bn.HoTen " +
                "FROM PhieuKhamBenh pkb " +
                "JOIN BenhNhan bn ON pkb.MaBenhNhan = bn.MaBenhNhan " +
                "WHERE pkb.MaPhieuKham = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maPhieuKham);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tenBenhNhan = rs.getString("HoTen");
                }
            }
        }
        return tenBenhNhan;
    }

    /**
     * Lấy Mã Bệnh Nhân (MaBenhNhan) từ Mã Phiếu Khám.
     * Cần thiết để lưu Hóa Đơn.
     */
    public int getMaBenhNhan(int maPhieuKham) throws SQLException {
        int maBenhNhan = -1; // Trả về -1 nếu không tìm thấy
        String sql = "SELECT MaBenhNhan FROM PhieuKhamBenh WHERE MaPhieuKham = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maPhieuKham);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    maBenhNhan = rs.getInt("MaBenhNhan");
                }
            }
        }
        return maBenhNhan;
    }

    // --- PHƯƠNG THỨC MỚI ĐƯỢC THÊM ---

    /**
     * Lấy danh sách Chi Tiết Dịch Vụ từ Mã Phiếu Khám.
     * (Bao gồm Phí khám, Xét nghiệm, v.v.)
     */
    public List<ChiTietDichVu> getChiTietDichVu(int maPhieuKham) throws SQLException {
        List<ChiTietDichVu> danhSach = new ArrayList<>();

        // --- QUAN TRỌNG: BẠN PHẢI TỰ VIẾT SQL CỦA MÌNH Ở ĐÂY ---
        // Bạn cần truy vấn CSDL để lấy các dịch vụ liên quan đến maPhieuKham.
        //
        // Ví dụ 1: Lấy phí khám từ chính PhieuKhamBenh
        // String sqlPhiKham = "SELECT phiKham FROM PhieuKhamBenh WHERE MaPhieuKham = ?";
        // try (Connection conn = DBConnection.getConnection();
        //      PreparedStatement pstmt = conn.prepareStatement(sqlPhiKham)) {
        //     pstmt.setInt(1, maPhieuKham);
        //     try (ResultSet rs = pstmt.executeQuery()) {
        //         if (rs.next()) {
        //             danhSach.add(new ChiTietDichVu("Phí Khám Bệnh", rs.getDouble("phiKham")));
        //         }
        //     }
        // }
        //
        // Ví dụ 2: Lấy các dịch vụ khác (xét nghiệm, siêu âm) từ bảng chi tiết
        // String sqlDichVu = "SELECT dv.TenDichVu, ctdv.PhiDichVu " +
        //                  "FROM ChiTietDichVu ctdv " +
        //                  "JOIN DichVu dv ON ctdv.MaDichVu = dv.MaDichVu " +
        //                  "WHERE ctdv.MaPhieuKham = ?";
        // ... (thực thi tương tự và thêm vào danh sách) ...


        // --- VÍ DỤ TẠM THỜI (để code chạy được) ---
        // Xóa phần này khi bạn đã có SQL thật.
        // Dữ liệu tạm này sẽ thay thế cho 150.000 VND cố định
        if (maPhieuKham > 0) { // Chỉ là một kiểm tra đơn giản
            danhSach.add(new ChiTietDichVu("Phí Khám Bệnh", 150000.0));
            // danhSach.add(new ChiTietDichVu("Xét nghiệm máu", 200000.0)); // <-- Thêm nếu có
        }
        // --- HẾT PHẦN VÍ DỤ TẠM ---

        return danhSach;
    }


    /**
     * Lưu một đối tượng Hóa Đơn mới vào CSDL (Bảng HoaDon).
     */
    public boolean luuHoaDon(HoaDon hoaDon) throws SQLException {
        // Cần cập nhật trạng thái của Phiếu Khám Bệnh là 'Đã thanh toán'?
        // Tạm thời chỉ INSERT vào bảng HoaDon

        String sql = "INSERT INTO HoaDon (MaPhieuKham, MaBenhNhan, NgayThanhToan, TongTien, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, hoaDon.getMaPhieuKham());
            pstmt.setInt(2, hoaDon.getMaBenhNhan());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(hoaDon.getNgayThanhToan())); // Chuyển LocalDate -> sql.Date
            pstmt.setDouble(4, hoaDon.getTongTien());
            pstmt.setString(5, hoaDon.getTrangThai());

            int rowsAffected = pstmt.executeUpdate();

            // Trả về true nếu có 1 (hoặc nhiều) dòng được thêm
            return rowsAffected > 0;
        }
    }
}