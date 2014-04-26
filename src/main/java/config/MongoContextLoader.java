package config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Created by isurud on 3/9/14.
 */
public class MongoContextLoader {

    ApplicationContext context;
    MongoOperations mongoOperation;

    public MongoContextLoader() {
        context = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
        mongoOperation = (MongoOperations) context.getBean("mongoTemplate");

    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public MongoOperations getMongoOperation() {
        return mongoOperation;
    }

    public void setMongoOperation(MongoOperations mongoOperation) {
        this.mongoOperation = mongoOperation;
    }
}
