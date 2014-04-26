package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.AppointmentDetails;
import config.MongoContextLoader;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by isurud on 4/26/14.
 */
public class DeleteAppointment {

    static boolean isSuccess=true;

    MongoContextLoader mongoContextLoader =   new MongoContextLoader();

    private static final Logger LOGGER = Logger.getLogger(GetAppointmentDetails.class);
    AppointmentDetails appointmentDetails;
    AppointmentDetails returnObject;

    public static boolean getIsSuccess() {
        return isSuccess;
    }

    public static void setIsSuccess(boolean isSuccess) {
        DeleteAppointment.isSuccess = isSuccess;
    }

    public AppointmentDetails deleteAppointment(String appCode){

        String details;

        Gson gson = new Gson();
        Query searchUserQuery = new Query(Criteria.where("appointmentCode").is(appCode));

        LOGGER.debug("Getting patient details to Delete : " + appCode);

        try{
            MongoOperations mongoOperations = mongoContextLoader.getMongoOperation();
            appointmentDetails = mongoOperations.findOne(searchUserQuery, AppointmentDetails.class);
            details= gson.toJson(appointmentDetails);
            LOGGER.info("Got Details from DB to Delete : " + details);
            returnObject = appointmentDetails;
            mongoOperations.remove(appointmentDetails);
            isSuccess =true;
        }catch (Exception ex){
             isSuccess = false;
        }
        return returnObject;
    }

}
