package com.gorontalo.chair.pelangganapp.adapter;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseClientAdapter {
    private Context mCtx;
    private static DatabaseClientAdapter mInstance;

    //our app database object
    private AppDatabaseAdapter appDatabase;

    private DatabaseClientAdapter(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDatabaseAdapter.class, "MyToDos").build();
    }

    public static synchronized DatabaseClientAdapter getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClientAdapter(mCtx);
        }
        return mInstance;
    }

    public AppDatabaseAdapter getAppDatabase() {
        return appDatabase;
    }
}
