package com.gorontalo.chair.pelangganapp.model;

public class OutletModel {
    private String id, nama, pemilik, hp, waktu, deskripsi, photo;
    private double latitude, longitude;

    public OutletModel() {
    }

    public OutletModel(String id, String nama, String pemilik, String hp, String waktu, String deskripsi, String photo, double latitude, double longitude) {
        this.id = id;
        this.nama = nama;
        this.pemilik = pemilik;
        this.hp = hp;
        this.waktu = waktu;
        this.deskripsi = deskripsi;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPemilik() {
        return pemilik;
    }

    public void setPemilik(String pemilik) {
        this.pemilik = pemilik;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
