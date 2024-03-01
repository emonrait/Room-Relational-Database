package com.kit.databasemanager.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kit.databasemanager.model.Nominee;

import java.util.List;

@Dao
public interface NomineeDao {
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    void insertNominee(Nominee nominee);

    @Update
    void updateNominee(Nominee nominee);

    @Delete
    void deleteNominee(Nominee nominee);

    @Query("SELECT * FROM nominee WHERE id = :id")
    Nominee getNomineeById(Long id);

    @Query("SELECT * FROM nominee")
    List<Nominee> getAllNominees();
}
