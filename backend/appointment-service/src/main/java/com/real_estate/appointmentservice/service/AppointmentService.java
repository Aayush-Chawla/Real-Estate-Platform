package com.real_estate.appointmentservice.service;

import com.real_estate.appointmentservice.dto.AppointmentRequest;
import com.real_estate.appointmentservice.dto.AppointmentResponse;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    List<AppointmentResponse> getAllAppointments();
    AppointmentResponse getAppointmentById(ObjectId id);
    AppointmentResponse updateAppointment(ObjectId  id, AppointmentRequest request);
    void deleteAppointment(ObjectId  id);
}
