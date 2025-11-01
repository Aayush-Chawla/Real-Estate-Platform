package com.example.appointmentservice.service;

import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.dto.AppointmentResponse;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    List<AppointmentResponse> getAllAppointments();
    AppointmentResponse getAppointmentById(UUID id);
    AppointmentResponse updateAppointment(UUID id, AppointmentRequest request);
    void deleteAppointment(UUID id);
}
