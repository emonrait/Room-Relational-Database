package com.kit.databasemanager.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kit.databasemanager.model.Address;

import java.util.List;

@Dao
public interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAddress(Address address);

    @Update
    void updateAddress(Address address);

    @Delete
    void deleteAddress(Address address);

    @Query("SELECT * FROM address WHERE id = :id")
    Address getAddressById(Long id);

    @Query("SELECT * FROM address")
    List<Address> getAllAddresses();
}

