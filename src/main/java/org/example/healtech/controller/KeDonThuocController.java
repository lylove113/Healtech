package org.example.healtech.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.healtech.dao.ThuocDAO;
import org.example.healtech.model.PhieuKham;
import org.example.healtech.model.Thuoc;
import org.example.healtech.util.DBConnection;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class KeDonThuocController implements Initializable {

    // --- 1. KHAI BÁO GIAO DIỆN (Phải trùng fx:id bên SceneBuilder) ---
    @FXML private Label lblTenBenhNhan, lblMaBenhNhan, lblChuanDoan;

    // Bảng tìm kiếm thuốc (Bên trái)
    @FXML private TextField txtTimKiemThuoc;
    @FXML private TableView<Thuoc> tblTimKiemThuoc;
    @FXML private TableColumn<Thuoc, String> colTenThuoc;
    @FXML private TableColumn<Thuoc, String> colDonViTinh;
    @FXML private TableColumn<Thuoc, Double> colGiaBan;

    // Form nhập (Góc dưới trái)
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtLieuDung;
    @FXML private Button btnThemVaoDon;

    // Bảng đơn thuốc hiện tại (Bên phải)
    @FXML private TableView<ChiTietHienThi> tblDonThuocHienTai;
    @FXML private TableColumn<ChiTietHienThi, String> colThuocTrongDon;
    @FXML private TableColumn<ChiTietHienThi, Integer> colSoLuong;
    @FXML private TableColumn<ChiTietHienThi, String> colLieuDung;

    // Footer
    @FXML private TextArea txtLoiDan;
    @FXML private Button btnLuuDon;
    @FXML private Button btnHuyDon;

    // --- 2. BIẾN DỮ LIỆU ---
    private PhieuKham phieuKhamHienTai;
    private ObservableList<Thuoc> listThuocKho = FXCollections.observableArrayList();
    private ObservableList<ChiTietHienThi> listThuocKeDon = FXCollections.observableArrayList();

    // --- 3. KHỞI TẠO ---
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTables();

        // Load dữ liệu thuốc ngay khi mở
        listThuocKho.addAll(ThuocDAO.getAllThuoc());
        tblTimKiemThuoc.setItems(listThuocKho);

        // Tìm kiếm thuốc khi gõ
        txtTimKiemThuoc.textProperty().addListener((obs, oldVal, newVal) -> {
            timKiemThuoc(newVal);
        });
    }

    // Nhận dữ liệu từ màn hình Khám Bệnh
    public void initData(PhieuKham pk, String tenBN, String diUng) {
        this.phieuKhamHienTai = pk;
        lblTenBenhNhan.setText(tenBN);
        lblMaBenhNhan.setText("Mã BN: " + pk.getMaBenhNhan());
        // lblChuanDoan.setText(pk.getChuanDoan()); // Uncomment nếu có label này
    }

    private void setupTables() {
        // Cấu hình bảng trái (Kho thuốc)
        colTenThuoc.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        colDonViTinh.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        // Sửa lỗi hiển thị giá tiền (BigDecimal -> Double)
        colGiaBan.setCellValueFactory(cellData -> {
            BigDecimal val = cellData.getValue().getDonGia();
            return new SimpleDoubleProperty(val != null ? val.doubleValue() : 0).asObject();
        });

        // Cấu hình bảng phải (Đơn thuốc đang kê)
        colThuocTrongDon.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colLieuDung.setCellValueFactory(new PropertyValueFactory<>("lieuDung"));
        tblDonThuocHienTai.setItems(listThuocKeDon);
    }

    private void timKiemThuoc(String keyword) {
        listThuocKho.clear();
        if (keyword == null || keyword.isEmpty()) {
            listThuocKho.addAll(ThuocDAO.getAllThuoc());
        } else {
            listThuocKho.addAll(ThuocDAO.timKiemThuoc(keyword));
        }
    }

    // --- 4. XỬ LÝ NÚT THÊM (Sang bảng phải) ---
    @FXML
    private void handleThemVaoDon(ActionEvent event) {
        Thuoc thuocChon = tblTimKiemThuoc.getSelectionModel().getSelectedItem();
        if (thuocChon == null) {
            showAlert("Chưa chọn thuốc từ danh sách bên trái!");
            return;
        }

        try {
            int sl = Integer.parseInt(txtSoLuong.getText().trim());
            String lieuDung = txtLieuDung.getText().trim();

            if (sl <= 0) throw new NumberFormatException();
            if (lieuDung.isEmpty()) { showAlert("Vui lòng nhập liều dùng!"); return; }
            if (sl > thuocChon.getSoLuongTon()) { showAlert("Kho chỉ còn " + thuocChon.getSoLuongTon()); return; }

            // Thêm vào danh sách hiển thị bên phải
            listThuocKeDon.add(new ChiTietHienThi(thuocChon.getMaThuoc(), thuocChon.getTenThuoc(), sl, lieuDung));

            // Clear ô nhập
            txtSoLuong.clear();
            txtLieuDung.clear();

        } catch (NumberFormatException e) {
            showAlert("Số lượng phải là số nguyên dương!");
        }
    }

    // --- 5. XỬ LÝ NÚT LƯU (Lưu vào DB) ---
    @FXML
    private void handleLuuDon(ActionEvent event) {
        if (listThuocKeDon.isEmpty()) { showAlert("Đơn thuốc trống!"); return; }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            // 1. Tạo Đơn Thuốc (Header)
            String sqlDonThuoc = "INSERT INTO DonThuoc (MaPhieuKham, MaNhanVien, NgayKeDon, LoiDan) VALUES (?, ?, NOW(), ?)";            PreparedStatement stmtDon = conn.prepareStatement(sqlDonThuoc, Statement.RETURN_GENERATED_KEYS);
            stmtDon.setInt(1, phieuKhamHienTai.getMaPhieuKham());
            stmtDon.setInt(2, phieuKhamHienTai.getMaBacSi());
            stmtDon.setString(3, txtLoiDan.getText());

            int affected = stmtDon.executeUpdate();
            if (affected == 0) throw new SQLException("Lỗi tạo đơn thuốc");

            // Lấy ID đơn thuốc vừa tạo
            int maDonThuoc = -1;
            ResultSet rs = stmtDon.getGeneratedKeys();
            if (rs.next()) maDonThuoc = rs.getInt(1);

            // 2. Lưu chi tiết & Trừ kho
            String sqlChiTiet = "INSERT INTO ChiTietDonThuoc (MaDonThuoc, MaThuoc, SoLuong, LieuDung) VALUES (?, ?, ?, ?)";
            String sqlTruKho = "UPDATE Thuoc SET SoLuongTon = SoLuongTon - ? WHERE MaThuoc = ?";

            PreparedStatement stmtChiTiet = conn.prepareStatement(sqlChiTiet);
            PreparedStatement stmtKho = conn.prepareStatement(sqlTruKho);

            for (ChiTietHienThi item : listThuocKeDon) {
                // Insert chi tiết
                stmtChiTiet.setInt(1, maDonThuoc);
                stmtChiTiet.setInt(2, item.getMaThuoc());
                stmtChiTiet.setInt(3, item.getSoLuong());
                stmtChiTiet.setString(4, item.getLieuDung());
                stmtChiTiet.executeUpdate();

                // Trừ kho
                stmtKho.setInt(1, item.getSoLuong());
                stmtKho.setInt(2, item.getMaThuoc());
                stmtKho.executeUpdate();
            }

            showAlert("Lưu đơn thuốc thành công!");
            ((Stage) btnLuuDon.getScene().getWindow()).close(); // Đóng cửa sổ

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi CSDL: " + e.getMessage());
        }
    }

    @FXML
    private void handleHuyDon(ActionEvent event) {
        ((Stage) btnHuyDon.getScene().getWindow()).close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // --- CLASS NỘI BỘ ĐỂ HIỂN THỊ (Không cần tạo file mới) ---
    public static class ChiTietHienThi {
        private final int maThuoc;
        private final SimpleStringProperty tenThuoc;
        private final SimpleIntegerProperty soLuong;
        private final SimpleStringProperty lieuDung;

        public ChiTietHienThi(int maThuoc, String ten, int sl, String ld) {
            this.maThuoc = maThuoc;
            this.tenThuoc = new SimpleStringProperty(ten);
            this.soLuong = new SimpleIntegerProperty(sl);
            this.lieuDung = new SimpleStringProperty(ld);
        }

        public int getMaThuoc() { return maThuoc; }
        public String getTenThuoc() { return tenThuoc.get(); }
        public int getSoLuong() { return soLuong.get(); }
        public String getLieuDung() { return lieuDung.get(); }
    }
}