package com.real_estate.appointmentservice.repository;

import com.real_estate.appointmentservice.entity.Appointment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends MongoRepository<Appointment, ObjectId> {
    List<Appointment> findByBuyerId(UUID buyerId);
    List<Appointment> findBySellerId(UUID sellerId);
//    Optional<Appointment> findByIdTimestamp(Long timestamp);
}
