package com.gorontalo.chair.pelangganapp.adapter;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.gorontalo.chair.pelangganapp.interfacee.KeranjangDAO;
import com.gorontalo.chair.pelangganapp.model.KeranjangModel;

@Database(entities = {KeranjangModel.class}, version = 1)
public abstract class AppDatabaseAdapter extends RoomDatabase {
    public abstract KeranjangDAO keranjangDAO();
}
