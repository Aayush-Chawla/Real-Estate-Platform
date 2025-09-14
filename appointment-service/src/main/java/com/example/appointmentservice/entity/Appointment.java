package com.example.appointmentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "buyer_idx", columnList = "buyerId"),
        @Index(name = "seller_idx", columnList = "sellerId"),
        @Index(name = "property_idx", columnList = "propertyId"),
        @Index(name = "status_idx", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID buyerId;

    private UUID sellerId;

    private UUID propertyId;

    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private LocalDateTime canceledAt;

    public enum Status {
        PENDING, CONFIRMED, RESCHEDULED, CANCELLED, COMPLETED
    }
}
