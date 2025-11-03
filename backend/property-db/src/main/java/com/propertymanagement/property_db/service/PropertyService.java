package com.propertymanagement.property_db.service;

import com.propertymanagement.property_db.model.Property;
import com.propertymanagement.property_db.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    // Create or Update property
    public Property saveProperty(Property property) {
        return propertyRepository.save(property);
    }

    // Get all properties
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // Get property by ID
    public Optional<Property> getPropertyById(String id) {
        return propertyRepository.findById(id);
    }

    // Delete property by ID
    public void deleteProperty(String id) {
        propertyRepository.deleteById(id);
    }
}
