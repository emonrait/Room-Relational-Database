package com.kit.databasemanager.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kit.databasemanager.model.Alternate;

import java.util.List;

@Dao
public interface AlternateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlternate(Alternate alternate);

    @Update
    void updateAlternate(Alternate alternate);

    @Delete
    void deleteAlternate(Alternate alternate);

    @Query("SELECT * FROM alternate WHERE id = :id")
    Alternate getAlternateById(Long id);

    @Query("SELECT * FROM alternate")
    List<Alternate> getAllAlternates();

}
