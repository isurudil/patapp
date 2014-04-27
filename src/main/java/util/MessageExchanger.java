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

    public static String getAppointmntNotExixtMsg(){
        return "This appointment has not registered with the system -- A project by I.D Ranaweera - USJP - AS2009500  ";
    }

    public static String getDocNotRegMsgforDoc(){
        return "You are not registered with the system. -- A project by I.D Ranaweera - USJP - AS2009500";
    }

    public static String getDocNotRegforPatient(){
        return "Your Doctor has not registered to the SMS service. -- A project by I.D Ranaweera - USJP - AS2009500  ";
    }

    public static String getDocNotRegForAppmntMsg(){
        return "You are not registered to any appointment. -- A project by I.D Ranaweera - USJP - AS2009500 ";
    }

    public static String getHelpMsg(){
        return "Type ' med view <appointment code> ' to view your registered appointments. \n " +
                "Type ' med change <appointment code> ' to request to change your appointment to next clinic date. \n" +
                "Type ' med cancel <appointment code> ' to cancel your appointment.  -- A project by I.D Ranaweera - USJP - AS2009500";
    }

    public static String getInvalidFormatMsg() {
        return "Invalid Format. Please resend the message as ' med <action> <appointment code> '. " +
                "-- A project by I.D Ranaweera - USJP - AS2009500";
    }

    public static String getInvalidActionMsg(){
        return "Invalid action. Please check the action and resend. Type ' med help ' for instructions. " +
                "-- A project by I.D Ranaweera - USJP - AS2009500 ";
    }

    public static String getCannotRescheduleMsg(AppointmentDetails appointmentDetails, String dName){
        return "Dr."+dName+", the patient " + appointmentDetails.getTitle()+""+appointmentDetails.getpName()+" has not requested for " +
                "a change of the appointment date.  -- A project by I.D Ranaweera - USJP - AS2009500";
    }

    public static String reschdlSuccessPatientMsg(AppointmentDetails appointmentDetails){

        return appointmentDetails.getTitle()+""+appointmentDetails.getpName()+ ", your request to change the appointment for the" +
                " "+appointmentDetails.getClinicType()+" clinic of Dr."+appointmentDetails.getdName()+" is approved. The new date for your appointment is on " +
                ""+appointmentDetails.getAppointmentDate()+". -- A project by I.D Ranaweera - USJP - AS2009500 ";

    }

    public static String reschdlSuccessDocInfrmMsg(AppointmentDetails appointmentDetails, DoctorRegistrationDetails doctorRegistrationDetails){
        return "Dr."+doctorRegistrationDetails.getdName()+", your approval has been informed to "+appointmentDetails.getTitle()+""
                +appointmentDetails.getpName()+" -- A project by I.D Ranaweera - USJP - AS2009500";
    }
}
