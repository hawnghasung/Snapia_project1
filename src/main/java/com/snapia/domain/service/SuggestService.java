package com.snapia.domain.service;

import com.snapia.domain.constant.AppointmentStatus;
import com.snapia.domain.constant.BusinessNotificationType;
import com.snapia.domain.constant.SuggestStatus;
import com.snapia.domain.dto.SuggestDTO;
import com.snapia.domain.entity.Appointment;
import com.snapia.domain.entity.Suggest;
import com.snapia.domain.repository.AppointmentRepository;
import com.snapia.domain.repository.SuggestRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class SuggestService {
    private final SuggestRepository suggestRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final BusinessNotificationService notificationService;

    public SuggestDTO createSuggest(SuggestDTO suggestDTO) {
        Appointment appointment = appointmentRepository.findById(suggestDTO.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        Suggest suggest = Suggest.builder()
                .suggestStatus(SuggestStatus.AWAITING)
                .appointment(appointment)
                .suggestTime(suggestDTO.getSuggestTime())
                .build();

        suggestRepository.save(suggest);

        notificationService.send(appointment.getBusinessProfile().getUsers().getUid(),
                appointment.getRequester().getUid(), BusinessNotificationType.APPOINTMENT);

        return SuggestDTO.builder()
                .appointmentId(suggestDTO.getAppointmentId())
                .suid(suggest.getSuid())
                .suggestStatus(suggest.getSuggestStatus())
                .suggestTime(suggest.getSuggestTime())
                .build();
    }

    public SuggestDTO acceptSuggest(Long suggestId) {
        Suggest suggest = suggestRepository.findById(suggestId)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        suggest.setSuggestStatus(SuggestStatus.ACCEPT);

        Appointment appointment = appointmentRepository.findById(suggest.getAppointment().getAid())
                .orElseThrow(()->new IllegalArgumentException("오류"));

        appointment.setAppointmentStatus(AppointmentStatus.ACCEPT);

        notificationService.send(appointment.getRequester().getUid(), appointment.getBusinessProfile().getUsers().getUid(),
                BusinessNotificationType.APPOINTMENT);

        return SuggestDTO.builder()
                .appointmentId(suggest.getAppointment().getAid())
                .suid(suggest.getSuid())
                .suggestStatus(suggest.getSuggestStatus())
                .suggestTime(suggest.getSuggestTime())
                .build();
    }

    public SuggestDTO rejectSuggest(Long suggestId, String rejectReason) {
        Suggest suggest = suggestRepository.findById(suggestId)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        suggest.setSuggestStatus(SuggestStatus.DECLINE);
        suggest.setRejectReason(rejectReason);

        Appointment appointment = appointmentRepository.findById(suggest.getAppointment().getAid())
                .orElseThrow(()->new IllegalArgumentException("오류"));

        appointment.setAppointmentStatus(AppointmentStatus.DECLINE);

        notificationService.send(appointment.getRequester().getUid(), appointment.getBusinessProfile().getUsers().getUid(),
                BusinessNotificationType.APPOINTMENT);

        return SuggestDTO.builder()
                .appointmentId(suggest.getAppointment().getAid())
                .suid(suggest.getSuid())
                .suggestStatus(suggest.getSuggestStatus())
                .suggestTime(suggest.getSuggestTime())
                .rejectReason(suggest.getRejectReason())
                .build();

    }


}
