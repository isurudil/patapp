package com.meda.model.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by isurud on 4/26/14.
 */
@Document(collection = "patient_details")
public class AppointmentDetails {

    @Id
    String appointmentCode;
    String pName;
    String appointmentDate;
    String clinicType;
    String dCode;
    String dName;
    String title;

    public String getTitle() {
        return title;
    }

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public void setAppointmentCode(String appointmentCode) {
        this.appointmentCode = appointmentCode;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getClinicType() {
        return clinicType;
    }

    public void setClinicType(String clinicType) {
        this.clinicType = clinicType;
    }

    public String getdCode() {
        return dCode;
    }

    public void setdCode(String dCode) {
        this.dCode = dCode;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getpName() {

        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }


}
