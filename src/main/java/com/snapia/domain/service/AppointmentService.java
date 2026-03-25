package com.snapia.domain.service;

import com.snapia.domain.constant.AppointmentStatus;
import com.snapia.domain.constant.BusinessNotificationType;
import com.snapia.domain.dto.AppointmentDTO;
import com.snapia.domain.dto.SuggestDTO;
import com.snapia.domain.entity.Appointment;
import com.snapia.domain.entity.BusinessProfile;
import com.snapia.domain.entity.Suggest;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.AppointmentRepository;
import com.snapia.domain.repository.BusinessProfileRepository;
import com.snapia.domain.repository.SuggestRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final BusinessNotificationService notificationService;
    private final SuggestService suggestService;
    private final SuggestRepository suggestRepository;

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        Users guest = userRepository.findById(appointmentDTO.getRequester_id())
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        BusinessProfile master = businessProfileRepository.findById(appointmentDTO.getBusinessProfile_id())
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        Appointment appointment = Appointment.builder()
                .appointmentStatus(AppointmentStatus.WAIT)
                .businessProfile(master)
                .requestTime(appointmentDTO.getRequestTime())
                .requester(guest)
                .build();

        appointmentRepository.save(appointment);

        notificationService.send(guest.getUid(), master.getUsers().getUid(), BusinessNotificationType.APPOINTMENT);

        return AppointmentDTO.builder()
                .aid(appointment.getAid())
                .businessProfile_id(appointment.getBusinessProfile().getBpid())
                .requester_id(appointment.getRequester().getUid())
                .requesterSnapiaId(appointment.getRequester().getSnapiaId())
                .requesterName(appointment.getRequester().getName())
                .requestTime(appointment.getRequestTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .build();
    }

    public AppointmentDTO acceptAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        appointment.setAppointmentStatus(AppointmentStatus.ACCEPT);

        Users guest = userRepository.findById(appointment.getRequester().getUid())
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        notificationService.send(appointment.getBusinessProfile().getUsers().getUid(),
                guest.getUid(), BusinessNotificationType.APPOINTMENT);

        return AppointmentDTO.builder()
                .aid(appointment.getAid())
                .businessProfile_id(appointment.getBusinessProfile().getBpid())
                .requester_id(appointment.getRequester().getUid())
                .requesterSnapiaId(appointment.getRequester().getSnapiaId())
                .requesterName(appointment.getRequester().getName())
                .requestTime(appointment.getRequestTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .build();
    }

    public AppointmentDTO rejectAppointment(Long appointmentId, String rejectReason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        appointment.setAppointmentStatus(AppointmentStatus.DECLINE);
        appointment.setRejectReason(rejectReason);

        notificationService.send(appointment.getBusinessProfile().getUsers().getUid(),
                appointment.getRequester().getUid(), BusinessNotificationType.APPOINTMENT);

        return AppointmentDTO.builder()
                .aid(appointment.getAid())
                .businessProfile_id(appointment.getBusinessProfile().getBpid())
                .requester_id(appointment.getRequester().getUid())
                .requesterSnapiaId(appointment.getRequester().getSnapiaId())
                .requesterName(appointment.getRequester().getName())
                .requestTime(appointment.getRequestTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .rejectReason(appointment.getRejectReason())
                .build();
    }

    public AppointmentDTO suggestAppointment(SuggestDTO suggestDTO) {
        Appointment appointment = appointmentRepository.findById(suggestDTO.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        appointment.setAppointmentStatus(AppointmentStatus.SUGGESTED);
        suggestService.createSuggest(suggestDTO);

        return AppointmentDTO.builder()
                .aid(appointment.getAid())
                .businessProfile_id(appointment.getBusinessProfile().getBpid())
                .requester_id(appointment.getRequester().getUid())
                .requesterSnapiaId(appointment.getRequester().getSnapiaId())
                .requesterName(appointment.getRequester().getName())
                .requestTime(appointment.getRequestTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .build();
    }

    public List<AppointmentDTO> guestAppointmentList(Long requestId) {
        return appointmentRepository.findByRequesterUid(requestId).stream()
                .map(a -> {
                    Suggest suggest = suggestRepository.findByAppointmentAid(a.getAid()).orElse(null);
                    return AppointmentDTO.builder()
                            .aid(a.getAid())
                            .appointmentStatus(a.getAppointmentStatus())
                            .requestTime(a.getRequestTime())
                            .requester_id(a.getRequester().getUid())
                            .requesterSnapiaId(a.getRequester().getSnapiaId())
                            .requesterName(a.getRequester().getName())
                            .businessProfile_id(a.getBusinessProfile().getBpid())
                            .suggestTime(suggest != null ? suggest.getSuggestTime() : null)
                            .suid(suggest != null ? suggest.getSuid() : null)
                            .build();
                }).toList();
    }

    public List<AppointmentDTO> businessAppointmentList(Long businessId) {
        return appointmentRepository.findByBusinessProfileBpid(businessId).stream()
                .map(a -> AppointmentDTO.builder()
                        .aid(a.getAid())
                        .appointmentStatus(a.getAppointmentStatus())
                        .requestTime(a.getRequestTime())
                        .requester_id(a.getRequester().getUid())
                        .requesterSnapiaId(a.getRequester().getSnapiaId())
                        .requesterName(a.getRequester().getName())
                        .businessProfile_id(a.getBusinessProfile().getBpid())
                        .build()).toList();
    }

    public AppointmentDTO appointmentSearch(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        Suggest suggest = suggestRepository.findByAppointmentAid(appointmentId).orElse(null);

        return AppointmentDTO.builder()
                .aid(appointment.getAid())
                .businessProfile_id(appointment.getBusinessProfile().getBpid())
                .requester_id(appointment.getRequester().getUid())
                .requesterSnapiaId(appointment.getRequester().getSnapiaId())
                .requesterName(appointment.getRequester().getName())
                .requestTime(appointment.getRequestTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .rejectReason(appointment.getRejectReason())
                .suggestTime(suggest != null ? suggest.getSuggestTime() : null)
                .suid(suggest != null ? suggest.getSuid() : null)
                .build();
    }

    public AppointmentDTO cancelAppointment(Long appointmentId, Long cancelUid) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("오류"));

        appointment.setAppointmentStatus(AppointmentStatus.CANCEL);

        if (cancelUid.equals(appointment.getBusinessProfile().getUsers().getUid())) {
            notificationService.send(appointment.getBusinessProfile().getUsers().getUid(),
                    appointment.getRequester().getUid(), BusinessNotificationType.APPOINTMENT);
        } else if (cancelUid.equals(appointment.getRequester().getUid())) {
            notificationService.send(appointment.getRequester().getUid(),
                    appointment.getBusinessProfile().getUsers().getUid(), BusinessNotificationType.APPOINTMENT);
        }

        return AppointmentDTO.builder()
                .aid(appointment.getAid())
                .businessProfile_id(appointment.getBusinessProfile().getBpid())
                .requester_id(appointment.getRequester().getUid())
                .requesterSnapiaId(appointment.getRequester().getSnapiaId())
                .requesterName(appointment.getRequester().getName())
                .requestTime(appointment.getRequestTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .build();
    }

    public List<AppointmentDTO> confirmedList(Long uid, boolean isBusiness) {
        List<Appointment> appointments;
        if (isBusiness) {
            BusinessProfile bp = businessProfileRepository.findByUsersUid(uid)
                    .orElseThrow(() -> new IllegalArgumentException("비즈니스 프로필 없음"));
            appointments = appointmentRepository.findByBusinessProfileBpid(bp.getBpid())
                    .stream()
                    .filter(a -> a.getAppointmentStatus() == AppointmentStatus.ACCEPT)
                    .toList();
        } else {
            appointments = appointmentRepository.findByRequesterUid(uid)
                    .stream()
                    .filter(a -> a.getAppointmentStatus() == AppointmentStatus.ACCEPT)
                    .toList();
        }
        return appointments.stream().map(a -> AppointmentDTO.builder()
                .aid(a.getAid())
                .requestTime(a.getRequestTime())
                .appointmentStatus(a.getAppointmentStatus())
                .requester_id(a.getRequester().getUid())
                .requesterSnapiaId(a.getRequester().getSnapiaId())
                .requesterName(a.getRequester().getName())
                .businessProfile_id(a.getBusinessProfile().getBpid())
                .build()).toList();
    }
}