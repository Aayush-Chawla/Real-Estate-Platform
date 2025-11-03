package com.propertymanagement.property_db.controller;

import com.propertymanagement.property_db.model.Property;
import com.propertymanagement.property_db.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    // ✅ Create or Update property
    @PostMapping
    public Property addProperty(@RequestBody Property property) {
        return propertyService.saveProperty(property);
    }

    // ✅ Get all properties
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    // ✅ Get property by ID
    @GetMapping("/{id}")
    public Optional<Property> getPropertyById(@PathVariable String id) {
        return propertyService.getPropertyById(id);
    }

    // ✅ Delete property by ID
    @DeleteMapping("/{id}")
    public String deleteProperty(@PathVariable String id) {
        propertyService.deleteProperty(id);
        return "Property deleted with id: " + id;
    }
}
