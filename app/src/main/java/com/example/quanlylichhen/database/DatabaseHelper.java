package com.example.quanlylichhen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quanlylichhen.model.Appointment;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_LOCATION = "location";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_APPOINTMENTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT NOT NULL, "
                + COL_LOCATION + " TEXT NOT NULL, "
                + COL_DATE + " TEXT NOT NULL, "
                + COL_TIME + " TEXT NOT NULL, "
                + COL_REMINDER + " INTEGER NOT NULL)");

        chenDuLieuMau(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Chèn tài khoản mẫu và 3 lịch hẹn mẫu khi tạo CSDL
    private void chenDuLieuMau(SQLiteDatabase db) {
        themUser(db, "admin", "123456");
        themLichHen(db, new Appointment("Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", true));
        themLichHen(db, new Appointment("Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", true));
        themLichHen(db, new Appointment("Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", false));
    }

    private long themUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    private long themLichHen(SQLiteDatabase db, Appointment a) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, a.getTitle());
        values.put(COL_LOCATION, a.getLocation());
        values.put(COL_DATE, a.getDate());
        values.put(COL_TIME, a.getTime());
        values.put(COL_REMINDER, a.isReminder() ? 1 : 0);
        return db.insert(TABLE_APPOINTMENTS, null, values);
    }

    private Appointment docLichHen(Cursor cursor) {
        return new Appointment(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_REMINDER)) == 1
        );
    }

    private int demSoDong(String sql, String[] args) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        int ketQua = 0;
        if (cursor.moveToFirst()) {
            ketQua = cursor.getInt(0);
        }
        cursor.close();
        return ketQua;
    }

    // Trả về true nếu có tài khoản khớp trong bảng users
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS
                        + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password});
        boolean hopLe = cursor.getCount() > 0;
        cursor.close();
        return hopLe;
    }

    public long insertUser(String username, String password) {
        return themUser(getWritableDatabase(), username, password);
    }

    // Thêm lịch hẹn, trả về id vừa thêm
    public long insertAppointment(Appointment a) {
        return themLichHen(getWritableDatabase(), a);
    }

    // Lấy toàn bộ lịch hẹn, gần nhất trước
    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> danhSach = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, null, null, null, null,
                COL_DATE + " ASC, " + COL_TIME + " ASC");
        while (cursor.moveToNext()) {
            danhSach.add(docLichHen(cursor));
        }
        cursor.close();
        return danhSach;
    }

    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Appointment a = null;
        if (cursor.moveToFirst()) {
            a = docLichHen(cursor);
        }
        cursor.close();
        return a;
    }

    public int updateReminder(int id, boolean on) {
        ContentValues values = new ContentValues();
        values.put(COL_REMINDER, on ? 1 : 0);
        return getWritableDatabase().update(TABLE_APPOINTMENTS, values, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int deleteAppointment(int id) {
        return getWritableDatabase().delete(TABLE_APPOINTMENTS, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int countAll() {
        return demSoDong("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS, null);
    }

    public int countReminders() {
        return demSoDong("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS
                + " WHERE " + COL_REMINDER + " = ?", new String[]{"1"});
    }
}
