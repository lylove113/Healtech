package org.example.healtech.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.healtech.dao.RegisterDAO;
import org.example.healtech.model.NhanVien;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField fullnameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private final RegisterDAO registerDAO = new RegisterDAO();

    @FXML
    private void handleRegister(ActionEvent event) {
        String fullname = fullnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không khớp!");
            return;
        }


        NhanVien nv = new NhanVien(fullname, email, password);
        boolean success = registerDAO.registerUser(nv);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công! Hãy đăng nhập.");
            goToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể đăng ký tài khoản!");
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        goToLogin();
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/healtech/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) fullnameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
