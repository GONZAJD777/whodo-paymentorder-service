package com.example.whodo_paymentorder_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
public class MongoReactiveConfig {

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(@Value("${spring.data.mongodb.uri}") String mongoConnUri,
                                                       @Value("${spring.data.mongodb.database}") String mongoDBName) {

        com.mongodb.reactivestreams.client.MongoClient mMongoClient =MongoClients.create(mongoConnUri);
        return new ReactiveMongoTemplate(new SimpleReactiveMongoDatabaseFactory(mMongoClient, mongoDBName)
        );
    }
}