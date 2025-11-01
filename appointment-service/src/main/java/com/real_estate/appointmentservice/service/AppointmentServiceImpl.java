package com.real_estate.appointmentservice.service;

import com.real_estate.appointmentservice.dto.AppointmentRequest;
import com.real_estate.appointmentservice.dto.AppointmentResponse;
import com.real_estate.appointmentservice.entity.Appointment;
import com.real_estate.appointmentservice.repository.AppointmentRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentServiceImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    private AppointmentResponse mapToResponse(Appointment entity) {
        return AppointmentResponse.builder()
                .id(entity.getId())
                .buyerId(entity.getBuyerId())
                .sellerId(entity.getSellerId())
                .propertyId(entity.getPropertyId())
                .scheduledAt(entity.getScheduledAt())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .canceledAt(entity.getCanceledAt())
                .build();
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Appointment entity = Appointment.builder()
                .buyerId(request.getBuyerId())
                .sellerId(request.getSellerId())
                .propertyId(request.getPropertyId())
                .scheduledAt(request.getScheduledAt())
                .notes(request.getNotes())
                .status(Appointment.Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        return mapToResponse(repository.save(entity));
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return repository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public AppointmentResponse getAppointmentById(ObjectId id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Override
    public AppointmentResponse updateAppointment(ObjectId id, AppointmentRequest request) {
        Appointment entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        entity.setScheduledAt(request.getScheduledAt());
        entity.setNotes(request.getNotes());
        entity.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(repository.save(entity));
    }

    @Override
    public void deleteAppointment(ObjectId id) {
        Appointment entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        entity.setStatus(Appointment.Status.CANCELLED);
        entity.setCanceledAt(LocalDateTime.now());

        repository.save(entity);
    }
}
