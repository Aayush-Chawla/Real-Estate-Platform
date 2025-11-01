package com.real_estate.appointmentservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AppointmentRequest {

    @NotNull
    private UUID buyerId;

    @NotNull
    private UUID sellerId;

    @NotNull
    private UUID propertyId;

    @NotNull
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
