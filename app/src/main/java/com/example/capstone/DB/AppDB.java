package com.example.capstone.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DataForUI.class}, version = 2)
public abstract class AppDB extends RoomDatabase {
    public abstract DataForUIDao dataForUIDao();
}
