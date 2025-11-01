package com.real_estate.appointmentservice.dto;

import com.real_estate.appointmentservice.entity.Appointment;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponse {

    private ObjectId id;
    private UUID buyerId;
    private UUID sellerId;
    private UUID propertyId;
    private LocalDateTime scheduledAt;
    private Appointment.Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime canceledAt;
}
