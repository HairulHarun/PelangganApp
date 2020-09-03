package com.gorontalo.chair.pelangganapp.interfacee;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gorontalo.chair.pelangganapp.model.KeranjangModel;

import java.util.List;

@Dao
public interface KeranjangDAO {
    @Query("SELECT * FROM keranjang")
    List<KeranjangModel> getAll();

    @Query("SELECT SUM((harga_barang*jumlah_barang)) FROM keranjang")
    int getTotal();

    @Query("SELECT COUNT(id_barang) FROM keranjang")
    int getJumlah();

    @Insert
    void insert(KeranjangModel task);

    @Delete
    void delete(KeranjangModel task);

    @Query("DELETE FROM keranjang")
    void deleteAll();

    @Query("UPDATE keranjang SET jumlah_barang= :jumlah WHERE id_barang= :id")
    void updateById(String id, String jumlah);

    @Query("DELETE FROM keranjang WHERE id_barang= :id")
    void deleteById(String id);

    @Update
    void update(KeranjangModel task);
}
