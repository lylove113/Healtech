package org.example.healtech.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.healtech.dao.ThuocDAO;
import org.example.healtech.model.Thuoc;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
public class ThuocController {

    @FXML private TableView<Thuoc> tableThuoc;
    @FXML private TableColumn<Thuoc, Integer> colMaThuoc;
    @FXML private TableColumn<Thuoc, String> colTenThuoc;
    @FXML private TableColumn<Thuoc, String> colDonViTinh;
    @FXML private TableColumn<Thuoc, Integer> colSoLuongTon;
    @FXML private TableColumn<Thuoc, Double> colGiaBan;

    @FXML private TextField txtTenThuoc;
    @FXML private ComboBox<String> cbDonViTinh;
    @FXML private TextField txtSoLuongTon;
    @FXML private TextField txtGiaBan;
    @FXML private TextField txtTimKiem;

    private ThuocDAO thuocDAO;
    private Thuoc thuocDangChon;

    @FXML
    public void initialize() {
        try {
            System.out.println("🔄 Đang khởi tạo ThuocController...");

            // Kiểm tra các thành phần FXML
            System.out.println("=== KIỂM TRA FXML INJECTION ===");
            System.out.println("tableThuoc: " + (tableThuoc != null ? "OK" : "NULL"));
            System.out.println("colMaThuoc: " + (colMaThuoc != null ? "OK" : "NULL"));
            System.out.println("colTenThuoc: " + (colTenThuoc != null ? "OK" : "NULL"));
            System.out.println("colDonViTinh: " + (colDonViTinh != null ? "OK" : "NULL"));
            System.out.println("colSoLuongTon: " + (colSoLuongTon != null ? "OK" : "NULL"));
            System.out.println("colGiaBan: " + (colGiaBan != null ? "OK" : "NULL"));

            thuocDAO = new ThuocDAO();
            setupTableColumns();
            setupComboBox();
            loadThuocTable();

            tableThuoc.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> showThuocDetails(newValue)
            );

            System.out.println("✅ Khởi tạo ThuocController thành công!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo: " + e.getMessage());
            e.printStackTrace();
            showAlert("❌ Lỗi khởi tạo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupTableColumns() {
        try {
            System.out.println("🔄 Đang thiết lập table columns...");

            // KIỂM TRA NULL TRƯỚC KHI THIẾT LẬP
            if (colMaThuoc != null) {
                colMaThuoc.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getMaThuoc()).asObject());
            }

            if (colTenThuoc != null) {
                colTenThuoc.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTenThuoc()));
            }

            if (colDonViTinh != null) {
                colDonViTinh.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDonViTinh()));
            }

            if (colSoLuongTon != null) {
                colSoLuongTon.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getSoLuongTon()).asObject());
            }

            if (colGiaBan != null) {
                colGiaBan.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getDonGia().doubleValue()).asObject());
            }

            System.out.println("✅ Thiết lập table columns thành công!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi thiết lập table columns: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void setupComboBox() {
        if (cbDonViTinh != null) {
            cbDonViTinh.getItems().addAll("Viên", "Vỉ", "Hộp", "Chai", "Tuýp", "Ống", "Gói");
        }
    }

    private void loadThuocTable() {
        try {
            List<Thuoc> danhSach = thuocDAO.getAllThuoc();
            if (tableThuoc != null) {
                tableThuoc.setItems(FXCollections.observableArrayList(danhSach));
                System.out.println("✅ Đã tải " + danhSach.size() + " thuốc vào bảng");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi tải dữ liệu: " + e.getMessage());
            showAlert("❌ Lỗi tải dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // GIỮ NGUYÊN CÁC PHƯƠNG THỨC KHÁC...
    @FXML
    private void handleThem(ActionEvent event) {
        Optional<Thuoc> thuocOpt = getThuocFromForm();
        if (thuocOpt.isPresent()) {
            Thuoc thuoc = thuocOpt.get();
            boolean success = thuocDAO.addThuoc(thuoc);

            if (success) {
                showAlert("✅ Thêm thuốc thành công!", Alert.AlertType.INFORMATION);
                loadThuocTable();
                clearForm();
            } else {
                showAlert("❌ Không thể thêm thuốc!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCapNhat(ActionEvent event) {
        if (thuocDangChon == null) {
            showAlert("⚠️ Vui lòng chọn thuốc cần cập nhật!", Alert.AlertType.WARNING);
            return;
        }

        Optional<Thuoc> thuocOpt = getThuocFromForm();
        if (thuocOpt.isPresent()) {
            Thuoc thuocMoi = thuocOpt.get();
            thuocMoi.setMaThuoc(thuocDangChon.getMaThuoc());

            boolean success = thuocDAO.updateThuoc(thuocMoi);

            if (success) {
                showAlert("✅ Cập nhật thuốc thành công!", Alert.AlertType.INFORMATION);
                loadThuocTable();
            } else {
                showAlert("❌ Không thể cập nhật thuốc!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleXoa(ActionEvent event) {
        if (thuocDangChon == null) {
            showAlert("⚠️ Vui lòng chọn thuốc cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa thuốc: " + thuocDangChon.getTenThuoc() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = thuocDAO.deleteThuoc(thuocDangChon.getMaThuoc());
            if (success) {
                showAlert("✅ Xóa thuốc thành công!", Alert.AlertType.INFORMATION);
                loadThuocTable();
                clearForm();
            } else {
                showAlert("❌ Không thể xóa thuốc!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleTimKiem(ActionEvent event) {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadThuocTable();
            return;
        }

        try {
            List<Thuoc> ketQua = thuocDAO.timKiemThuoc(keyword);
            if (tableThuoc != null) {
                tableThuoc.setItems(FXCollections.observableArrayList(ketQua));
            }

            if (ketQua.isEmpty()) {
                showAlert("🔍 Không tìm thấy thuốc nào phù hợp!", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("❌ Lỗi khi tìm kiếm: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleTaiLai(ActionEvent event) {
        loadThuocTable();
        clearForm();
    }

    @FXML
    private void clearForm() {
        if (txtTenThuoc != null) txtTenThuoc.clear();
        if (cbDonViTinh != null) cbDonViTinh.setValue(null);
        if (txtSoLuongTon != null) txtSoLuongTon.clear();
        if (txtGiaBan != null) txtGiaBan.clear();
        if (txtTimKiem != null) txtTimKiem.clear();
        if (tableThuoc != null) tableThuoc.getSelectionModel().clearSelection();
        thuocDangChon = null;
    }

    private void showThuocDetails(Thuoc thuoc) {
        if (thuoc != null) {
            thuocDangChon = thuoc;
            if (txtTenThuoc != null) txtTenThuoc.setText(thuoc.getTenThuoc());
            if (cbDonViTinh != null) cbDonViTinh.setValue(thuoc.getDonViTinh());
            if (txtSoLuongTon != null) txtSoLuongTon.setText(String.valueOf(thuoc.getSoLuongTon()));
            if (txtGiaBan != null) txtGiaBan.setText(String.valueOf(thuoc.getDonGia()));
        }
    }

    private Optional<Thuoc> getThuocFromForm() {
        try {
            if (txtTenThuoc == null || cbDonViTinh == null || txtSoLuongTon == null || txtGiaBan == null) {
                showAlert("⚠️ Form không được khởi tạo đúng!", Alert.AlertType.ERROR);
                return Optional.empty();
            }

            String tenThuoc = txtTenThuoc.getText().trim();
            String donViTinh = cbDonViTinh.getValue();
            String soLuongText = txtSoLuongTon.getText().trim();
            String giaBanText = txtGiaBan.getText().trim();

            if (tenThuoc.isEmpty() || donViTinh == null || soLuongText.isEmpty() || giaBanText.isEmpty()) {
                showAlert("⚠️ Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.WARNING);
                return Optional.empty();
            }

            int soLuongTon;
            double giaBan;

            try {
                soLuongTon = Integer.parseInt(soLuongText);
                if (soLuongTon < 0) {
                    showAlert("⚠️ Số lượng tồn không thể âm!", Alert.AlertType.WARNING);
                    return Optional.empty();
                }
            } catch (NumberFormatException e) {
                showAlert("⚠️ Số lượng tồn phải là số nguyên!", Alert.AlertType.WARNING);
                return Optional.empty();
            }

            try {
                giaBan = Double.parseDouble(giaBanText);
                if (giaBan < 0) {
                    showAlert("⚠️ Giá bán không thể âm!", Alert.AlertType.WARNING);
                    return Optional.empty();
                }
            } catch (NumberFormatException e) {
                showAlert("⚠️ Giá bán phải là số!", Alert.AlertType.WARNING);
                return Optional.empty();
            }

            Thuoc thuoc = new Thuoc();
            thuoc.setTenThuoc(tenThuoc);
            thuoc.setDonViTinh(donViTinh);
            thuoc.setSoLuongTon(soLuongTon);
            thuoc.setDonGia(BigDecimal.valueOf(giaBan));
            return Optional.of(thuoc);

        } catch (Exception e) {
            showAlert("⚠️ Lỗi khi lấy dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
            return Optional.empty();
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}