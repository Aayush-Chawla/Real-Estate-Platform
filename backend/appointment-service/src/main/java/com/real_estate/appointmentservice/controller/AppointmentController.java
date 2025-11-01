package com.real_estate.appointmentservice.controller;

import com.real_estate.appointmentservice.dto.AppointmentRequest;
import com.real_estate.appointmentservice.dto.AppointmentResponse;
import com.real_estate.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(service.createAppointment(request));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAll() {
        return ResponseEntity.ok(service.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable ObjectId id) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(@PathVariable ObjectId  id,
                                                      @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(service.updateAppointment(id, request));
    }

    // soft delete
    // appointment.status -> "CANCELLED"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ObjectId  id) {
        service.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
