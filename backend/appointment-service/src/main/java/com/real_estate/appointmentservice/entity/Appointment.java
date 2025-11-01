package com.real_estate.appointmentservice.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

//    @Id
//    private UUID id = UUID.randomUUID(); // MongoDB doesn't auto-generate UUIDs like JPA

    @Id
    private ObjectId id; // Let MongoDB generate _id automatically

    @Indexed
    private UUID buyerId;

    @Indexed
    private UUID sellerId;

    @Indexed
    private UUID propertyId;

    private LocalDateTime scheduledAt;

    @Indexed
    private Status status = Status.PENDING;

    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private LocalDateTime canceledAt;

    public enum Status {
        PENDING, CONFIRMED, RESCHEDULED, CANCELLED, COMPLETED
    }
}
