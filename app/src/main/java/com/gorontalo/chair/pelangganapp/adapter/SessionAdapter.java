package com.gorontalo.chair.pelangganapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.gorontalo.chair.pelangganapp.LoginActivity;
import com.gorontalo.chair.pelangganapp.MainActivity;

public class SessionAdapter {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Sesi";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_SHOW = "IsShow";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "nama";
    public static final String KEY_KTP = "ktp";
    public static final String KEY_HP = "hp";
    public static final String KEY_SEX = "sex";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ALAMAT = "alamat";
    public static final String KEY_SALDO = "saldo";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_BIAYA = "biaya";
    public static final String KEY_BIAYA2 = "biaya2";
    public static final String KEY_POTONGAN = "potongan";
    public static final String FIREBASE_TOKEN = "token";

    public SessionAdapter(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String id, String nama, String ktp, String hp, String sex, String email, String alamat, String saldo, String username, String photo, int biaya, int biaya2, int potongan){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, nama);
        editor.putString(KEY_KTP, ktp);
        editor.putString(KEY_HP, hp);
        editor.putString(KEY_SEX, sex);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_SALDO, saldo);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PHOTO, photo);
        editor.putInt(KEY_BIAYA, biaya);
        editor.putInt(KEY_BIAYA2, biaya2);
        editor.putInt(KEY_POTONGAN, potongan);
        editor.commit();
    }

    public void simpanToken(String token){
        editor.putString(FIREBASE_TOKEN, token);
        editor.commit();
    }

    public void createDialogSession(){
        editor.putBoolean(IS_SHOW, true);
        editor.commit();
    }

    public void checkLoginMain(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void checkLogin(){
        if(this.isLoggedIn()){
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void updateSaldo(String saldo){
        editor.putString(KEY_SALDO, saldo);
        editor.commit();
    }

    public String getID(){
        String user = pref.getString(KEY_ID, null);
        return user;
    }

    public String getName(){
        String user = pref.getString(KEY_NAME, null);
        return user;
    }

    public String getSaldo(){
        String user = pref.getString(KEY_SALDO, null);
        return user;
    }

    public String getKtp(){
        String user = pref.getString(KEY_KTP, null);
        return user;
    }

    public String getHp(){
        String user = pref.getString(KEY_HP, null);
        return user;
    }

    public String getSex(){
        String user = pref.getString(KEY_SEX, null);
        return user;
    }

    public String getEmail(){
        String user = pref.getString(KEY_EMAIL, null);
        return user;
    }

    public String getAlamat(){
        String user = pref.getString(KEY_ALAMAT, null);
        return user;
    }

    public String getPhoto(){
        String user = pref.getString(KEY_PHOTO, null);
        return user;
    }

    public String getBiaya1(){
        String user = pref.getString(KEY_BIAYA, null);
        return user;
    }

    public String getBiaya2(){
        String user = pref.getString(KEY_BIAYA2, null);
        return user;
    }

    public String getPotongan(){
        String user = String.valueOf(pref.getInt(KEY_POTONGAN, 0));
        return user;
    }

    public void setBiaya(String biaya){
        editor.putString(KEY_BIAYA, biaya);
        editor.commit();
    }

    public void setBiaya2(String biaya2){
        editor.putString(KEY_BIAYA2, biaya2);
        editor.commit();
    }

    public void setSaldo(String saldo){
        editor.putString(KEY_SALDO, saldo);
        editor.commit();
    }

    public String getToken(){
        String token = pref.getString(FIREBASE_TOKEN, null);
        return token;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isShow(){
        return pref.getBoolean(IS_SHOW, false);
    }
}
