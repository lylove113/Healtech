package org.example.healtech.controller;
import org.example.healtech.model.DataHolder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.healtech.dao.HoaDonDAO;
import org.example.healtech.model.ChiTietDichVu; // <-- THÊM IMPORT MỚI
import org.example.healtech.model.ChiTietThuoc;
import org.example.healtech.model.HoaDon;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ThanhToanController {

    // --- Các thành phần FXML (Của bạn) ---
    @FXML private TextField txtMaPhieuKham;
    @FXML private Label lblTenBenhNhan;
    @FXML private Label lblTongTienThuoc;
    @FXML private Label lblTongCong;
    @FXML private Button btnXacNhanThanhToan;

    // --- Bảng Thuốc (Của bạn) ---
    @FXML private TableView<ChiTietThuoc> chiTietThuocTable;
    @FXML private TableColumn<ChiTietThuoc, String> colTenThuoc;
    @FXML private TableColumn<ChiTietThuoc, String> colDonVi;
    @FXML private TableColumn<ChiTietThuoc, Integer> colSoLuong;
    @FXML private TableColumn<ChiTietThuoc, Double> colGiaBan;
    @FXML private TableColumn<ChiTietThuoc, Double> colThanhTien;

    // --- THÊM MỚI: Bảng Dịch Vụ ---
    @FXML private TableView<ChiTietDichVu> chiTietDichVuTable;
    @FXML private TableColumn<ChiTietDichVu, String> colTenDichVu;
    @FXML private TableColumn<ChiTietDichVu, Double> colPhiDichVu;
    @FXML private Label lblTongTienDichVu;
    // --- HẾT PHẦN THÊM MỚI ---

    // --- Các thành phần FXML cho Hình thức Thanh toán (Của bạn) ---
    @FXML private ToggleGroup paymentMethodGroup;
    @FXML private RadioButton radioTienMat;
    @FXML private RadioButton radioThe;
    @FXML private RadioButton radioChuyenKhoan;
    @FXML private HBox tienMatBox;
    @FXML private TextField txtTienKhachDua;
    @FXML private Label lblTienThoiLai;

    // --- Biến nội bộ (Của bạn) ---
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    // private final double PHI_KHAM = 150000; // <-- KHÔNG CẦN NỮA, SẼ LẤY TỪ DAO
    private final NumberFormat currencyFormatter;

    // --- Biến tạm (Của bạn) ---
    private int currentMaPhieuKham = -1;
    private int currentMaBenhNhan = -1;
    private double currentTongTien = 0;

    public ThanhToanController() {
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    /**
     * ĐÃ CẬP NHẬT: Thêm setup cho bảng dịch vụ
     */


    @FXML
    public void initialize() {
        // 1. Cấu hình bảng thuốc (Giữ nguyên)
        colTenThuoc.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        colDonVi.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colGiaBan.setCellValueFactory(new PropertyValueFactory<>("giaBan"));
        colThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        formatColumnCurrency(colGiaBan);
        formatColumnCurrency(colThanhTien);

        // 2. THÊM MỚI: Cấu hình bảng dịch vụ
        colTenDichVu.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        colPhiDichVu.setCellValueFactory(new PropertyValueFactory<>("phiDichVu"));
        formatColumnCurrency(colPhiDichVu);

        // 3. Đặt lại giao diện
        resetView();

        // 4. Cài đặt logic thanh toán
        setupPaymentLogic();

        // 5. --- QUAN TRỌNG: KIỂM TRA DỮ LIỆU TỪ TRANG KHÁC GỬI SANG ---
        checkDataTuTrangKhac();
    }

    /**
     * Hàm này tự động chạy khi màn hình mở lên.
     * Nó kiểm tra xem bác sĩ có vừa kê đơn và gửi mã phiếu sang không.
     */
    private void checkDataTuTrangKhac() {
        // Kiểm tra biến static trong DataHolder
        if (DataHolder.maPhieuKhamThanhToan != -1) {

            System.out.println("Nhận được mã phiếu từ trang khám: " + DataHolder.maPhieuKhamThanhToan);

            // 1. Tự động điền mã phiếu vào ô nhập liệu
            txtMaPhieuKham.setText(String.valueOf(DataHolder.maPhieuKhamThanhToan));

            // 2. Tự động gọi nút "Tính Tiền" để hiện danh sách thuốc/dịch vụ ngay lập tức
            handleTinhTien(null);

            // 3. Reset lại DataHolder về -1 để lần sau không bị lặp lại
            DataHolder.maPhieuKhamThanhToan = -1;
        }
    }

    /**
     * ĐÃ CẬP NHẬT: Thêm logic tải dịch vụ và tính lại tổng
     */
    @FXML
    void handleTinhTien(ActionEvent event) {
        String maPhieuKhamStr = txtMaPhieuKham.getText();
        if (maPhieuKhamStr == null || maPhieuKhamStr.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập Mã Phiếu Khám.");
            return;
        }

        try {
            int maPhieuKham = Integer.parseInt(maPhieuKhamStr);

            // 1. Lấy tên bệnh nhân (Giữ nguyên)
            String tenBenhNhan = hoaDonDAO.getTenBenhNhan(maPhieuKham);
            if (tenBenhNhan == null) {
                showAlert(Alert.AlertType.ERROR, "Không tìm thấy", "Không tìm thấy bệnh nhân cho Mã Phiếu Khám này.");
                resetView();
                return;
            }
            lblTenBenhNhan.setText(tenBenhNhan);

            // 2. Lấy chi tiết đơn thuốc (Giữ nguyên)
            List<ChiTietThuoc> danhSachThuoc = hoaDonDAO.getChiTietThuoc(maPhieuKham);
            chiTietThuocTable.setItems(FXCollections.observableArrayList(danhSachThuoc));

            // 3. THÊM MỚI: Lấy chi tiết dịch vụ
            List<ChiTietDichVu> danhSachDichVu = hoaDonDAO.getChiTietDichVu(maPhieuKham);
            chiTietDichVuTable.setItems(FXCollections.observableArrayList(danhSachDichVu));

            // 4. CẬP NHẬT: Tính toán và cập nhật tổng tiền
            double tongThuoc = danhSachThuoc.stream().mapToDouble(ChiTietThuoc::getThanhTien).sum();
            double tongDichVu = danhSachDichVu.stream().mapToDouble(ChiTietDichVu::getPhiDichVu).sum();
            double tongCong = tongThuoc + tongDichVu; // <-- Tổng mới

            lblTongTienThuoc.setText(formatCurrency(tongThuoc));
            lblTongTienDichVu.setText(formatCurrency(tongDichVu)); // <-- Cập nhật label mới
            lblTongCong.setText(formatCurrency(tongCong));

            // 5. Lưu lại thông tin (Giữ nguyên)
            this.currentMaPhieuKham = maPhieuKham;
            this.currentMaBenhNhan = hoaDonDAO.getMaBenhNhan(maPhieuKham);
            this.currentTongTien = tongCong;

            // 6. Kích hoạt nút thanh toán (Giữ nguyên)
            btnXacNhanThanhToan.setDisable(false);

            // 7. Tính lại tiền thối lại (Giữ nguyên)
            if (radioTienMat.isSelected()) {
                calculateChange();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Mã Phiếu Khám phải là một con số.");
            resetView();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi CSDL", "Không thể tải dữ liệu: " + e.getMessage());
            resetView();
        }
    }

    /**
     * ĐÃ CẬP NHẬT: Thêm logic reset cho các trường dịch vụ
     */
    private void resetView() {
        // Code gốc của bạn
        txtMaPhieuKham.clear();
        lblTenBenhNhan.setText("[Tên Bệnh Nhân]");
        chiTietThuocTable.setItems(FXCollections.observableArrayList());
        lblTongTienThuoc.setText(formatCurrency(0));

        // --- CẬP NHẬT ---
        chiTietDichVuTable.setItems(FXCollections.observableArrayList()); // <-- MỚI
        lblTongTienDichVu.setText(formatCurrency(0)); // <-- MỚI
        lblTongCong.setText(formatCurrency(0)); // <-- THAY ĐỔI (từ PHI_KHAM về 0)
        // --- HẾT CẬP NHẬT ---

        currentMaPhieuKham = -1;
        currentMaBenhNhan = -1;
        currentTongTien = 0;

        if (btnXacNhanThanhToan != null) {
            btnXacNhanThanhToan.setDisable(true);
            btnXacNhanThanhToan.setText("XÁC NHẬN THANH TOÁN");
        }

        if (paymentMethodGroup != null) {
            radioTienMat.setSelected(true);
        }
        if (txtTienKhachDua != null) {
            txtTienKhachDua.clear();
        }
        if (lblTienThoiLai != null) {
            lblTienThoiLai.setText(formatCurrency(0));
        }
    }

    // ===================================================================
    // CÁC HÀM CÒN LẠI GIỮ NGUYÊN NHƯ TRƯỚC
    // (handleXacNhanThanhToan, formatCurrency, showAlert,
    // formatColumnCurrency, setupPaymentLogic, calculateChange,
    // getSelectedPaymentMethod)
    // ===================================================================
    /**
     * Sự kiện: Khi rê chuột vào màn hình thanh toán,
     * tự động kiểm tra xem có dữ liệu gửi từ trang khám bệnh sang không.
     */
    @FXML
    public void handleMouseEnter(javafx.scene.input.MouseEvent event) {
        checkDataTuTrangKhac();
    }

    // ... existing code ...
    @FXML
    void handleXacNhanThanhToan(ActionEvent event) {
        // ... existing code ...
        // (Các đoạn kiểm tra tiền khách đưa giữ nguyên)

        btnXacNhanThanhToan.setDisable(true);
        btnXacNhanThanhToan.setText("Đang xử lý...");

        try {
            // 1. Tạo hóa đơn (Constructor này chỉ set NgayTao)
            HoaDon hoaDon = new HoaDon(
                    currentMaPhieuKham,
                    currentMaBenhNhan,
                    LocalDate.now(),
                    currentTongTien,
                    "Đã thanh toán"
            );

            // ✅ QUAN TRỌNG: THÊM DÒNG NÀY ĐỂ SỬA LỖI
            // Thiết lập thời gian thanh toán là hiện tại (đầy đủ ngày giờ)
            hoaDon.setNgayThanhToan(java.time.LocalDateTime.now());

            boolean luuThanhCong = hoaDonDAO.luuHoaDon(hoaDon);

            if (luuThanhCong) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thanh toán thành công và đã lưu hóa đơn.");
                resetView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Lỗi khi lưu hóa đơn.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi CSDL", "Không thể lưu hóa đơn: " + e.getMessage());
        } finally {
            if (!btnXacNhanThanhToan.isDisabled()) {
                btnXacNhanThanhToan.setText("XÁC NHẬN THANH TOÁN");
            }
        }
    }
// ... existing code ...

    private String formatCurrency(double amount) {
        return currencyFormatter.format(amount).replace("₫", "VND").replaceAll("\\s+", " ").trim();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * HÀM HỖ TRỢ MỚI: Tái sử dụng cho cả 2 bảng
     * (Cần ép kiểu chung)
     */
    private <T> void formatColumnCurrency(TableColumn<T, Double> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(formatCurrency(price));
                }
            }
        });
    }

    private void setupPaymentLogic() {
        tienMatBox.managedProperty().bind(radioTienMat.selectedProperty());
        tienMatBox.visibleProperty().bind(radioTienMat.selectedProperty());

        txtTienKhachDua.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateChange();
        });

        paymentMethodGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != radioTienMat) {
                txtTienKhachDua.clear();
                lblTienThoiLai.setText(formatCurrency(0));
            }
        });
    }

    private void calculateChange() {
        try {
            String tienKhachDuaText = txtTienKhachDua.getText().replace(".", "").replace(",", "");
            if (tienKhachDuaText.isEmpty()) {
                lblTienThoiLai.setText(formatCurrency(0));
                return;
            }

            double tienKhachDua = Double.parseDouble(tienKhachDuaText);
            double tienThoiLai = tienKhachDua - this.currentTongTien;

            if (tienThoiLai < 0) {
                lblTienThoiLai.setText("Chưa đủ");
                lblTienThoiLai.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                lblTienThoiLai.setText(formatCurrency(tienThoiLai));
                lblTienThoiLai.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
            }
        } catch (NumberFormatException e) {
            lblTienThoiLai.setText("Không hợp lệ");
            lblTienThoiLai.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }

    private String getSelectedPaymentMethod() {
        RadioButton selectedRadio = (RadioButton) paymentMethodGroup.getSelectedToggle();
        if (selectedRadio != null) {
            return selectedRadio.getText();
        }
        return null;
    }
}