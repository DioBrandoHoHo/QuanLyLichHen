package com.example.quanlylichhen.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.database.DatabaseHelper;
import com.example.quanlylichhen.model.Appointment;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText edtTenLichHen, edtDiaDiem;
    private DatePicker dpNgayHen;
    private TimePicker tpGioHen;
    private MaterialSwitch swNhacNho;
    private Button btnLuuLichHen;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        dbHelper = new DatabaseHelper(this);
        anhXa();
        caiDatBoChon();

        findViewById(R.id.btnQuayLaiHeader).setOnClickListener(v -> finish());
        btnLuuLichHen.setOnClickListener(v -> luuLichHen());
    }

    private void anhXa() {
        edtTenLichHen = findViewById(R.id.edtTenLichHen);
        edtDiaDiem = findViewById(R.id.edtDiaDiem);
        dpNgayHen = findViewById(R.id.dpNgayHen);
        tpGioHen = findViewById(R.id.tpGioHen);
        swNhacNho = findViewById(R.id.swNhacNho);
        btnLuuLichHen = findViewById(R.id.btnLuuLichHen);
    }

    // Đặt giờ 24h, mặc định 1 tiếng sau hiện tại, không cho chọn ngày đã qua
    private void caiDatBoChon() {
        tpGioHen.setIs24HourView(true);

        Calendar macDinh = Calendar.getInstance();
        macDinh.add(Calendar.HOUR_OF_DAY, 1);
        dpNgayHen.updateDate(macDinh.get(Calendar.YEAR), macDinh.get(Calendar.MONTH),
                macDinh.get(Calendar.DAY_OF_MONTH));
        tpGioHen.setHour(macDinh.get(Calendar.HOUR_OF_DAY));
        tpGioHen.setMinute(macDinh.get(Calendar.MINUTE));

        Calendar homNay = Calendar.getInstance();
        homNay.set(Calendar.HOUR_OF_DAY, 0);
        homNay.set(Calendar.MINUTE, 0);
        homNay.set(Calendar.SECOND, 0);
        homNay.set(Calendar.MILLISECOND, 0);
        dpNgayHen.setMinDate(homNay.getTimeInMillis());
    }

    private Calendar layThoiDiemDaChon() {
        Calendar c = Calendar.getInstance();
        c.set(dpNgayHen.getYear(), dpNgayHen.getMonth(), dpNgayHen.getDayOfMonth(),
                tpGioHen.getHour(), tpGioHen.getMinute(), 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    // Kiểm tra tên, địa điểm không trống và thời điểm phải sau hiện tại
    private boolean kiemTraDuLieu(String ten, String diaDiem) {
        if (ten.isEmpty()) {
            edtTenLichHen.setError("Vui lòng nhập tên lịch hẹn");
            edtTenLichHen.requestFocus();
            return false;
        }
        if (diaDiem.isEmpty()) {
            edtDiaDiem.setError("Vui lòng nhập địa điểm");
            edtDiaDiem.requestFocus();
            return false;
        }
        if (!layThoiDiemDaChon().after(Calendar.getInstance())) {
            Toast.makeText(this, "Thời điểm hẹn phải sau thời điểm hiện tại", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Lưu lịch hẹn vào CSDL rồi quay về danh sách
    private void luuLichHen() {
        String ten = edtTenLichHen.getText().toString().trim();
        String diaDiem = edtDiaDiem.getText().toString().trim();

        if (!kiemTraDuLieu(ten, diaDiem)) {
            return;
        }

        String ngay = String.format(Locale.US, "%04d-%02d-%02d",
                dpNgayHen.getYear(), dpNgayHen.getMonth() + 1, dpNgayHen.getDayOfMonth());
        String gio = String.format(Locale.US, "%02d:%02d", tpGioHen.getHour(), tpGioHen.getMinute());

        Appointment lichHen = new Appointment(ten, diaDiem, ngay, gio, swNhacNho.isChecked());
        long id = dbHelper.insertAppointment(lichHen);

        if (id == -1) {
            Toast.makeText(this, "Lưu lịch hẹn thất bại", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đã lưu lịch hẹn", Toast.LENGTH_SHORT).show();
        finish();
    }
}
