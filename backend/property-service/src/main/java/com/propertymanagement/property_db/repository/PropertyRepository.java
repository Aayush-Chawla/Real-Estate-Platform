package com.propertymanagement.property_db.repository;

import com.propertymanagement.property_db.model.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
    // You can later add custom queries here if needed
}
