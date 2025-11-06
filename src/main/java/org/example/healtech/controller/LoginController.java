package org.example.healtech.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.healtech.dao.LoginDAO;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi đăng nhập", "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        boolean isValid = LoginDAO.checkLogin(email, password);

        if (isValid) {
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công", "Chào mừng bạn!");
            loadScene("/org/example/healtech/view/DashboardHomeView.fxml", "Dashboard");
        } else {
            showAlert(Alert.AlertType.ERROR, "Sai thông tin", "Tài khoản hoặc mật khẩu không chính xác!");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        loadScene("/org/example/healtech/view/RegisterView.fxml", "Đăng ký");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở giao diện: " + title);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
