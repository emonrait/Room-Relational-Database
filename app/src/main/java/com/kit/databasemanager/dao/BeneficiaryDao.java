package com.kit.databasemanager.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kit.databasemanager.model.Address;
import com.kit.databasemanager.model.Alternate;
import com.kit.databasemanager.model.Beneficiary;
import com.kit.databasemanager.model.Biometric;
import com.kit.databasemanager.model.HouseholdInfo;
import com.kit.databasemanager.model.Location;
import com.kit.databasemanager.model.Nominee;
import com.kit.databasemanager.model.SelectionReason;

import java.util.List;

@Dao
public interface BeneficiaryDao {
    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    void insertBeneficiary(Beneficiary beneficiary);

    @Update
    void updateBeneficiary(Beneficiary beneficiary);

    @Delete
    void deleteBeneficiary(Beneficiary beneficiary);

    @Query("SELECT * FROM beneficiary WHERE id = :id")
    Beneficiary getBeneficiaryById(Long id);

    @Query("SELECT * FROM beneficiary WHERE application_id = :appId")
    Beneficiary getBeneficiaryByAppId(String appId);

    @Query("SELECT * FROM biometric WHERE application_id = :appId")
    Biometric getBiometricByAppId(String appId);

    @Query("SELECT * FROM location WHERE application_id = :appId")
    Location getLocationByAppId(String appId);


    @Query("SELECT * FROM address WHERE application_id = :appId")
    Address getAddressByAppId(String appId);

    @Query("SELECT * FROM selection_reason WHERE application_id = :appId")
    List<SelectionReason> getSelectionReasonByAppId(String appId);

    @Query("SELECT * FROM alternate WHERE application_id = :appId")
    List<Alternate> getAlternateByAppId(String appId);

    @Query("SELECT * FROM household_info WHERE application_id = :appId")
    List<HouseholdInfo> getHouseholdInfoByAppId(String appId);

    @Query("SELECT * FROM nominee WHERE application_id = :appId")
    List<Nominee> getNomineeByAppId(String appId);


    @Query("SELECT * FROM beneficiary")
    List<Beneficiary> getAllBeneficiaries();


}
