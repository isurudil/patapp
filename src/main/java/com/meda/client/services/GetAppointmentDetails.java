package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.AppointmentDetails;
import config.MongoContextLoader;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Created by isurud on 4/26/14.
 */
@Component
public class GetAppointmentDetails {

    String appCode;

    MongoContextLoader mongoContextLoader =   new MongoContextLoader();
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(GetAppointmentDetails.class.getName());
    AppointmentDetails appointmentDetails;
    String details;

    public void setAppCode(String appCode){
         this.appCode = appCode;
    }

    public AppointmentDetails getAppointmentDetails(){
        Gson gson = new Gson();
        Query searchUserQuery = new Query(Criteria.where("_id").is(appCode));

        LOGGER.info("Getting patient details : " + appCode);

        try{
        appointmentDetails = mongoContextLoader.getMongoOperation().findOne(searchUserQuery,AppointmentDetails.class);
         details= gson.toJson(appointmentDetails);
        LOGGER.info("Got Details from DB : "+details);
    }catch (Exception ex){
            ex.printStackTrace();
    }
        return appointmentDetails;
    }

}
