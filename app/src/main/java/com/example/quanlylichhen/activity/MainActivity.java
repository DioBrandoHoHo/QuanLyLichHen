package com.example.quanlylichhen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.adapter.LichHenAdapter;
import com.example.quanlylichhen.database.DatabaseHelper;
import com.example.quanlylichhen.model.Appointment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "appointment_id";

    private TextView tvTongLichHen, tvSoNhacNho, tvDanhSachRong;
    private ListView lvLichHen;
    private Button btnThemLichHen;

    private DatabaseHelper dbHelper;
    private final ArrayList<Appointment> danhSachLichHen = new ArrayList<>();
    private LichHenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        anhXa();
        caiDatDanhSach();
        caiDatSuKien();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSach();
        capNhatThongKe();
    }

    private void anhXa() {
        tvTongLichHen = findViewById(R.id.tvTongLichHen);
        tvSoNhacNho = findViewById(R.id.tvSoNhacNho);
        tvDanhSachRong = findViewById(R.id.tvDanhSachRong);
        lvLichHen = findViewById(R.id.lvLichHen);
        btnThemLichHen = findViewById(R.id.btnThemLichHen);
    }

    private void caiDatDanhSach() {
        adapter = new LichHenAdapter(this, danhSachLichHen);
        lvLichHen.setAdapter(adapter);
        lvLichHen.setEmptyView(tvDanhSachRong);
    }

    private void caiDatSuKien() {
        btnThemLichHen.setOnClickListener(v ->
                startActivity(new Intent(this, AddAppointmentActivity.class)));

        lvLichHen.setOnItemClickListener((parent, view, position, id) ->
                moChiTiet(danhSachLichHen.get(position).getId()));
    }

    // Đọc lại danh sách lịch hẹn từ CSDL
    private void taiDanhSach() {
        danhSachLichHen.clear();
        danhSachLichHen.addAll(dbHelper.getAllAppointments());
        adapter.notifyDataSetChanged();
    }

    // Cập nhật tổng số lịch hẹn và số lịch hẹn bật nhắc nhở
    private void capNhatThongKe() {
        tvTongLichHen.setText(String.valueOf(dbHelper.countAll()));
        tvSoNhacNho.setText(String.valueOf(dbHelper.countReminders()));
    }

    private void moChiTiet(int idLichHen) {
        Intent intent = new Intent(this, AppointmentDetailActivity.class);
        intent.putExtra(EXTRA_ID, idLichHen);
        startActivity(intent);
    }
}
