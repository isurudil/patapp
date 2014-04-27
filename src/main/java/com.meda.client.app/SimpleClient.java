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
import util.DateUtil;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                mtSmsReq.setMessage(MessageExchanger.getInvalidFormatMsg());

            } else if (messageContent.length == 2 && messageContent[1].equalsIgnoreCase("help")) {
                mtSmsReq.setMessage(MessageExchanger.getHelpMsg());

            } else if (messageContent.length == 2 && !messageContent[1].equalsIgnoreCase("help")) {
                mtSmsReq.setMessage(MessageExchanger.getInvalidFormatMsg());
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
        if (action.equalsIgnoreCase("view")) {
            GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
            getAppointmentDetails.setAppCode(appointmentCode);
            AppointmentDetails appointmentDetails = getAppointmentDetails.getAppointmentDetails();

            if (appointmentDetails != null) {
                mtSmsReq.setMessage(MessageExchanger.getSchdleSuccessMsg(appointmentDetails));
            } else {
                mtSmsReq.setMessage(MessageExchanger.getAppointmntNotExixtMsg());
            }
        } else if (action.equalsIgnoreCase("cancel")) {
            DeleteAppointment deleteAppointment = new DeleteAppointment();

            LOGGER.log(Level.INFO, "Executing deleting appoinment details");
            AppointmentDetails appointmentDetails = deleteAppointment.deleteAppointment(appointmentCode);

            if (appointmentDetails != null) {
                mtSmsReq.setMessage(MessageExchanger.getCnclSuccessMsg(appointmentDetails));
            } else {
                mtSmsReq.setMessage(MessageExchanger.getDocNotRegForAppmntMsg());
            }
        } else if (action.equalsIgnoreCase("change")) {
            //Returns the updated document
            GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
            UpdateAppointment updateAppointment = new UpdateAppointment(AppointmentStatus.PENDING_RESCHEDULE.name(), appointmentCode);
            getAppointmentDetails.setAppCode(appointmentCode);
            AppointmentDetails appointmentDetails = getAppointmentDetails.getAppointmentDetails();
            if (appointmentDetails != null) {
                // updates the patient_registration document with current source address. If the doc does not has the record returns null
                PatientRegistrationDetails patientRegistrationDetails = new InsertPatientSourceAddress().insertPatientDestination(moSmsReq, appointmentCode);
                if (patientRegistrationDetails != null) {
                    appointmentDetails = updateAppointment.updateAppointmentStatus();
                    getAppointmentDetails.setAppCode(appointmentCode);
                    mtSmsReq.setMessage(MessageExchanger.getChngReqSuccessMsg(appointmentDetails));
                    fireReqMsgForDoc(smsMtSender, patientRegistrationDetails, appointmentDetails);


                } else {
                    // response null means it does not have a record. A record is created when a doctor register to an existing appointment
                    mtSmsReq.setMessage(MessageExchanger.getDocNotRegforPatient());
                }
            } else {
                mtSmsReq.setMessage(MessageExchanger.getAppointmntNotExixtMsg());
            }

        } else if (action.equalsIgnoreCase("reg")) {
            InsertDoctorSourceAddress insertDoctorSourceAddress = new InsertDoctorSourceAddress(moSmsReq, appointmentCode, doctorCode);
            GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
            AppointmentDetails appointmentDetails;
            getAppointmentDetails.setAppCode(appointmentCode);
            appointmentDetails = getAppointmentDetails.getAppointmentDetails();

            if (appointmentDetails != null) {
                DoctorRegistrationDetails doctorRegistrationDetails = new GetDoctorDetails(doctorCode).findDoctor();
                if (doctorRegistrationDetails != null) {

                    insertDoctorSourceAddress.insertDoctorSource();
                    mtSmsReq.setMessage(MessageExchanger.getRegSuccessMsg(doctorRegistrationDetails, appointmentDetails));
                } else {
                    mtSmsReq.setMessage(MessageExchanger.getDocNotRegMsgforDoc());
                }
            } else {
//                mtSmsReq.setMessage("You are not registered with this appointment. Please send" +
//                        "' med reg <doctor code> <appointment code>' to register with the appointment -- A project by I.D Ranaweera - USJP - AS2009500");
                mtSmsReq.setMessage(MessageExchanger.getAppointmntNotExixtMsg());
            }
        } else if (action.equalsIgnoreCase("ok")) {
            DoctorRegistrationDetails doctorRegistrationDetails = new GetDoctorDetails(doctorCode).findDoctor();
            if (doctorRegistrationDetails != null) {  // doctor has registered with the system
                GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
                getAppointmentDetails.setAppCode(appointmentCode);
                LOGGER.info("Doc registered ");
                AppointmentDetails appointmentDetails = getAppointmentDetails.getAppointmentDetails();
                if (appointmentDetails != null) { // Appointment Exists
                    GetPatientRegistrationDetails getPatientRegistrationDetails = new GetPatientRegistrationDetails(appointmentCode);
                    PatientRegistrationDetails patientRegistrationDetails = getPatientRegistrationDetails.getRegistrationDetails();
                    LOGGER.info("App exists");
                    if (patientRegistrationDetails != null) {
                        LOGGER.info("doctor ref to appmnt");
                        // Doctor has registered to the appointment
                        if (appointmentDetails.getStatus().equals(AppointmentStatus.PENDING_RESCHEDULE.name())) { // appointment is in reschedule state
                            LOGGER.info("in get status");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                LOGGER.info("The number freq " + doctorRegistrationDetails.getClicnicFreq());
                                UpdateAppointment updateAppointment = new UpdateAppointment(AppointmentStatus.NO_RESCHEDULE.name(), appointmentCode);
                                Date currentDate = dateFormat.parse(appointmentDetails.getAppointmentDate());
                                int clinicFreq = Integer.parseInt(doctorRegistrationDetails.getClicnicFreq());
                                Date newDate = DateUtil.addDays(currentDate, clinicFreq);
                                appointmentDetails = updateAppointment.updateAppointmentDate(dateFormat.format(newDate));
                                mtSmsReq.setMessage(MessageExchanger.reschdlSuccessDocInfrmMsg(appointmentDetails, doctorRegistrationDetails));
                                fireSuccessRespMsgForPatient(smsMtSender, patientRegistrationDetails, appointmentDetails);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            // put the date changing code here
                        } else { // user has not requested to change the appointment
                            mtSmsReq.setMessage(MessageExchanger.getCannotRescheduleMsg(appointmentDetails, doctorRegistrationDetails.getdName()));
                        }
                    } else { // doctor has not registered with the appointment
                        mtSmsReq.setMessage(MessageExchanger.getDocNotRegForAppmntMsg());
                    }

                } else {   // Appointment does not exist
                    mtSmsReq.setMessage(MessageExchanger.getAppointmntNotExixtMsg());
                }

            } else {   // doctor has not registered with the system
                mtSmsReq.setMessage(MessageExchanger.getDocNotRegMsgforDoc());
            }
        } else if (action.equalsIgnoreCase("reject")) {

            DoctorRegistrationDetails doctorRegistrationDetails = new GetDoctorDetails(doctorCode).findDoctor();
            if (doctorRegistrationDetails != null) {  // doctor has registered with the system
                GetAppointmentDetails getAppointmentDetails = new GetAppointmentDetails();
                getAppointmentDetails.setAppCode(appointmentCode);
                LOGGER.info("Doc registered ");
                AppointmentDetails appointmentDetails = getAppointmentDetails.getAppointmentDetails();
                if (appointmentDetails != null) { // Appointment Exists
                    GetPatientRegistrationDetails getPatientRegistrationDetails = new GetPatientRegistrationDetails(appointmentCode);
                    PatientRegistrationDetails patientRegistrationDetails = getPatientRegistrationDetails.getRegistrationDetails();
                    LOGGER.info("App exists");
                    if (patientRegistrationDetails != null) {
                        LOGGER.info("doctor ref to appmnt");
                        // Doctor has registered to the appointment
                        if (appointmentDetails.getStatus().equals(AppointmentStatus.PENDING_RESCHEDULE.name())) { // appointment is in reschedule state
                            LOGGER.info("in get status");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            LOGGER.info("The number freq " + doctorRegistrationDetails.getClicnicFreq());
                            UpdateAppointment updateAppointment = new UpdateAppointment(AppointmentStatus.ACTIVE.name(), appointmentCode);
                            appointmentDetails = updateAppointment.updateAppointmentStatus();
                            mtSmsReq.setMessage(MessageExchanger.reschdlSuccessDocInfrmMsg(appointmentDetails, doctorRegistrationDetails));
                            fireRejectRespMsgForPatient(smsMtSender, patientRegistrationDetails, appointmentDetails,doctorRegistrationDetails.getdName());

                            // put the date changing code here
                        } else { // user has not requested to change the appointment
                            mtSmsReq.setMessage(MessageExchanger.getCannotRescheduleMsg(appointmentDetails, doctorRegistrationDetails.getdName()));
                        }
                    } else { // doctor has not registered with the appointment
                        mtSmsReq.setMessage(MessageExchanger.getDocNotRegForAppmntMsg());
                    }

                } else {   // Appointment does not exist
                    mtSmsReq.setMessage(MessageExchanger.getAppointmntNotExixtMsg());
                }

            } else {   // doctor has not registered with the system
                mtSmsReq.setMessage(MessageExchanger.getDocNotRegMsgforDoc());
            }

        } else {
            mtSmsReq.setMessage(MessageExchanger.getInvalidActionMsg());
        }
        return mtSmsReq;
    }

    private void fireRejectRespMsgForPatient(SmsRequestSender smsMtSender, PatientRegistrationDetails patientRegistrationDetails, AppointmentDetails appointmentDetails,String doctorName) {

        List<String> addressList = new ArrayList<String>();
        addressList.add(patientRegistrationDetails.getdDestination());
        mtSmsReq.setDestinationAddresses(addressList);

        mtSmsReq.setMessage(MessageExchanger.reschdlRejectPatientMsg(appointmentDetails, doctorName));
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

    private void fireSuccessRespMsgForPatient(SmsRequestSender smsMtSender, PatientRegistrationDetails registrationDetails
            , AppointmentDetails appointmentDetails) {
        List<String> addressList = new ArrayList<String>();
        addressList.add(registrationDetails.getdDestination());
        mtSmsReq.setDestinationAddresses(addressList);

        mtSmsReq.setMessage(MessageExchanger.reschdlSuccessPatientMsg(appointmentDetails));
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

    public void fireAtlernateMsg(SmsRequestSender smsMtSender, String dest, Object obj) {

        List<String> addressList = new ArrayList<String>();
        addressList.add(dest);
        mtSmsReq.setDestinationAddresses(addressList);
        PatientRegistrationDetails patientRegistrationDetails = (PatientRegistrationDetails) obj;

        mtSmsReq.setMessage("Dr." + patientRegistrationDetails.getdName());
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

    public void fireReqMsgForDoc(SmsRequestSender smsMtSender, PatientRegistrationDetails registrationDetails
            , AppointmentDetails appointmentDetails) {

        List<String> addressList = new ArrayList<String>();
        addressList.add(registrationDetails.getdDestination());
        mtSmsReq.setDestinationAddresses(addressList);

        mtSmsReq.setMessage("Dr." + registrationDetails.getdName() + ", your patient " + appointmentDetails.getTitle() + "" +
                "" + appointmentDetails.getpName() + " is requesting to change his date of the " + appointmentDetails.getClinicType() + "" +
                " clinic on " + appointmentDetails.getAppointmentDate() + ". Please reply with the appointment number : " + appointmentDetails.getAppointmentCode() + "" +
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