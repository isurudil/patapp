package util;

import com.meda.model.dto.AppointmentDetails;
import com.meda.model.dto.DoctorRegistrationDetails;

/**
 * Created by isurud on 4/27/14.
 */
public class MessageExchanger {

    public static String getSchdleSuccessMsg(AppointmentDetails appointmentDetails) {
        String text = appointmentDetails.getTitle() + " " + appointmentDetails.getpName() + ", your appointment to Dr." + appointmentDetails.getdName() +
                " for the " + appointmentDetails.getClinicType() + " clinic has been scheduled on " + appointmentDetails.getAppointmentDate() +
                ". -- A project by I.D Ranaweera - USJP - AS2009500";
        return text;
    }

    public static String getRegSuccessMsg(DoctorRegistrationDetails doctorRegistrationDetails, AppointmentDetails appointmentDetails) {

        return "Dr." + doctorRegistrationDetails.getdName() + ", you have been successfully registered " +
                "with the " + appointmentDetails.getClinicType() + " clinic appointment of " + appointmentDetails.getTitle() + "" + appointmentDetails.getpName() + " on "
                + appointmentDetails.getAppointmentDate() + " -- A project by I.D Ranaweera - USJP - AS2009500 ";
    }

    public static String getChngReqSuccessMsg(AppointmentDetails appointmentDetails) {
        return appointmentDetails.getTitle() + " " + appointmentDetails.getpName() + ", Your request to change the appointment date for the " +
                " " + appointmentDetails.getClinicType() + " clinic of Dr." + appointmentDetails.getdName() + " on " + appointmentDetails.getAppointmentDate() +
                " is sent for the approval. Please await for the confirmation -- A project by I.D Ranaweera - USJP - AS2009500 ";
    }

    public static String getCnclSuccessMsg(AppointmentDetails appointmentDetails) {
        return appointmentDetails.getTitle() + " " + appointmentDetails.getpName() + ", your appointment to Dr." + appointmentDetails.getdName() + " " +
                " on " + appointmentDetails.getAppointmentDate() + " for the " + appointmentDetails.getClinicType() + " clinic has been cancelled. " +
                "-- A project by I.D Ranaweera - USJP - AS2009500";
    }



}
