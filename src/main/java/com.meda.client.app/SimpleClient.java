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

import com.meda.client.services.*;
import com.meda.model.dto.AppointmentDetails;
import com.meda.model.dto.DoctorRegistrationDetails;
import com.meda.model.dto.PatientRegistrationDetails;
import util.AppointmentStatus;
import util.MessageExchanger;
import hms.kite.samples.api.SdpException;
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
    String doctorCode;
    MtSmsReq mtSmsReq = new MtSmsReq();

    @Override
    public void init(ServletConfig servletConfig) {

    }

    @Override
    public void onReceivedSms(MoSmsReq moSmsReq) {

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
                if (messageContent.length == 4) {
                    doctorCode = messageContent[3];
                }
                mtSmsReq = createSimpleMtSms(moSmsReq, smsMtSender);


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

    private MtSmsReq createSimpleMtSms(MoSmsReq moSmsReq, SmsRequestSender smsMtSender) {

        MtSmsReq mtSmsReq = new MtSmsReq();
        if (action.equals("view")) {
            GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
            getAppointmentDetails.setAppCode(appointmentCode);
            AppointmentDetails appointmentDetails = getAppointmentDetails.getAppointmentDetails();

            if (appointmentDetails != null) {
                mtSmsReq.setMessage(MessageExchanger.getSchdleSuccessMsg(appointmentDetails));
            } else {
                mtSmsReq.setMessage(" There are no appointments registered with this appointment code " +
                        "-- A project by I.D Ranaweera - USJP - AS2009500 ");
            }
        } else if (action.equals("cancel")) {
            DeleteAppointment deleteAppointment = new DeleteAppointment();

            LOGGER.log(Level.INFO, "Executing deleting appoinment details");
            AppointmentDetails appointmentDetails = deleteAppointment.deleteAppointment(appointmentCode);

            if (appointmentDetails != null) {
                mtSmsReq.setMessage(MessageExchanger.getCnclSuccessMsg(appointmentDetails));
            } else {
                mtSmsReq.setMessage("You are not registered to any clinic. -- A project by I.D Ranaweera - USJP - AS2009500 ");
            }
        } else if (action.equals("change")) {
            //Returns the updated document
            GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
            UpdateAppointmentStatus updateAppointmentStatus = new UpdateAppointmentStatus(AppointmentStatus.PENDING_RESCHEDULE.name(),appointmentCode);
            getAppointmentDetails.setAppCode(appointmentCode);
            AppointmentDetails appointmentDetails = getAppointmentDetails.getAppointmentDetails();
            if (appointmentDetails != null) {
                // updates the patient_registration document with current source address. If the doc does not has the record returns null
                PatientRegistrationDetails patientRegistrationDetails = new InsertPatientSourceAddress().insertPatientDestination(moSmsReq, appointmentCode);
                if (patientRegistrationDetails != null) {
                    appointmentDetails = updateAppointmentStatus.updateAppointmentStatus();
                    getAppointmentDetails.setAppCode(appointmentCode);
                    mtSmsReq.setMessage(MessageExchanger.getChngReqSuccessMsg(appointmentDetails));
                    fireDoctorApprovalMsg(smsMtSender,patientRegistrationDetails,appointmentDetails);


                } else {
                    // response null means it does not have a record. A record is created when a doctor register to an existing appointment
                    mtSmsReq.setMessage("Your Doctor has not registered to the SMS service. -- A project by I.D Ranaweera - USJP - AS2009500  ");
                }
            } else {
                mtSmsReq.setMessage("This appointment has not registered with the system -- A project by I.D Ranaweera - USJP - AS2009500  ");
            }

        } else if (action.equals("reg")) {
            InsertDoctorSourceAddress insertDoctorSourceAddress = new InsertDoctorSourceAddress(moSmsReq, appointmentCode, doctorCode);
            GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
            AppointmentDetails appointmentDetails;
            getAppointmentDetails.setAppCode(appointmentCode);
            appointmentDetails = getAppointmentDetails.getAppointmentDetails();

            if (appointmentDetails != null) {
                DoctorRegistrationDetails doctorRegistrationDetails = insertDoctorSourceAddress.findDoctor();
                if (doctorRegistrationDetails != null) {

                    insertDoctorSourceAddress.insertDoctorSource();
                    mtSmsReq.setMessage(MessageExchanger.getRegSuccessMsg(doctorRegistrationDetails, appointmentDetails));
                } else {
                    mtSmsReq.setMessage("You are not registered with the system. -- A project by I.D Ranaweera - USJP - AS2009500");
                }
            } else {
//                mtSmsReq.setMessage("You are not registered with this appointment. Please send" +
//                        "' med reg <doctor code> <appointment code>' to register with the appointment -- A project by I.D Ranaweera - USJP - AS2009500");
                mtSmsReq.setMessage("The appointment does not exist. Please check the appointment id.  -- A project by I.D Ranaweera - USJP - AS2009500");
            }
        } else {
            mtSmsReq.setMessage("Invalid action. Please check the action and resend. Type ' med help ' for instructions. " +
                    "-- A project by I.D Ranaweera - USJP - AS2009500 ");
        }
        return mtSmsReq;
    }

    public void fireAtlernateMsg(SmsRequestSender smsMtSender, String dest, Object obj) {

        List<String> addressList = new ArrayList<String>();
        addressList.add(dest);
        mtSmsReq.setDestinationAddresses(addressList);
        PatientRegistrationDetails patientRegistrationDetails = (PatientRegistrationDetails) obj;

        mtSmsReq.setMessage("Dr."+patientRegistrationDetails.getdName());
        MtSmsResp mtSmsResp = null;
        try {
            mtSmsResp = smsMtSender.sendSmsRequest(mtSmsReq);
        } catch (SdpException e) {
            e.printStackTrace();
        }
        String statusCode = mtSmsResp.getStatusCode();
        String statusDetails = mtSmsResp.getStatusDetail();
        if (StatusCodes.SuccessK.equals(statusCode)) {
            LOGGER.info("MT SMS message successfully sent");
        } else {
            LOGGER.info("MT SMS message sending failed with status code [" + statusCode + "] " + statusDetails);
        }
    }

    public void fireDoctorApprovalMsg(SmsRequestSender smsMtSender,PatientRegistrationDetails registrationDetails
                                       ,AppointmentDetails appointmentDetails) {

        List<String> addressList = new ArrayList<String>();
        addressList.add(registrationDetails.getdDestination());
        mtSmsReq.setDestinationAddresses(addressList);

        mtSmsReq.setMessage("Dr."+registrationDetails.getdName()+", you patient "+appointmentDetails.getTitle()+"" +
                ""+appointmentDetails.getpName()+" is requesting to change his date of the "+appointmentDetails.getClinicType()+"" +
                " clinic on "+appointmentDetails.getAppointmentDate()+". Please reply with the appointment number : "+appointmentDetails.getAppointmentCode()+"" +
                " -- A project by I.D Ranaweera - USJP - AS2009500");
        MtSmsResp mtSmsResp = null;
        try {
            mtSmsResp = smsMtSender.sendSmsRequest(mtSmsReq);
        } catch (SdpException e) {
            e.printStackTrace();
        }
        String statusCode = mtSmsResp.getStatusCode();
        String statusDetails = mtSmsResp.getStatusDetail();
        if (StatusCodes.SuccessK.equals(statusCode)) {
            LOGGER.info("MT SMS message successfully sent");
        } else {
            LOGGER.info("MT SMS message sending failed with status code [" + statusCode + "] " + statusDetails);
        }
    }


}