package com.example.capstone.DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.DeleteTable;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DataForUIDao {
    @Query("SELECT * FROM DataForUI ORDER BY id")
    LiveData<List<DataForUI>> getAll();

    @Insert
    void insert(DataForUI dataforui);

    @Update
    void update(DataForUI dataforui);

    @Query("DELETE FROM DataForUI")
    void deleteAll();
}
