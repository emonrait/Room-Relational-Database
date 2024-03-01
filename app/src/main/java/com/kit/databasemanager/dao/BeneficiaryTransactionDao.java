package com.kit.databasemanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;

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
public abstract class BeneficiaryTransactionDao {
    @Transaction
    public void insertBeneficiaryRecord(Beneficiary beneficiary, Address address, Location location,
                                        List<Biometric> biometric, List<HouseholdInfo> householdInfo,
                                        List<Alternate> alternate,
                                        List<Nominee> nominee,
                                        List<SelectionReason> selectionReason) {

        for (SelectionReason nowSelectionReason : selectionReason) {
            insert(nowSelectionReason);
        }
        for (Nominee nowNominee : nominee) {
            insert(nowNominee);
        }
        for (Alternate nowAlternate : alternate) {
            insert(nowAlternate);
        }
        for (HouseholdInfo nowHouseholdInfo : householdInfo) {
            insert(nowHouseholdInfo);
        }
        for (Biometric nowBiometric : biometric) {
            insert(nowBiometric);
        }

        insert(location);
        insert(address);
        insert(beneficiary);
    }

    @Insert
    abstract void insert(Beneficiary beneficiary);

    @Insert
    abstract void insert(Address address);

    @Insert
    abstract void insert(Location location);

    @Insert
    abstract void insert(Biometric biometric);

    @Insert
    abstract void insert(HouseholdInfo householdInfo);

    @Insert
    abstract void insert(Alternate alternate);

    @Insert
    abstract void insert(Nominee nominee);

    @Insert
    abstract void insert(SelectionReason selectionReason);
}
