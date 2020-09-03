package com.gorontalo.chair.pelangganapp.model;

public class RiwayatPekerjaanModel {
    private String IdPekerjaan, IdKurir, NamaKurir, HpKurir, PhotoKurir, RatingKurir, IdOutlet, NamaOutlet, Metode, Biaya, Total, Tanggal, Status, NamaPelanggan, Catatan;
    private double LatOutlet, LongOutlet, LatPelanggan, LongPelanggan, Jarak;

    public RiwayatPekerjaanModel() {
    }

    public RiwayatPekerjaanModel(String idPekerjaan, String idKurir, String namaKurir, String hpKurir, String photoKurir, String ratingKurir, String idOutlet, String namaOutlet, String metode, String biaya, String total, double latOutlet, double longOutlet, double latPelanggan, double longPelanggan, double jarak) {
        IdPekerjaan = idPekerjaan;
        IdKurir = idKurir;
        NamaKurir = namaKurir;
        HpKurir = hpKurir;
        PhotoKurir = photoKurir;
        RatingKurir = ratingKurir;
        IdOutlet = idOutlet;
        NamaOutlet = namaOutlet;
        Metode = metode;
        Biaya = biaya;
        Total = total;
        LatOutlet = latOutlet;
        LongOutlet = longOutlet;
        LatPelanggan = latPelanggan;
        LongPelanggan = longPelanggan;
        Jarak = jarak;
    }

    public String getIdPekerjaan() {
        return IdPekerjaan;
    }

    public void setIdPekerjaan(String idPekerjaan) {
        IdPekerjaan = idPekerjaan;
    }

    public String getIdKurir() {
        return IdKurir;
    }

    public void setIdKurir(String idKurir) {
        IdKurir = idKurir;
    }

    public String getNamaKurir() {
        return NamaKurir;
    }

    public void setNamaKurir(String namaKurir) {
        NamaKurir = namaKurir;
    }

    public String getHpKurir() {
        return HpKurir;
    }

    public void setHpKurir(String hpKurir) {
        HpKurir = hpKurir;
    }

    public String getPhotoKurir() {
        return PhotoKurir;
    }

    public void setPhotoKurir(String photoKurir) {
        PhotoKurir = photoKurir;
    }

    public String getRatingKurir() {
        return RatingKurir;
    }

    public void setRatingKurir(String ratingKurir) {
        RatingKurir = ratingKurir;
    }

    public String getIdOutlet() {
        return IdOutlet;
    }

    public void setIdOutlet(String idOutlet) {
        IdOutlet = idOutlet;
    }

    public String getNamaOutlet() {
        return NamaOutlet;
    }

    public void setNamaOutlet(String namaOutlet) {
        NamaOutlet = namaOutlet;
    }

    public String getMetode() {
        return Metode;
    }

    public void setMetode(String metode) {
        Metode = metode;
    }

    public String getBiaya() {
        return Biaya;
    }

    public void setBiaya(String biaya) {
        Biaya = biaya;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getNamaPelanggan() {
        return NamaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        NamaPelanggan = namaPelanggan;
    }

    public String getCatatan() {
        return Catatan;
    }

    public void setCatatan(String catatan) {
        Catatan = catatan;
    }

    public double getLatOutlet() {
        return LatOutlet;
    }

    public void setLatOutlet(double latOutlet) {
        LatOutlet = latOutlet;
    }

    public double getLongOutlet() {
        return LongOutlet;
    }

    public void setLongOutlet(double longOutlet) {
        LongOutlet = longOutlet;
    }

    public double getLatPelanggan() {
        return LatPelanggan;
    }

    public void setLatPelanggan(double latPelanggan) {
        LatPelanggan = latPelanggan;
    }

    public double getLongPelanggan() {
        return LongPelanggan;
    }

    public void setLongPelanggan(double longPelanggan) {
        LongPelanggan = longPelanggan;
    }

    public double getJarak() {
        return Jarak;
    }

    public void setJarak(double jarak) {
        Jarak = jarak;
    }
}
