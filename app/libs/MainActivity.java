package com.kit.databasemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.databasemanager.dao.BeneficiaryTransactionDao;
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


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button registerBtn;
    private Button showBtn;

    private BeneficiaryDatabase mDatabase;

    private String TAG = "MAIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerBtn = findViewById(R.id.insertBeneficiary);
        showBtn = findViewById(R.id.showBeneficiary);

        mDatabase = BeneficiaryDatabase.getInstance(getApplicationContext());

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    insertBeneficiary();
            }
        });

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBeneficiary();
            }
        });
    }

    public void insertBeneficiary(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getResources().openRawResource(R.raw.single_reg);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            com.kit.integrationmanager.model.Beneficiary beneficiaryBO = mapper.readValue(br,  com.kit.integrationmanager.model.Beneficiary.class);

            DatabaseExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    UUID uuid = UUID.randomUUID();
                    Beneficiary beneficiaryEO = prepareBeneficiaryEntity(uuid.toString(),beneficiaryBO);
                    Address addressEO = prepareAddressEntity(uuid.toString(),beneficiaryBO.getAddress());
                    Location locationEO = prepareLocationEntity(uuid.toString(),beneficiaryBO.getLocation());
                    List<SelectionReason> selectionReasonList = prepareSelectionReasonEntity(uuid.toString(),null);

                    List<Alternate> alternateList = new ArrayList<>();
                    Alternate firstAlternateEO = prepareAlternateEntity(uuid.toString(),beneficiaryBO.getAlternatePayee1());
                    alternateList.add(firstAlternateEO);
                    Alternate secondAlternateEO = prepareAlternateEntity(uuid.toString(),beneficiaryBO.getAlternatePayee2());
                    alternateList.add(secondAlternateEO);

                    List<Nominee> nomineeList = prepareNomineeEntity(uuid.toString(),beneficiaryBO.getNominees());

                    List<HouseholdInfo> householdInfoList = new ArrayList<>();

                    HouseholdInfo householdInfo2EO = prepareHouseholdInfoEntity(uuid.toString(),beneficiaryBO.getHouseholdMember2());
                    householdInfoList.add(householdInfo2EO);
                    HouseholdInfo householdInfo5EO = prepareHouseholdInfoEntity(uuid.toString(),beneficiaryBO.getHouseholdMember5());
                    householdInfoList.add(householdInfo5EO);
                    HouseholdInfo householdInfo17EO = prepareHouseholdInfoEntity(uuid.toString(),beneficiaryBO.getHouseholdMember17());
                    householdInfoList.add(householdInfo17EO);
                    HouseholdInfo householdInfo35EO = prepareHouseholdInfoEntity(uuid.toString(),beneficiaryBO.getHouseholdMember35());
                    householdInfoList.add(householdInfo35EO);
                    HouseholdInfo householdInfo64EO = prepareHouseholdInfoEntity(uuid.toString(),beneficiaryBO.getHouseholdMember64());
                    householdInfoList.add(householdInfo64EO);
                    HouseholdInfo householdInfo65EO = prepareHouseholdInfoEntity(uuid.toString(),beneficiaryBO.getHouseholdMember65());
                    householdInfoList.add(householdInfo65EO);

                    List<Biometric> biometricList = new ArrayList<>();

                    if(beneficiaryBO.getBiometrics()!=null) {
                        Biometric beneficiaryBiometric = prepareBiometricEntity(uuid.toString(), beneficiaryBO.getBiometrics());
                        biometricList.add(beneficiaryBiometric);
                    }
                    if(beneficiaryBO.getAlternatePayee1()!=null && beneficiaryBO.getAlternatePayee1().getBiometrics()!=null) {
                        Biometric alternate1Biometric = prepareBiometricEntity(uuid.toString(), beneficiaryBO.getAlternatePayee1().getBiometrics());
                        biometricList.add(alternate1Biometric);
                    }

                    if(beneficiaryBO.getAlternatePayee2()!=null && beneficiaryBO.getAlternatePayee2().getBiometrics()!=null) {
                        Biometric alternate2Biometric = prepareBiometricEntity(uuid.toString(), beneficiaryBO.getAlternatePayee2().getBiometrics());
                        biometricList.add(alternate2Biometric);
                    }

                    BeneficiaryTransactionDao beneficiaryTransactionDao = mDatabase.beneficiaryTransactionDao();
                    beneficiaryTransactionDao.insertBeneficiaryRecord(beneficiaryEO,addressEO,locationEO,biometricList,
                                    householdInfoList,alternateList,nomineeList,selectionReasonList);

                }
            });

        }catch (Exception ex){
            Log.e(TAG,"Error while sending data : "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void showBeneficiary(){

    }

    public Biometric prepareBiometricEntity(String appId, List<com.kit.integrationmanager.model.Biometric> biometricList){
        Biometric nowBiometricEO = new Biometric();
        for(com.kit.integrationmanager.model.Biometric nowBiometric : biometricList){

            if(nowBiometric!=null) {
                nowBiometricEO.setApplicationId(appId);
                nowBiometricEO.setBiometricUserType((long)nowBiometric.getBiometricUserType().ordinal());
                nowBiometricEO.setNoFingerPrint(nowBiometric.getNoFingerPrint());
                nowBiometricEO.setNoFingerprintReason((long)nowBiometric.getNoFingerprintReason().ordinal());
                nowBiometricEO.setNoFingerprintReasonText(nowBiometric.getNoFingerprintReasonText());
                if(nowBiometric.getBiometricType() ==  BiometricType.PHOTO) {
                    nowBiometricEO.setPhoto(nowBiometric.getBiometricData());
                }

                switch (nowBiometric.getBiometricType()){
                    case PHOTO:
                        nowBiometricEO.setPhoto(nowBiometric.getBiometricData());
                        break;
                    case LT:
                        nowBiometricEO.setWsqLt(nowBiometric.getBiometricData());
                        break;
                    case LI:
                        nowBiometricEO.setWsqLi(nowBiometric.getBiometricData());
                        break;
                    case LM:
                        nowBiometricEO.setWsqLm(nowBiometric.getBiometricData());
                        break;
                    case LR:
                        nowBiometricEO.setWsqLr(nowBiometric.getBiometricData());
                        break;
                    case LL:
                        nowBiometricEO.setWsqLs(nowBiometric.getBiometricData());
                        break;
                    case RT:
                        nowBiometricEO.setWsqRt(nowBiometric.getBiometricData());
                        break;
                    case RI:
                        nowBiometricEO.setWsqRi(nowBiometric.getBiometricData());
                        break;
                    case RM:
                        nowBiometricEO.setWsqRm(nowBiometric.getBiometricData());
                        break;
                    case RR:
                        nowBiometricEO.setWsqRr(nowBiometric.getBiometricData());
                        break;
                    case RL:
                        nowBiometricEO.setWsqRs(nowBiometric.getBiometricData());
                        break;
                }

            }
        }

        return nowBiometricEO;
    }
    public Address prepareAddressEntity(String appId, com.kit.integrationmanager.model.Address addressBO){
        Address addressEO = new Address();
        addressEO.setApplicationId(appId);
        //addressEO.setAddressLine(addressBO.getAddressLine());
        addressEO.setStateId(addressBO.getStateId());
        addressEO.setCountyId(addressBO.getCountyId());
        addressEO.setPayam(addressBO.getPayam());
        addressEO.setBoma(addressBO.getBoma());
        return addressEO;
    }
    public Location prepareLocationEntity(String appId,com.kit.integrationmanager.model.Location locationBO){
        Location locationEO = new Location();
        locationEO.setLat(locationBO.getLat());
        locationEO.setLon(locationBO.getLon());
        //locationEO.setApplicationId(appId);
        return locationEO;
    }
    public Alternate prepareAlternateEntity(String appId,com.kit.integrationmanager.model.AlternatePayee alternateBO){
        Alternate alternateEO = new Alternate();
        alternateEO.setApplicationId(appId);
        alternateEO.setPayeeFirstName(alternateBO.getPayeeFirstName());
        alternateEO.setPayeeMiddleName(alternateBO.getPayeeMiddleName());
        alternateEO.setPayeeLastName(alternateBO.getPayeeLastName());
        alternateEO.setPayeeNickName(alternateBO.getPayeeNickName());
        alternateEO.setPayeeGender(alternateBO.getPayeeGender() != null ? Long.valueOf(alternateBO.getPayeeGender().ordinal()) : null);
        alternateEO.setPayeeAge(alternateBO.getPayeeAge());
        alternateEO.setDocumentType(alternateBO.getDocumentType() != null ? Long.valueOf(alternateBO.getDocumentType().ordinal()) : null);
        alternateEO.setDocumentTypeOther(alternateBO.getDocumentTypeOther());
        alternateEO.setNationalId(alternateBO.getNationalId());
        alternateEO.setPayeePhoneNo(alternateBO.getPayeePhoneNo());
        return alternateEO;
    }
    public List<Nominee> prepareNomineeEntity(String appId,List<com.kit.integrationmanager.model.Nominee> nomineeList){
        List<Nominee> nominees = new ArrayList<>();

        for(com.kit.integrationmanager.model.Nominee nominee : nomineeList){
            Nominee nomineeEO = new Nominee();

            nomineeEO.setApplicationId(appId);
            nomineeEO.setNomineeFirstName(nominee.getNomineeFirstName());
            nomineeEO.setNomineeMiddleName(nominee.getNomineeMiddleName());
            nomineeEO.setNomineeLastName(nominee.getNomineeLastName());
            nomineeEO.setNomineeNickName(nominee.getNomineeNickName());
            nomineeEO.setRelationshipWithHouseholdHead(nominee.getRelationshipWithHouseholdHead() != null ? Long.valueOf(nominee.getRelationshipWithHouseholdHead().ordinal()) : null);
            nomineeEO.setNomineeAge(nominee.getNomineeAge());
            nomineeEO.setNomineeGender(nominee.getNomineeGender() != null ? Long.valueOf(nominee.getNomineeGender().ordinal()) : null);
            nomineeEO.setReadWrite(nominee.getIsReadWrite());
            nomineeEO.setNomineeOccupation(nominee.getNomineeOccupation() != null ? Long.valueOf(nominee.getNomineeOccupation().ordinal()) : null);
            nomineeEO.setOtherOccupation(nominee.getOtherOccupation());
            nominees.add(nomineeEO);
        }
        return nominees;
    }
    public HouseholdInfo prepareHouseholdInfoEntity(String appId,com.kit.integrationmanager.model.HouseholdMember member){
        HouseholdInfo householdInfoEO = new HouseholdInfo();
        householdInfoEO.setApplicationId(appId);
        householdInfoEO.setMaleTotal(member.getTotalMale());
        householdInfoEO.setFemaleTotal(member.getTotalFemale());
        householdInfoEO.setMaleDisable(member.getMaleDisable());
        householdInfoEO.setFemaleDisable(member.getFemaleDisable());
        householdInfoEO.setMaleChronicalIll(member.getMaleChronicalIll());
        householdInfoEO.setFemaleChronicalIll(member.getFemaleChronicalIll());
        householdInfoEO.setFemaleNormal(member.getFemaleNormal());
        return householdInfoEO;
    }
    public List<SelectionReason> prepareSelectionReasonEntity(String appId, List<Integer> reasons){
        List<SelectionReason> selectionReasons = new ArrayList<>();
        for(Integer nowReason:reasons){
            SelectionReason nowSelectionReason = new SelectionReason();
            nowSelectionReason.setApplicationId(appId);
            nowSelectionReason.setSelectionReasonId(Integer.toUnsignedLong(nowReason));
            selectionReasons.add(nowSelectionReason);
        }
        return selectionReasons;
    }
    public Beneficiary prepareBeneficiaryEntity(String appId,com.kit.integrationmanager.model.Beneficiary beneficiaryBO){
        Beneficiary beneficiaryEO = new Beneficiary();
        if(beneficiaryBO!=null) {
            beneficiaryEO.setApplicationId(appId);
            beneficiaryEO.setApplicationId(beneficiaryBO.getApplicationId());

            beneficiaryEO.setRespondentFirstName(beneficiaryBO.getRespondentFirstName());
            beneficiaryEO.setRespondentMiddleName(beneficiaryBO.getRespondentMiddleName());
            beneficiaryEO.setRespondentLastName(beneficiaryBO.getRespondentLastName());
            beneficiaryEO.setRespondentNickName(beneficiaryBO.getRespondentNickName());

            beneficiaryEO.setSpouseFirstName(beneficiaryBO.getSpouseFirstName());
            beneficiaryEO.setSpouseMiddleName(beneficiaryBO.getSpouseMiddleName());
            beneficiaryEO.setSpouseLastName(beneficiaryBO.getSpouseLastName());
            beneficiaryEO.setSpouseNickName(beneficiaryBO.getSpouseNickName());

            beneficiaryEO.setRelationshipWithHouseholdHead(beneficiaryBO.getRelationshipWithHouseholdHead() != null ? (long) beneficiaryBO.getRelationshipWithHouseholdHead().ordinal(): null);

            beneficiaryEO.setRespondentAge(beneficiaryBO.getRespondentAge());
            beneficiaryEO.setRespondentGender(beneficiaryBO.getRespondentGender() != null ? (long) beneficiaryBO.getRespondentGender().ordinal() : null);
            beneficiaryEO.setRespondentMaritalStatus(beneficiaryBO.getRespondentMaritalStatus() != null ? (long) beneficiaryBO.getRespondentMaritalStatus().ordinal() : null);
            beneficiaryEO.setRespondentLegalStatus(beneficiaryBO.getRespondentLegalStatus() != null ? (long) beneficiaryBO.getRespondentLegalStatus().ordinal() : null);

            beneficiaryEO.setDocumentType(beneficiaryBO.getDocumentType() != null ? (long) beneficiaryBO.getDocumentType().ordinal() : null);
            beneficiaryEO.setDocumentTypeOther(beneficiaryBO.getDocumentTypeOther());

            beneficiaryEO.setRespondentId(beneficiaryBO.getRespondentId());
            beneficiaryEO.setRespondentPhoneNo(beneficiaryBO.getRespondentPhoneNo());
            beneficiaryEO.setHouseholdIncomeSource(beneficiaryBO.getHouseholdIncomeSource() != null ? (long) beneficiaryBO.getHouseholdIncomeSource().ordinal() : null);
            beneficiaryEO.setHouseholdMonthlyAvgIncome(beneficiaryBO.getHouseholdMonthlyAvgIncome());
            beneficiaryEO.setHouseholdSize(beneficiaryBO.getHouseholdSize());
            beneficiaryEO.setOtherMemberPerticipating(beneficiaryBO.getIsOtherMemberPerticipating());

            beneficiaryEO.setReadWrite(beneficiaryBO.getIsReadWrite());
            beneficiaryEO.setMemberReadWrite(beneficiaryBO.getMemberReadWrite());
            beneficiaryEO.setNotPerticipationReason(beneficiaryBO.getNotPerticipationReason() != null ? (long) beneficiaryBO.getNotPerticipationReason().ordinal() : null);
            beneficiaryEO.setNotPerticipationOtherReason(beneficiaryBO.getNotPerticipationOtherReason());

            beneficiaryEO.setCreatedBy(beneficiaryBO.getCreatedBy());
            beneficiaryEO.setSelectionCriteria(beneficiaryBO.getSelectionCriteria() != null ? (long) beneficiaryBO.getSelectionCriteria().ordinal() : null);

            beneficiaryEO.setCurrency(beneficiaryBO.getCurrency() != null ? (long) beneficiaryBO.getCurrency().ordinal() : null);
        }

        return beneficiaryEO;
    }
}