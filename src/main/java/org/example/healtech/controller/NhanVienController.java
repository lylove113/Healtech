

        package org.example.healtech.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.healtech.dao.NhanVienDAO;
import org.example.healtech.model.NhanVien;

import java.util.Optional;

public class NhanVienController {

    @FXML private TableView<NhanVien> nhanVienTableView;
    @FXML private TableColumn<NhanVien, Integer> colMaNV;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, String> colChuyenKhoa;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, String> colSoDienThoai;
    @FXML private TableColumn<NhanVien, String> colEmail;

    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtChuyenKhoa;
    @FXML private ComboBox<String> cmbChucVu;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtEmail;
    @FXML private TextField txtSearch;

    @FXML private ComboBox<String> cboChucVuFilter;

    private final NhanVienDAO dao = new NhanVienDAO();
    private final ObservableList<NhanVien> nhanVienList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {


        cboChucVuFilter.setItems(FXCollections.observableArrayList(
                "Bác sĩ", "Y tá", "Lễ tân", "Quản lý"
        ));


        cboChucVuFilter.valueProperty().addListener((obs, oldValue, newValue) -> {
            filterByChucVu(newValue);
        });


        colMaNV.setCellValueFactory(data -> data.getValue().maNhanVienProperty().asObject());
        colHoTen.setCellValueFactory(data -> data.getValue().hoTenProperty());
        colChuyenKhoa.setCellValueFactory(data -> data.getValue().chuyenKhoaProperty());
        colChucVu.setCellValueFactory(data -> data.getValue().chucVuProperty());
        colSoDienThoai.setCellValueFactory(data -> data.getValue().soDienThoaiProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());


        cmbChucVu.setItems(FXCollections.observableArrayList("Bác sĩ", "Y tá", "Lễ tân", "Quản lý"));

        loadNhanVien();

        nhanVienTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) setForm(newVal);
        });
    }

    private void loadNhanVien() {
        nhanVienList.setAll(dao.getAll());
        nhanVienTableView.setItems(nhanVienList);
    }


    private void filterByChucVu(String role) {
        if (role == null || role.isEmpty()) {
            nhanVienTableView.setItems(nhanVienList); // hiển thị mọi nhân viên
            return;
        }

        ObservableList<NhanVien> filtered = nhanVienList.filtered(
                nv -> nv.getChucVu().equalsIgnoreCase(role)
        );

        nhanVienTableView.setItems(filtered);
    }

    private void setForm(NhanVien nv) {
        txtMaNV.setText(String.valueOf(nv.getMaNhanVien()));
        txtHoTen.setText(nv.getHoTen());
        txtChuyenKhoa.setText(nv.getChuyenKhoa());
        cmbChucVu.setValue(nv.getChucVu());
        txtSoDienThoai.setText(nv.getSoDienThoai());
        txtEmail.setText(nv.getEmail());
    }

    private void clearForm() {
        txtMaNV.clear();
        txtHoTen.clear();
        txtChuyenKhoa.clear();
        cmbChucVu.setValue(null);
        txtSoDienThoai.clear();
        txtEmail.clear();
        nhanVienTableView.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        String phoneRegex = "\\d{9,11}";
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";

        if (txtHoTen.getText().isEmpty() || cmbChucVu.getValue() == null || txtSoDienThoai.getText().isEmpty()) {
            showAlert("Thiếu dữ liệu", "Vui lòng nhập đầy đủ các trường bắt buộc!", Alert.AlertType.WARNING);
            return false;
        }
        if (!txtSoDienThoai.getText().matches(phoneRegex)) {
            showAlert("Sai định dạng", "Số điện thoại không hợp lệ!", Alert.AlertType.WARNING);
            return false;
        }
        if (!txtEmail.getText().isEmpty() && !txtEmail.getText().matches(emailRegex)) {
            showAlert("Sai định dạng", "Email không hợp lệ!", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    private void handleAdd(ActionEvent e) {
        if (!validateForm()) return;

        NhanVien nv = new NhanVien(
                0,
                txtHoTen.getText(),
                txtChuyenKhoa.getText(),
                cmbChucVu.getValue(),
                txtSoDienThoai.getText(),
                txtEmail.getText(),
                "123456"
        );

        if (dao.insert(nv)) {
            loadNhanVien();
            clearForm();
            showAlert("Thành công", "Đã thêm nhân viên!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Không thể thêm nhân viên.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEdit(ActionEvent e) {
        NhanVien selected = nhanVienTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chưa chọn", "Hãy chọn nhân viên cần sửa!", Alert.AlertType.WARNING);
            return;
        }

        if (!validateForm()) return;

        selected.setHoTen(txtHoTen.getText());
        selected.setChuyenKhoa(txtChuyenKhoa.getText());
        selected.setChucVu(cmbChucVu.getValue());
        selected.setSoDienThoai(txtSoDienThoai.getText());
        selected.setEmail(txtEmail.getText());

        if (dao.update(selected)) {
            loadNhanVien();
            clearForm();
            showAlert("Thành công", "Đã cập nhật!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Lỗi", "Không thể cập nhật!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        NhanVien selected = nhanVienTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chưa chọn", "Hãy chọn nhân viên cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn chắc chắn muốn xóa?", ButtonType.OK, ButtonType.CANCEL);

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (dao.delete(selected.getMaNhanVien())) {
                loadNhanVien();
                clearForm();
                showAlert("Thành công", "Đã xóa!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Lỗi", "Không thể xóa!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSearch(ActionEvent e) {
        String keyword = txtSearch.getText();
        nhanVienList.setAll(dao.searchByName(keyword));
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
