package org.example.healtech.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.healtech.dao.BenhNhanDAO;
import org.example.healtech.dao.LichHenDAO;
import org.example.healtech.dao.PhieuKhamDAO;
import org.example.healtech.model.LichHen;
import org.example.healtech.model.PhieuKham;
import org.example.healtech.model.DataHolder; // <--- 1. THÊM IMPORT NÀY

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class KhamBenhController implements Initializable {

    // --- 1. KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN (FXML) ---
    @FXML private TableView<LichHen> waitingTable;
    @FXML private TableColumn<LichHen, Integer> colMaLichHen;
    @FXML private TableColumn<LichHen, String> colTenBN;
    @FXML private TableColumn<LichHen, String> colGioiTinh;
    @FXML private TableColumn<LichHen, Integer> colNamSinh;
    @FXML private TableColumn<LichHen, String> colLyDo;
    @FXML private TableColumn<LichHen, String> colTrangThai;

    @FXML private VBox examForm;
    @FXML private TextField txtTenBenhNhan, txtMaBenhNhan;
    @FXML private TextArea txtTrieuChung, txtChuanDoan;
    @FXML private Button btnLuuKetQua;
    @FXML private Button btnKeDonThuoc;
    @FXML private Button btnHuy;

    @FXML private TextField txtSearch;
    @FXML private ToggleGroup filterGroup;
    @FXML private ToggleButton btnChoKham, btnDangKham, btnDaKham, btnTatCa;
    @FXML private Button btnLamMoi;

    private LichHen currentPatient;
    private String currentPatientAllergy = "Không rõ";
    private boolean isSaving = false;

    // --- 2. HÀM KHỞI TẠO ---

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        setupListeners();
        resetForm();
        if (btnChoKham != null) {
            btnChoKham.setSelected(true);
        }
        loadData();
    }

    private void setupTable() {
        colMaLichHen.setCellValueFactory(new PropertyValueFactory<>("maLichHen"));
        colTenBN.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioitinh"));
        colNamSinh.setCellValueFactory(new PropertyValueFactory<>("namSinh"));
        colLyDo.setCellValueFactory(new PropertyValueFactory<>("lyDoKham"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        txtMaBenhNhan.setEditable(false);
        txtTenBenhNhan.setEditable(false);
    }

    private void setupListeners() {
        waitingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null && !isSaving) {
                if (oldVal.getTrangThai().equals("Đang khám")) {
                    LichHenDAO.capNhatTrangThai(oldVal.getMaLichHen(), "Chờ khám");
                    oldVal.setTrangThai("Chờ khám");
                }
            }

            currentPatient = newVal;
            if (newVal == null) {
                resetForm();
                return;
            }

            currentPatientAllergy = BenhNhanDAO.getThongTinDiUng(newVal.getMaBenhNhan());

            if (newVal.getTrangThai().equals("Đã khám")) {
                fillPatientData(newVal);
                examForm.setDisable(true);
                showAlert("Bệnh nhân này đã hoàn thành khám!", Alert.AlertType.INFORMATION);
            } else {
                boolean ok = LichHenDAO.capNhatTrangThai(newVal.getMaLichHen(), "Đang khám");
                if (ok) {
                    newVal.setTrangThai("Đang khám");
                    fillPatientData(newVal);
                    examForm.setDisable(false);
                }
            }
            waitingTable.refresh();
        });

        if (txtSearch != null) txtSearch.onKeyReleasedProperty().set(e -> loadData());
        if (filterGroup != null) filterGroup.selectedToggleProperty().addListener((o, old, newT) -> { if(newT!=null) loadData(); });
    }

    // --- 4. XỬ LÝ NÚT BẤM (QUAN TRỌNG - ĐÃ SỬA) ---

    @FXML
    private void handleLuuKetQua(ActionEvent event) {
        if (currentPatient == null) return;

        String trieuChung = txtTrieuChung.getText().trim();
        String chuanDoan = txtChuanDoan.getText().trim();

        if (trieuChung.isEmpty() || chuanDoan.isEmpty()) {
            showAlert("Vui lòng nhập triệu chứng và chẩn đoán!", Alert.AlertType.WARNING);
            return;
        }

        int maBacSi = 2;

        try {
            int maPhieuKham = PhieuKhamDAO.taoPhieuKham(
                    currentPatient.getMaLichHen(),
                    currentPatient.getMaBenhNhan(),
                    maBacSi,
                    trieuChung,
                    chuanDoan
            );

            if (maPhieuKham != -1) {
                isSaving = true;

                // --- 2. GỬI DỮ LIỆU SANG TRANG THANH TOÁN ---
                DataHolder.maPhieuKhamThanhToan = maPhieuKham;
                System.out.println("Đã gửi mã phiếu " + maPhieuKham + " sang DataHolder.");
                // ---------------------------------------------

                currentPatient.setTrangThai("Đã khám");
                LichHenDAO.capNhatTrangThai(currentPatient.getMaLichHen(), "Đã khám");

                showAlert("Lưu thành công! Đã chuyển thông tin sang bộ phận thanh toán.", Alert.AlertType.INFORMATION);

                loadData();
                resetForm();
                isSaving = false;
            } else {
                showAlert("Lỗi: Không thể lưu phiếu khám.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi hệ thống: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleKeDonThuoc(ActionEvent event) {
        if (currentPatient == null) return;

        String trieuChung = txtTrieuChung.getText().trim();
        String chuanDoan = txtChuanDoan.getText().trim();

        if (trieuChung.isEmpty() || chuanDoan.isEmpty()) {
            showAlert("Nhập chẩn đoán trước khi kê đơn!", Alert.AlertType.WARNING);
            return;
        }

        int maBacSi = 2;

        try {
            int maPhieuKham = PhieuKhamDAO.taoPhieuKham(
                    currentPatient.getMaLichHen(),
                    currentPatient.getMaBenhNhan(),
                    maBacSi,
                    trieuChung,
                    chuanDoan
            );

            // --- 3. GỬI DỮ LIỆU SANG TRANG THANH TOÁN ---
            // Dù có kê đơn hay không, phiếu khám đã tạo xong thì gửi sang thanh toán luôn
            if (maPhieuKham != -1) {
                DataHolder.maPhieuKhamThanhToan = maPhieuKham;
                System.out.println("Đã gửi mã phiếu " + maPhieuKham + " sang DataHolder (Kê đơn).");
            }
            // ---------------------------------------------

            isSaving = true;
            currentPatient.setTrangThai("Đã khám");
            LichHenDAO.capNhatTrangThai(currentPatient.getMaLichHen(), "Đã khám");

            // Mở màn hình kê đơn
            PhieuKham pk = new PhieuKham();
            pk.setMaPhieuKham(maPhieuKham);
            pk.setMaBenhNhan(currentPatient.getMaBenhNhan());
            pk.setMaBacSi(maBacSi);
            pk.setChuanDoan(chuanDoan);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/healtech/view/KeDonThuocView.fxml"));
            Parent root = loader.load();
            KeDonThuocController controller = loader.getController();
            controller.initData(pk, currentPatient.getTenBenhNhan(), currentPatientAllergy);

            Stage stage = new Stage();
            stage.setTitle("Kê đơn: " + currentPatient.getTenBenhNhan());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
            resetForm();
            isSaving = false;

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi mở màn hình kê đơn!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        if (currentPatient != null && currentPatient.getTrangThai().equals("Đang khám")) {
            LichHenDAO.capNhatTrangThai(currentPatient.getMaLichHen(), "Chờ khám");
        }
        resetForm();
        loadData();
    }

    @FXML
    private void handleLamMoi(ActionEvent event) {
        loadData();
    }

    private void loadData() {
        isSaving = true;
        String searchTerm = (txtSearch != null) ? txtSearch.getText() : "";
        String trangThaiFilter = "Tất cả";
        if (filterGroup != null && filterGroup.getSelectedToggle() != null) {
            trangThaiFilter = ((ToggleButton) filterGroup.getSelectedToggle()).getText();
        }
        ObservableList<LichHen> list = LichHenDAO.getDanhSachKhamHomNay(trangThaiFilter, searchTerm);
        waitingTable.setItems(list);
        waitingTable.getSelectionModel().clearSelection();
        isSaving = false;
    }

    private void resetForm() {
        currentPatient = null;
        examForm.setDisable(true);
        txtMaBenhNhan.clear();
        txtTenBenhNhan.clear();
        txtTrieuChung.clear();
        txtChuanDoan.clear();
    }

    private void fillPatientData(LichHen patient) {
        txtMaBenhNhan.setText(String.valueOf(patient.getMaBenhNhan()));
        txtTenBenhNhan.setText(patient.getTenBenhNhan());
        txtTrieuChung.clear();
        txtChuanDoan.clear();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}