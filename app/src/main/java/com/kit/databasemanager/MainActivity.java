package com.kit.databasemanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.databasemanager.dao.BeneficiaryDao;
import com.kit.databasemanager.dao.BeneficiaryTransactionDao;
import com.kit.databasemanager.dao.SelectionReasonDao;
import com.kit.databasemanager.database.BeneficiaryDatabase;
import com.kit.databasemanager.database.DatabaseExecutors;
import com.kit.databasemanager.model.Address;
import com.kit.databasemanager.model.Alternate;
import com.kit.databasemanager.model.Beneficiary;
import com.kit.databasemanager.model.Biometric;
import com.kit.databasemanager.model.HouseholdInfo;
import com.kit.databasemanager.model.Location;
import com.kit.databasemanager.model.Nominee;
import com.kit.databasemanager.model.SelectionReason;
import com.kit.integrationmanager.model.BiometricType;
import com.kit.integrationmanager.model.SelectionReasonEnum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button registerBtn;
    private Button showBtn;

    private UUID uuid;

    private BeneficiaryDatabase mDatabase;

    private final String TAG = "MAIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerBtn = findViewById(R.id.insertBeneficiary);
        showBtn = findViewById(R.id.showBeneficiary);

        mDatabase = BeneficiaryDatabase.getInstance(getApplicationContext());

        registerBtn.setOnClickListener(v -> insertBeneficiary());

        showBtn.setOnClickListener(v -> showBeneficiary());
    }

    public void insertBeneficiary() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getResources().openRawResource(R.raw.single_reg);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            com.kit.integrationmanager.model.Beneficiary beneficiaryBO = mapper.readValue(br, com.kit.integrationmanager.model.Beneficiary.class);

            Log.d(TAG, "Loaded beneficiary reason : " + beneficiaryBO.getSelectionReason().get(0));

            DatabaseExecutors.getInstance().diskIO().execute(() -> {
                uuid = UUID.randomUUID();

                Beneficiary beneficiaryEO = prepareBeneficiaryEntity(uuid.toString(), beneficiaryBO);
                Address addressEO = prepareAddressEntity(uuid.toString(), beneficiaryBO.getAddress());
                Location locationEO = prepareLocationEntity(uuid.toString(), beneficiaryBO.getLocation());
                List<SelectionReason> selectionReasonList = prepareSelectionReasonEntity(uuid.toString(), beneficiaryBO.getSelectionReason());

                List<Alternate> alternateList = new ArrayList<>();

                Alternate firstAlternateEO = prepareAlternateEntity(uuid.toString(), beneficiaryBO.getAlternatePayee1());
                alternateList.add(firstAlternateEO);
                Alternate secondAlternateEO = prepareAlternateEntity(uuid.toString(), beneficiaryBO.getAlternatePayee2());
                alternateList.add(secondAlternateEO);

                List<Nominee> nomineeList = prepareNomineeEntity(uuid.toString(), beneficiaryBO.getNominees());

                List<HouseholdInfo> householdInfoList = new ArrayList<>();

                HouseholdInfo householdInfo2EO = prepareHouseholdInfoEntity(uuid.toString(), beneficiaryBO.getHouseholdMember2());
                householdInfoList.add(householdInfo2EO);
                HouseholdInfo householdInfo5EO = prepareHouseholdInfoEntity(uuid.toString(), beneficiaryBO.getHouseholdMember5());
                householdInfoList.add(householdInfo5EO);
                HouseholdInfo householdInfo17EO = prepareHouseholdInfoEntity(uuid.toString(), beneficiaryBO.getHouseholdMember17());
                householdInfoList.add(householdInfo17EO);
                HouseholdInfo householdInfo35EO = prepareHouseholdInfoEntity(uuid.toString(), beneficiaryBO.getHouseholdMember35());
                householdInfoList.add(householdInfo35EO);
                HouseholdInfo householdInfo64EO = prepareHouseholdInfoEntity(uuid.toString(), beneficiaryBO.getHouseholdMember64());
                householdInfoList.add(householdInfo64EO);
                HouseholdInfo householdInfo65EO = prepareHouseholdInfoEntity(uuid.toString(), beneficiaryBO.getHouseholdMember65());
                householdInfoList.add(householdInfo65EO);

                List<Biometric> biometricList = new ArrayList<>();

                if (beneficiaryBO.getBiometrics() != null) {
                    Biometric beneficiaryBiometric = prepareBiometricEntity(uuid.toString(), beneficiaryBO.getBiometrics());
                    biometricList.add(beneficiaryBiometric);
                }
                if (beneficiaryBO.getAlternatePayee1() != null && beneficiaryBO.getAlternatePayee1().getBiometrics() != null) {
                    Biometric alternate1Biometric = prepareBiometricEntity(uuid.toString(), beneficiaryBO.getAlternatePayee1().getBiometrics());
                    biometricList.add(alternate1Biometric);
                }

                if (beneficiaryBO.getAlternatePayee2() != null && beneficiaryBO.getAlternatePayee2().getBiometrics() != null) {
                    Biometric alternate2Biometric = prepareBiometricEntity(uuid.toString(), beneficiaryBO.getAlternatePayee2().getBiometrics());
                    biometricList.add(alternate2Biometric);
                }

                BeneficiaryTransactionDao beneficiaryTransactionDao = mDatabase.beneficiaryTransactionDao();
                beneficiaryTransactionDao.insertBeneficiaryRecord(beneficiaryEO, addressEO, locationEO, biometricList,
                        householdInfoList, alternateList, nomineeList, selectionReasonList);

                /*BeneficiaryDao beneficiaryDao = mDatabase.beneficiaryDao();
                AddressDao addressDao = mDatabase.addressDao();
                LocationDao locationDao = mDatabase.locationDao();
                BiometricDao biometricDao = mDatabase.biometricDao();
                AlternateDao alternateDao = mDatabase.alternateDao();
                NomineeDao nomineeDao = mDatabase.nomineeDao();
                HouseholdInfoDao householdInfoDao = mDatabase.householdInfoDao();
                SelectionReasonDao selectionReasonDao = mDatabase.selectionReasonDao();

                beneficiaryDao.insertBeneficiary(beneficiaryEO);
                addressDao.insertAddress(addressEO);
                locationDao.insertLocation(locationEO);

                for(Biometric biometricEO:biometricList){
                    biometricDao.insertBiometric(biometricEO);
                }
                for(Alternate alternateEO:alternateList){
                    alternateDao.insertAlternate(alternateEO);
                }
                for(Nominee nomineeEO:nomineeList){
                    nomineeDao.insertNominee(nomineeEO);
                }
                for(HouseholdInfo householdInfoEO:householdInfoList){
                    householdInfoDao.insertHouseholdInfo(householdInfoEO);
                }
                for(SelectionReason selectionReasonEO:selectionReasonList){
                    selectionReasonDao.insertSelectionReason(selectionReasonEO);
                }*/

                Log.d(TAG, "Inserted the beneficiary data");
            });

        } catch (Exception ex) {
            Log.e(TAG, "Error while sending data : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void showBeneficiary() {
        DatabaseExecutors.getInstance().diskIO().execute(() -> {
            try {
                BeneficiaryDao beneficiaryDao = mDatabase.beneficiaryDao();
                SelectionReasonDao selectionReasonDao = mDatabase.selectionReasonDao();
                SelectionReason selectionReasonEO = selectionReasonDao.getSelectionReasonByAppId(uuid.toString());
                Beneficiary beneficiaryEO = beneficiaryDao.getBeneficiaryByAppId(uuid.toString());
                Log.d(TAG, "Loaded beneficiary");
                Log.d(TAG, "DB loaded beneficiary first name " + beneficiaryEO.respondentFirstName);
                Log.d(TAG, "Beneficiary name " + beneficiaryEO);
                Log.d(TAG, "Loaded beneficiary reason : " + selectionReasonEO.selectionReasonName);
            } catch (Exception exc) {
                Log.e(TAG, "Error while loading beneficiary.");
                exc.printStackTrace();
            }
        });
    }

    public Biometric prepareBiometricEntity(String appId, List<com.kit.integrationmanager.model.Biometric> biometricList) {
        Biometric nowBiometricEO = new Biometric();
        if (biometricList != null) {
            for (com.kit.integrationmanager.model.Biometric nowBiometric : biometricList) {
                if (nowBiometric != null) {
                    nowBiometricEO.applicationId = appId;
                    nowBiometricEO.biometricUserType = (long) nowBiometric.getBiometricUserType().ordinal();
                    nowBiometricEO.noFingerPrint = nowBiometric.getNoFingerPrint();
                    nowBiometricEO.noFingerprintReason = (long) nowBiometric.getNoFingerprintReason().ordinal();
                    nowBiometricEO.noFingerprintReasonText = nowBiometric.getNoFingerprintReasonText();
                    if (nowBiometric.getBiometricType() == BiometricType.PHOTO) {
                        nowBiometricEO.photo = nowBiometric.getBiometricData();
                    }

                    switch (nowBiometric.getBiometricType()) {
                        case PHOTO:
                            nowBiometricEO.photo = nowBiometric.getBiometricData();
                            break;
                        case LT:
                            nowBiometricEO.wsqLt = nowBiometric.getBiometricData();
                            break;
                        case LI:
                            nowBiometricEO.wsqLi = nowBiometric.getBiometricData();
                            break;
                        case LM:
                            nowBiometricEO.wsqLm = nowBiometric.getBiometricData();
                            break;
                        case LR:
                            nowBiometricEO.wsqLr = nowBiometric.getBiometricData();
                            break;
                        case LL:
                            nowBiometricEO.wsqLs = nowBiometric.getBiometricData();
                            break;
                        case RT:
                            nowBiometricEO.wsqRt = nowBiometric.getBiometricData();
                            break;
                        case RI:
                            nowBiometricEO.wsqRi = nowBiometric.getBiometricData();
                            break;
                        case RM:
                            nowBiometricEO.wsqRm = nowBiometric.getBiometricData();
                            break;
                        case RR:
                            nowBiometricEO.wsqRr = nowBiometric.getBiometricData();
                            break;
                        case RL:
                            nowBiometricEO.wsqRs = nowBiometric.getBiometricData();
                            break;
                    }

                }
            }
        }

        return nowBiometricEO;
    }

    public Address prepareAddressEntity(String appId, com.kit.integrationmanager.model.Address addressBO) {
        Address addressEO = new Address();
        if (addressBO != null) {
            addressEO.applicationId = appId;
            //addressEO.setAddressLine(addressBO.getAddressLine());
            addressEO.stateId = addressBO.getStateId();
            addressEO.countyId = addressBO.getCountyId();
            addressEO.payam = addressBO.getPayam();
            addressEO.boma = addressBO.getBoma();
        }
        return addressEO;
    }

    public Location prepareLocationEntity(String appId, com.kit.integrationmanager.model.Location locationBO) {
        Location locationEO = new Location();
        if (locationBO != null) {
            locationEO.lat = locationBO.getLat();
            locationEO.lon = locationBO.getLon();
            locationEO.applicationId = appId;
        }
        return locationEO;
    }

    public Alternate prepareAlternateEntity(String appId, com.kit.integrationmanager.model.AlternatePayee alternateBO) {
        Alternate alternateEO = new Alternate();
        alternateEO.applicationId = appId;
        alternateEO.payeeFirstName = alternateBO.getPayeeFirstName();
        alternateEO.payeeMiddleName = alternateBO.getPayeeMiddleName();
        alternateEO.payeeLastName = alternateBO.getPayeeLastName();
        alternateEO.payeeNickName = alternateBO.getPayeeNickName();
        alternateEO.payeeGender = alternateBO.getPayeeGender() != null ? (long) alternateBO.getPayeeGender().ordinal() : null;
        alternateEO.payeeAge = alternateBO.getPayeeAge();
        alternateEO.documentType = alternateBO.getDocumentType() != null ? (long) alternateBO.getDocumentType().ordinal() : null;
        alternateEO.documentTypeOther = alternateBO.getDocumentTypeOther();
        alternateEO.nationalId = alternateBO.getNationalId();
        alternateEO.payeePhoneNo = alternateBO.getPayeePhoneNo();
        return alternateEO;
    }

    public List<Nominee> prepareNomineeEntity(String appId, List<com.kit.integrationmanager.model.Nominee> nomineeList) {
        List<Nominee> nominees = new ArrayList<>();
        if (nomineeList != null) {
            for (com.kit.integrationmanager.model.Nominee nominee : nomineeList) {
                Nominee nomineeEO = new Nominee();

                nomineeEO.applicationId = appId;
                nomineeEO.nomineeFirstName = nominee.getNomineeFirstName();
                nomineeEO.nomineeMiddleName = nominee.getNomineeMiddleName();
                nomineeEO.nomineeLastName = nominee.getNomineeLastName();
                nomineeEO.nomineeNickName = nominee.getNomineeNickName();
                nomineeEO.relationshipWithHouseholdHead = nominee.getRelationshipWithHouseholdHead() != null ? (long) nominee.getRelationshipWithHouseholdHead().ordinal() : null;
                nomineeEO.nomineeAge = nominee.getNomineeAge();
                nomineeEO.nomineeGender = nominee.getNomineeGender() != null ? (long) nominee.getNomineeGender().ordinal() : null;
                nomineeEO.isReadWrite = nominee.getIsReadWrite();
                nomineeEO.nomineeOccupation = nominee.getNomineeOccupation() != null ? (long) nominee.getNomineeOccupation().ordinal() : null;
                nomineeEO.otherOccupation = nominee.getOtherOccupation();
                nominees.add(nomineeEO);
            }
        }
        return nominees;
    }

    public HouseholdInfo prepareHouseholdInfoEntity(String appId, com.kit.integrationmanager.model.HouseholdMember member) {

        HouseholdInfo householdInfoEO = new HouseholdInfo();
        if (member != null) {
            householdInfoEO.applicationId = appId;
            householdInfoEO.maleTotal = member.getTotalMale();
            householdInfoEO.femaleTotal = member.getTotalFemale();
            householdInfoEO.maleDisable = member.getMaleDisable();
            householdInfoEO.femaleDisable = member.getFemaleDisable();
            householdInfoEO.maleChronicalIll = member.getMaleChronicalIll();
            householdInfoEO.femaleChronicalIll = member.getFemaleChronicalIll();
            householdInfoEO.femaleNormal = member.getFemaleNormal();
        }
        return householdInfoEO;
    }

    public List<SelectionReason> prepareSelectionReasonEntity(String appId, List<SelectionReasonEnum> reasons) {
        Log.d(TAG, "Reason List: " + appId + reasons.get(0).getValue());

        List<SelectionReason> selectionReasons = new ArrayList<>();
        if (reasons != null) {
            for (SelectionReasonEnum nowReason : reasons) {
                SelectionReason nowSelectionReason = new SelectionReason();
                nowSelectionReason.applicationId = appId;
                nowSelectionReason.selectionReasonName = nowReason.getValue();
                selectionReasons.add(nowSelectionReason);
            }
            if (selectionReasons.size() <= 0) {
                SelectionReason nowSelectionReason = new SelectionReason();
                nowSelectionReason.applicationId = appId;
                nowSelectionReason.selectionReasonName = SelectionReasonEnum.LIPW_REASON_4.getValue();
                selectionReasons.add(nowSelectionReason);
            }
        }
        return selectionReasons;
    }

    public Beneficiary prepareBeneficiaryEntity(String appId, com.kit.integrationmanager.model.Beneficiary beneficiaryBO) {
        Beneficiary beneficiaryEO = new Beneficiary();
        if (beneficiaryBO != null) {
            beneficiaryEO.applicationId = appId;
            beneficiaryEO.respondentFirstName = beneficiaryBO.getRespondentFirstName();
            beneficiaryEO.respondentMiddleName = beneficiaryBO.getRespondentMiddleName();
            beneficiaryEO.respondentLastName = beneficiaryBO.getRespondentLastName();
            beneficiaryEO.respondentNickName = beneficiaryBO.getRespondentNickName();

            beneficiaryEO.spouseFirstName = beneficiaryBO.getSpouseFirstName();
            beneficiaryEO.spouseMiddleName = beneficiaryBO.getSpouseMiddleName();
            beneficiaryEO.spouseLastName = beneficiaryBO.getSpouseLastName();
            beneficiaryEO.spouseNickName = beneficiaryBO.getSpouseNickName();

            beneficiaryEO.relationshipWithHouseholdHead = beneficiaryBO.getRelationshipWithHouseholdHead() != null ? (long) beneficiaryBO.getRelationshipWithHouseholdHead().ordinal() : null;

            beneficiaryEO.respondentAge = beneficiaryBO.getRespondentAge();
            beneficiaryEO.respondentGender = beneficiaryBO.getRespondentGender() != null ? (long) beneficiaryBO.getRespondentGender().ordinal() : null;
            beneficiaryEO.respondentMaritalStatus = beneficiaryBO.getRespondentMaritalStatus() != null ? (long) beneficiaryBO.getRespondentMaritalStatus().ordinal() : null;
            beneficiaryEO.respondentLegalStatus = beneficiaryBO.getRespondentLegalStatus() != null ? (long) beneficiaryBO.getRespondentLegalStatus().ordinal() : null;

            beneficiaryEO.documentType = beneficiaryBO.getDocumentType() != null ? (long) beneficiaryBO.getDocumentType().ordinal() : null;
            beneficiaryEO.documentTypeOther = beneficiaryBO.getDocumentTypeOther();

            beneficiaryEO.respondentId = beneficiaryBO.getRespondentId();
            beneficiaryEO.respondentPhoneNo = beneficiaryBO.getRespondentPhoneNo();
            beneficiaryEO.householdIncomeSource = beneficiaryBO.getHouseholdIncomeSource() != null ? (long) beneficiaryBO.getHouseholdIncomeSource().ordinal() : null;
            beneficiaryEO.householdMonthlyAvgIncome = beneficiaryBO.getHouseholdMonthlyAvgIncome();
            beneficiaryEO.householdSize = beneficiaryBO.getHouseholdSize();
            beneficiaryEO.isOtherMemberPerticipating = beneficiaryBO.getIsOtherMemberPerticipating();

            beneficiaryEO.isReadWrite = beneficiaryBO.getIsReadWrite();
            beneficiaryEO.memberReadWrite = beneficiaryBO.getMemberReadWrite();
            beneficiaryEO.notPerticipationReason = beneficiaryBO.getNotPerticipationReason() != null ? (long) beneficiaryBO.getNotPerticipationReason().ordinal() : null;
            beneficiaryEO.notPerticipationOtherReason = beneficiaryBO.getNotPerticipationOtherReason();

            beneficiaryEO.createdBy = beneficiaryBO.getCreatedBy();
            beneficiaryEO.selectionCriteria = beneficiaryBO.getSelectionCriteria() != null ? (long) beneficiaryBO.getSelectionCriteria().ordinal() : null;

            beneficiaryEO.currency = beneficiaryBO.getCurrency() != null ? (long) beneficiaryBO.getCurrency().ordinal() : null;
        }

        return beneficiaryEO;
    }
}