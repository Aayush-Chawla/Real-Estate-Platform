package com.example.appointmentservice.dto;

import com.example.appointmentservice.entity.Appointment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponse {

    private UUID id;
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
