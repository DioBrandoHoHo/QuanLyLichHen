package com.example.quanlylichhen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtTenDangNhap, edtMatKhau;
    private TextView tvLoi;
    private Button btnDangNhap;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        anhXa();
        btnDangNhap.setOnClickListener(v -> xuLyDangNhap());
    }

    private void anhXa() {
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        tvLoi = findViewById(R.id.tvLoi);
        btnDangNhap = findViewById(R.id.btnDangNhap);
    }

    // Kiểm tra bỏ trống, đối chiếu bảng users rồi chuyển sang MainActivity
    private void xuLyDangNhap() {
        String tenDangNhap = edtTenDangNhap.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            hienThiLoi("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            return;
        }

        if (!dbHelper.checkLogin(tenDangNhap, matKhau)) {
            hienThiLoi("Sai tên đăng nhập hoặc mật khẩu");
            return;
        }

        tvLoi.setVisibility(View.GONE);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void hienThiLoi(String noiDung) {
        tvLoi.setText(noiDung);
        tvLoi.setVisibility(View.VISIBLE);
    }
}
