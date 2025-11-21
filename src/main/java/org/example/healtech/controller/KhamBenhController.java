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

    // Biến cờ để kiểm soát việc Listener tự động đổi trạng thái
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

    // --- 3. TRÌNH LẮNG NGHE SỰ KIỆN (ĐÃ SỬA LỖI LOGIC) ---

    private void setupListeners() {
        waitingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {

            // A. Xử lý bệnh nhân cũ (vừa bị bỏ chọn)
            // CHỈ đổi về "Chờ khám" nếu họ chưa hoàn thành khám (isSaving = false)
            if (oldVal != null && !isSaving) {
                if (oldVal.getTrangThai().equals("Đang khám")) {
                    LichHenDAO.capNhatTrangThai(oldVal.getMaLichHen(), "Chờ khám");
                    oldVal.setTrangThai("Chờ khám");
                }
            }

            // B. Xử lý bệnh nhân mới được chọn
            currentPatient = newVal;
            if (newVal == null) {
                resetForm();
                return;
            }

            // Lấy thông tin dị ứng
            currentPatientAllergy = BenhNhanDAO.getThongTinDiUng(newVal.getMaBenhNhan());

            if (newVal.getTrangThai().equals("Đã khám")) {
                fillPatientData(newVal);
                examForm.setDisable(true);
                showAlert("Bệnh nhân này đã hoàn thành khám!", Alert.AlertType.INFORMATION);
            } else {
                // Tự động chuyển sang "Đang khám"
                boolean ok = LichHenDAO.capNhatTrangThai(newVal.getMaLichHen(), "Đang khám");
                if (ok) {
                    newVal.setTrangThai("Đang khám");
                    fillPatientData(newVal);
                    examForm.setDisable(false);
                }
            }
            waitingTable.refresh();
        });

        // Listener cho tìm kiếm và lọc
        if (txtSearch != null) txtSearch.onKeyReleasedProperty().set(e -> loadData());
        if (filterGroup != null) filterGroup.selectedToggleProperty().addListener((o, old, newT) -> { if(newT!=null) loadData(); });
    }

    // --- 4. XỬ LÝ NÚT BẤM (QUAN TRỌNG) ---

    @FXML
    private void handleLuuKetQua(ActionEvent event) {
        if (currentPatient == null) return;

        String trieuChung = txtTrieuChung.getText().trim();
        String chuanDoan = txtChuanDoan.getText().trim();

        if (trieuChung.isEmpty() || chuanDoan.isEmpty()) {
            showAlert("Vui lòng nhập triệu chứng và chẩn đoán!", Alert.AlertType.WARNING);
            return;
        }

        int maBacSi = 2; // ID Bác sĩ (có thể lấy từ Session đăng nhập)

        try {
            // 1. Tạo phiếu khám
            int maPhieuKham = PhieuKhamDAO.taoPhieuKham(
                    currentPatient.getMaLichHen(),
                    currentPatient.getMaBenhNhan(),
                    maBacSi,
                    trieuChung,
                    chuanDoan
            );

            if (maPhieuKham != -1) {
                // 2. Đánh dấu là ĐANG LƯU (để Listener không tự revert trạng thái)
                isSaving = true;
                currentPatient.setTrangThai("Đã khám"); // Cập nhật bộ nhớ ngay lập tức

                // 3. Cập nhật CSDL
                LichHenDAO.capNhatTrangThai(currentPatient.getMaLichHen(), "Đã khám");

                showAlert("Lưu thành công! Bệnh nhân đã được chuyển sang danh sách 'Đã khám'.", Alert.AlertType.INFORMATION);

                // 4. Tải lại bảng (Bệnh nhân sẽ biến mất khỏi danh sách Chờ)
                loadData();
                resetForm();

                // 5. Trả lại cờ
                isSaving = false;
            } else {
                showAlert("Lỗi: Không thể lưu (Có thể phiếu khám đã tồn tại).", Alert.AlertType.ERROR);
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
            // 1. Thử tạo phiếu khám (Logic: Lưu trước rồi mới kê đơn)
            int maPhieuKham = PhieuKhamDAO.taoPhieuKham(
                    currentPatient.getMaLichHen(),
                    currentPatient.getMaBenhNhan(),
                    maBacSi,
                    trieuChung,
                    chuanDoan
            );

            // Nếu tạo thất bại (do đã tồn tại), ta không coi là lỗi mà tiếp tục mở form kê đơn
            // (Có thể cần một hàm lấy ID phiếu khám nếu nó đã tồn tại, nhưng tạm thời ta bỏ qua ID phiếu khám trong luồng này nếu chỉ cần truyền object)

            // 2. Đánh dấu trạng thái "Đã khám"
            isSaving = true;
            currentPatient.setTrangThai("Đã khám");
            LichHenDAO.capNhatTrangThai(currentPatient.getMaLichHen(), "Đã khám");

            // 3. Mở màn hình kê đơn
            PhieuKham pk = new PhieuKham();
            pk.setMaPhieuKham(maPhieuKham); // Lưu ý: Nếu trùng, maPhieuKham = -1, cần xử lý bên KeDonThuocController
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

            // 4. Sau khi đóng form kê đơn -> Tải lại bảng chính
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

    // --- CÁC HÀM PHỤ ---

    private void loadData() {
        // Trước khi load, đảm bảo không trigger logic "revert status" trong Listener
        isSaving = true;

        String searchTerm = (txtSearch != null) ? txtSearch.getText() : "";
        String trangThaiFilter = "Tất cả";
        if (filterGroup != null && filterGroup.getSelectedToggle() != null) {
            trangThaiFilter = ((ToggleButton) filterGroup.getSelectedToggle()).getText();
        }

        ObservableList<LichHen> list = LichHenDAO.getDanhSachKhamHomNay(trangThaiFilter, searchTerm);

        // --- BỔ SUNG DEBUG LOG TẠI ĐÂY ---
        System.out.println("--- DEBUG DỮ LIỆU KHÁM BỆNH ---");
        System.out.println("Bộ lọc trạng thái đang sử dụng: " + trangThaiFilter);
        System.out.println("Từ khóa tìm kiếm: " + searchTerm);
        System.out.println("Số lượng bệnh nhân được tải từ DAO: " + list.size());
        // In ra mã và tên bệnh nhân đầu tiên (nếu có) để kiểm tra ngày giờ
        if (!list.isEmpty()) {
            LichHen firstPatient = list.get(0);
            System.out.println("Kiểm tra BN đầu tiên: MaLH=" + firstPatient.getMaLichHen() + ", Tên=" + firstPatient.getTenBenhNhan() + ", Trạng thái=" + firstPatient.getTrangThai());
        }
        System.out.println("---------------------------------");
        // ------------------------------------

        waitingTable.setItems(list);
        waitingTable.getSelectionModel().clearSelection();

        isSaving = false; // Load xong thì trả lại trạng thái bình thường
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