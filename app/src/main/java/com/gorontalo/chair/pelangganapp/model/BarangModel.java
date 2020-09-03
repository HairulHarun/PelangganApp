package com.gorontalo.chair.pelangganapp.model;

public class BarangModel {
    private String id, kategori, outlet, barang, harga, status, deskripsi, photo;

    public BarangModel() {
    }

    public BarangModel(String id, String kategori, String outlet, String barang, String harga, String status, String deskripsi, String photo) {
        this.id = id;
        this.kategori = kategori;
        this.outlet = outlet;
        this.barang = barang;
        this.harga = harga;
        this.status = status;
        this.deskripsi = deskripsi;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public String getBarang() {
        return barang;
    }

    public void setBarang(String barang) {
        this.barang = barang;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
