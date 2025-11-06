package org.example.healtech.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane mainContent;

    // 🩺 Trang Dashboard mặc định
    @FXML
    private void showDashboard() {
        loadView("/org/example/healtech/view/DashboardHomeView.fxml");
    }

    @FXML
    private void showBenhNhan() {
        loadView("/org/example/healtech/view/BenhNhanView.fxml");
    }

    @FXML
    private void showLichHen() {
        loadView("/org/example/healtech/view/LichHenView.fxml");
    }

    @FXML
    private void showBacSi() {
        loadView("/org/example/healtech/view/NhanVienView.fxml");
    }

    @FXML
    private void showKhamBenh() {
        loadView("/org/example/healtech/view/KhamBenhView.fxml");
    }

    @FXML
    private void showThuoc() {
        loadView("/org/example/healtech/view/ThuocView.fxml");
    }

    @FXML
    private void showThanhToan() {
        loadView("/org/example/healtech/view/ThanhToanView.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) mainContent.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/healtech/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 📂 Hàm dùng chung để nạp FXML vào phần trung tâm
    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
