package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.AppointmentDetails;
import config.MongoContextLoader;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Created by isurud on 4/27/14.
 */
public class UpdateAppointment {

    String status;
    String appCode;

    public UpdateAppointment(String status, String appCode) {
        this.status = status;
        this.appCode = appCode;
    }

    MongoContextLoader mongoContextLoader = new MongoContextLoader();
    private static final Logger LOGGER = Logger.getLogger(UpdateAppointment.class);

    public AppointmentDetails updateAppointmentStatus() {
         String details;
        Gson gson = new Gson();
        Query updateAppointmentDetails = new Query(Criteria.where("_id").is(appCode));
        AppointmentDetails appointmentDetails = null;

        try {
            MongoOperations mongoOperations = mongoContextLoader.getMongoOperation();
            Update update = new Update();
            mongoOperations.updateFirst(updateAppointmentDetails, update.set("status", status), AppointmentDetails.class);
            appointmentDetails = mongoOperations.findOne(updateAppointmentDetails, AppointmentDetails.class);
            details = gson.toJson(appointmentDetails);
            LOGGER.info("Got Details from DB After Updating  : " + details);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return appointmentDetails;
    }

    public AppointmentDetails updateAppointmentDate(String date){

        String details;
        Gson gson = new Gson();
        Query updateAppointmentDetails = new Query(Criteria.where("_id").is(appCode));
        AppointmentDetails appointmentDetails = null;

        try {
            MongoOperations mongoOperations = mongoContextLoader.getMongoOperation();
            Update update = new Update();
            mongoOperations.updateFirst(updateAppointmentDetails, update.set("appointmentDate", date), AppointmentDetails.class);
            mongoOperations.updateFirst(updateAppointmentDetails, update.set("status", status), AppointmentDetails.class);
            appointmentDetails = mongoOperations.findOne(updateAppointmentDetails, AppointmentDetails.class);
            details = gson.toJson(appointmentDetails);
            LOGGER.info("Got Details from DB After Updating  : " + details);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return appointmentDetails;

    }
}
