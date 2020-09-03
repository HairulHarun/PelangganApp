package com.gorontalo.chair.pelangganapp.model;

public class LayananModel {
    String id, nama;

    public LayananModel() {
    }

    public LayananModel(String id, String nama) {
        this.id = id;
        this.nama = nama;
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
}
