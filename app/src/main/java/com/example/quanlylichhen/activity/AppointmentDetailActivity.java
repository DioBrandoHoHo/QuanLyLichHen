package com.example.quanlylichhen.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.database.DatabaseHelper;
import com.example.quanlylichhen.model.Appointment;
import com.google.android.material.materialswitch.MaterialSwitch;

public class AppointmentDetailActivity extends AppCompatActivity {

    private TextView tvTieuDe, tvDiaDiem, tvThoiGian, tvTrangThaiNhac;
    private MaterialSwitch swNhacNho;
    private Button btnQuayLai, btnXoaLichHen;

    private DatabaseHelper dbHelper;
    private int idLichHen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        dbHelper = new DatabaseHelper(this);
        idLichHen = getIntent().getIntExtra(MainActivity.EXTRA_ID, -1);

        anhXa();
        if (!hienThiChiTiet()) {
            Toast.makeText(this, "Không tìm thấy lịch hẹn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        caiDatSuKien();
    }

    private void anhXa() {
        tvTieuDe = findViewById(R.id.tvTieuDe);
        tvDiaDiem = findViewById(R.id.tvDiaDiem);
        tvThoiGian = findViewById(R.id.tvThoiGian);
        tvTrangThaiNhac = findViewById(R.id.tvTrangThaiNhac);
        swNhacNho = findViewById(R.id.swNhacNho);
        btnQuayLai = findViewById(R.id.btnQuayLai);
        btnXoaLichHen = findViewById(R.id.btnXoaLichHen);
    }

    // Truy vấn lịch hẹn theo id nhận từ Intent và đổ lên giao diện
    private boolean hienThiChiTiet() {
        Appointment a = dbHelper.getAppointmentById(idLichHen);
        if (a == null) {
            return false;
        }
        tvTieuDe.setText(a.getTitle());
        tvDiaDiem.setText(a.getLocation());
        tvThoiGian.setText(a.layNgayGioHienThi());
        swNhacNho.setChecked(a.isReminder());
        capNhatChuTrangThai(a.isReminder());
        return true;
    }

    private void caiDatSuKien() {
        findViewById(R.id.btnQuayLaiHeader).setOnClickListener(v -> finish());
        btnQuayLai.setOnClickListener(v -> finish());
        swNhacNho.setOnCheckedChangeListener((buttonView, isChecked) -> doiNhacNho(isChecked));
        btnXoaLichHen.setOnClickListener(v -> xacNhanXoa());
    }

    // Ghi trạng thái nhắc nhở mới vào CSDL
    private void doiNhacNho(boolean batNhac) {
        dbHelper.updateReminder(idLichHen, batNhac);
        capNhatChuTrangThai(batNhac);
    }

    private void capNhatChuTrangThai(boolean batNhac) {
        tvTrangThaiNhac.setText(batNhac ? "Đang bật" : "Đang tắt");
    }

    private void xacNhanXoa() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch hẹn")
                .setMessage("Bạn có chắc muốn xóa lịch hẹn \"" + tvTieuDe.getText() + "\"?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> xoaLichHen())
                .show();
    }

    // Xóa lịch hẹn khỏi CSDL rồi quay về danh sách
    private void xoaLichHen() {
        dbHelper.deleteAppointment(idLichHen);
        Toast.makeText(this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show();
        finish();
    }
}
