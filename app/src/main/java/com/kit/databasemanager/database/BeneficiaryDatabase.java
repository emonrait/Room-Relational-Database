package com.kit.databasemanager.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kit.databasemanager.dao.AddressDao;
import com.kit.databasemanager.dao.AlternateDao;
import com.kit.databasemanager.dao.BeneficiaryDao;
import com.kit.databasemanager.dao.BeneficiaryTransactionDao;
import com.kit.databasemanager.dao.BiometricDao;
import com.kit.databasemanager.dao.HouseholdInfoDao;
import com.kit.databasemanager.dao.LocationDao;
import com.kit.databasemanager.dao.NomineeDao;
import com.kit.databasemanager.dao.SelectionReasonDao;
import com.kit.databasemanager.model.Address;
import com.kit.databasemanager.model.Alternate;
import com.kit.databasemanager.model.Beneficiary;
import com.kit.databasemanager.model.Biometric;
import com.kit.databasemanager.model.HouseholdInfo;
import com.kit.databasemanager.model.Location;
import com.kit.databasemanager.model.Nominee;
import com.kit.databasemanager.model.SelectionReason;

@Database(entities = {Beneficiary.class, Address.class, Location.class, Alternate.class,
        Nominee.class, Biometric.class, HouseholdInfo.class, SelectionReason.class},
        version = 1, exportSchema = false)
public abstract class BeneficiaryDatabase extends RoomDatabase {
    private static final String LOG_TAG = BeneficiaryDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "BeneficiaryDatabase";
    private static BeneficiaryDatabase sInstance;

    public static BeneficiaryDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                try {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                    BeneficiaryDatabase.class, BeneficiaryDatabase.DATABASE_NAME)
                            .build();
                }catch(Exception exc){
                    Log.d(LOG_TAG, "Error while creating new database instance");
                    exc.printStackTrace();
                }
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract BeneficiaryDao beneficiaryDao();
    public abstract AddressDao addressDao();
    public abstract LocationDao locationDao();
    public abstract AlternateDao alternateDao();
    public abstract NomineeDao nomineeDao();
    public abstract BiometricDao biometricDao();
    public abstract HouseholdInfoDao householdInfoDao();
    public abstract SelectionReasonDao selectionReasonDao();
    public abstract BeneficiaryTransactionDao beneficiaryTransactionDao();
}
