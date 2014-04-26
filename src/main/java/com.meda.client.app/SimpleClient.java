/*
 *   (C) Copyright 1996-${year} hSenid Software International (Pvt) Limited.
 *   All Rights Reserved.
 *
 *   These materials are unpublished, proprietary, confidential source code of
 *   hSenid Software International (Pvt) Limited and constitute a TRADE SECRET
 *   of hSenid Software International (Pvt) Limited.
 *
 *   hSenid Software International (Pvt) Limited retains all title to and intellectual
 *   property rights in these materials.
 *
 */
package com.meda.client.app;

import com.meda.client.services.DeleteAppointment;
import com.meda.client.services.GetAppointmentDetails;
import com.meda.client.services.InsertPatientSourceAddress;
import com.meda.model.dto.AppointmentDetails;
import com.meda.model.dto.RegistrationDetails;
import hms.kite.samples.api.StatusCodes;
import hms.kite.samples.api.sms.MoSmsListener;
import hms.kite.samples.api.sms.SmsRequestSender;
import hms.kite.samples.api.sms.messages.MoSmsReq;
import hms.kite.samples.api.sms.messages.MtSmsReq;
import hms.kite.samples.api.sms.messages.MtSmsResp;

import javax.servlet.ServletConfig;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleClient implements MoSmsListener {

    private final static Logger LOGGER = Logger.getLogger(SimpleClient.class.getName());
    String action;
    String appointmentCode;

    @Override
    public void init(ServletConfig servletConfig) {

    }

    @Override
    public void onReceivedSms(MoSmsReq moSmsReq) {
        MtSmsReq mtSmsReq = new MtSmsReq();
        try {
            LOGGER.info("Sms Received for generate request : " + moSmsReq);
            System.out.println(moSmsReq);
            SmsRequestSender smsMtSender = new SmsRequestSender(new URL("http://localhost:7000/sms/send"));
            String[] messageContent = moSmsReq.getMessage().split(" ");
            if (messageContent.length <= 1) {
                mtSmsReq.setMessage("Invalid Format. Please resend the message as ' med <action> <appointment code> '. " +
                        "-- A project by I.D Ranaweera - USJP - AS2009500");

            } else if (messageContent.length == 2 && messageContent[1].equals("help")) {
                mtSmsReq.setMessage("Type ' med view <appointment code> ' to view your registered appointments. \n " +
                        "Type ' med change <appointment code> ' to request to change your appointment to next clinic date. \n" +
                        "Type ' med cancel <appointment code> ' to cancel your appointment");

            } else if (messageContent.length == 2 && !messageContent[1].equals("help")) {
                mtSmsReq.setMessage("Invalid Format. Please resend the message as ' med <action> <appointment code> '. " +
                        "-- A project by I.D Ranaweera - USJP - AS2009500");
            } else {

                action = messageContent[1];
                appointmentCode = messageContent[2];
                mtSmsReq = createSimpleMtSms(moSmsReq);


            }
            mtSmsReq.setApplicationId(moSmsReq.getApplicationId());
            mtSmsReq.setSourceAddress("MEDIC");// default sender address or aliases
            mtSmsReq.setPassword("96f00de1f3501e429a35bd5e58fec963");

            mtSmsReq.setVersion(moSmsReq.getVersion());
            List<String> addressList = new ArrayList<String>();
            addressList.add(moSmsReq.getSourceAddress());
            mtSmsReq.setDestinationAddresses(addressList);

            MtSmsResp mtSmsResp = smsMtSender.sendSmsRequest(mtSmsReq);
            String statusCode = mtSmsResp.getStatusCode();
            String statusDetails = mtSmsResp.getStatusDetail();
            if (StatusCodes.SuccessK.equals(statusCode)) {
                LOGGER.info("MT SMS message successfully sent");
            } else {
                LOGGER.info("MT SMS message sending failed with status code [" + statusCode + "] " + statusDetails);
            }


        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Unexpected error occurred", e);
        }
    }

    private MtSmsReq createSimpleMtSms(MoSmsReq moSmsReq) {

        MtSmsReq mtSmsReq = new MtSmsReq();
        if (action.equals("view")) {
            AppointmentDetails appointmentDetails = new GetAppointmentDetails().getAppointmentDetails(appointmentCode);

            if (appointmentDetails != null) {
                mtSmsReq.setMessage(formatText(appointmentDetails));
            } else {
                mtSmsReq.setMessage(" There are no appointments registered with this appointment code " +
                        "-- A project by I.D Ranaweera - USJP - AS2009500 ");
            }
        } else if (action.equals("cancel")) {
            DeleteAppointment deleteAppointment = new DeleteAppointment();

            LOGGER.log(Level.INFO,"Executing deleting appoinment details");
            AppointmentDetails appointmentDetails = deleteAppointment.deleteAppointment(appointmentCode);

                if(appointmentDetails != null){
                     mtSmsReq.setMessage("Dear "+appointmentDetails.getpName()+", your appointment to Dr."+appointmentDetails.getdName()+" " +
                             " on "+appointmentDetails.getAppointmentDate()+" for the "+appointmentDetails.getClinicType()+" clinic has been cancelled. " +
                             "-- A project by I.D Ranaweera - USJP - AS2009500");
                }else {
                    mtSmsReq.setMessage("You are not registered to any clinic. -- A project by I.D Ranaweera - USJP - AS2009500 ");
                }
        }else if(action.equals("change")){
            RegistrationDetails registrationDetails = new InsertPatientSourceAddress().insertPatientDestination(moSmsReq,appointmentCode);
            if(registrationDetails != null){
                AppointmentDetails appointmentDetails = new GetAppointmentDetails().getAppointmentDetails(appointmentCode);
                mtSmsReq.setMessage("Dear "+appointmentDetails.getpName()+", Your request to change the appointment date for the " +
                        " "+appointmentDetails.getClinicType()+" clinic of Dr."+appointmentDetails.getdName()+" on "+appointmentDetails.getAppointmentDate() +
                        " is sent for the approval. Please await ... -- A project by I.D Ranaweera - USJP - AS2009500 ");
            }else {
                mtSmsReq.setMessage("Your Doctor has not registered to the SMS service. -- A project by I.D Ranaweera - USJP - AS2009500  ");
            }
        }
        else {
            mtSmsReq.setMessage("Invalid action. Please check the action and resend. Type ' med help ' for instructions. " +
                    "-- A project by I.D Ranaweera - USJP - AS2009500 ");
        }
        return mtSmsReq;
    }

    public String formatText(AppointmentDetails appointmentDetails) {
        String text = "Dear " + appointmentDetails.getpName() + ", your appointment to Dr." + appointmentDetails.getdName() +
                " for the " + appointmentDetails.getClinicType() + " clinic has been scheduled on " + appointmentDetails.getAppointmentDate() +
                ". -- A project by I.D Ranaweera - USJP - AS2009500";
        return text;
    }


}