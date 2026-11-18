package com.example.quanlylichhen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.quanlylichhen.R;
import com.example.quanlylichhen.model.Appointment;

import java.util.List;

public class LichHenAdapter extends ArrayAdapter<Appointment> {

    private final Context context;
    private final List<Appointment> danhSach;

    public LichHenAdapter(@NonNull Context context, List<Appointment> danhSach) {
        super(context, R.layout.item_lich_hen, danhSach);
        this.context = context;
        this.danhSach = danhSach;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lich_hen, parent, false);
        }

        TextView tvTieuDe = convertView.findViewById(R.id.tvTieuDe);
        TextView tvDiaDiem = convertView.findViewById(R.id.tvDiaDiem);
        TextView tvNgayGio = convertView.findViewById(R.id.tvNgayGio);
        TextView tvNhacNho = convertView.findViewById(R.id.tvNhacNho);

        Appointment a = danhSach.get(position);
        tvTieuDe.setText(a.getTitle());
        tvDiaDiem.setText("Địa điểm: " + a.getLocation());
        tvNgayGio.setText(a.layNgayGioHienThi());
        hienThiNhanNhacNho(tvNhacNho, a.isReminder());

        return convertView;
    }

    // Đổi chữ và màu nhãn Có nhắc / Không nhắc
    private void hienThiNhanNhacNho(TextView tv, boolean coNhac) {
        if (coNhac) {
            tv.setText("Có nhắc");
            tv.setBackgroundResource(R.drawable.bg_co_nhac);
            tv.setTextColor(ContextCompat.getColor(context, R.color.nhac_text));
        } else {
            tv.setText("Không nhắc");
            tv.setBackgroundResource(R.drawable.bg_khong_nhac);
            tv.setTextColor(ContextCompat.getColor(context, R.color.khong_nhac_text));
        }
    }
}
