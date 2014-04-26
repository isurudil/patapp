package config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * Created by isurud on 3/8/14.
 */
public class SpringMongoConfig {

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception{
        return  new SimpleMongoDbFactory(new MongoClient("localhost",27017),"medic");
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception{

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return  mongoTemplate;

    }
}
