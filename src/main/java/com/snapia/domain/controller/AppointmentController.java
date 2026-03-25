package com.snapia.domain.controller;

import com.snapia.domain.dto.AppointmentDTO;
import com.snapia.domain.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointment")
@Log4j2
public class AppointmentController {
        private final AppointmentService appointmentService;

        @PostMapping("/request")
        public AppointmentDTO createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
                log.info("잘 됨 : createAppointment");
                return appointmentService.createAppointment(appointmentDTO);
        }

        @PostMapping("/accept")
        public AppointmentDTO acceptAppointment(@RequestBody AppointmentDTO appointmentDTO) {
                log.info("잘 됨 : acceptAppointment");
                return appointmentService.acceptAppointment(appointmentDTO.getAid());
        }

        @PostMapping("/reject")
        public AppointmentDTO rejectAppointment(@RequestBody AppointmentDTO appointmentDTO) {
                log.info("잘 됨 : rejectAppointment");
                return appointmentService.rejectAppointment(appointmentDTO.getAid(), appointmentDTO.getRejectReason());
        }

        @GetMapping("/a/guest")
        public List<AppointmentDTO> guestAppointmentList(@RequestParam("uid") Long requesterId) {
                log.info("잘 됨 : guestAppointmentList");
                return appointmentService.guestAppointmentList(requesterId);
        }

        @GetMapping("/a/business")
        public List<AppointmentDTO> businessAppointmentList(@RequestParam("uid") Long businessId) {
                log.info("잘 됨 : businessAppointmentList");
                return appointmentService.businessAppointmentList(businessId);
        }

        @GetMapping("/a/confirmed")
        public List<AppointmentDTO> confirmedList(
                @RequestParam Long uid,
                @RequestParam boolean isBusiness) {
                log.info("잘 됨 : confirmedList");
                return appointmentService.confirmedList(uid, isBusiness);
        }

        @GetMapping("/{aid}")
        public AppointmentDTO appointmentSearch(@PathVariable Long aid) {
                log.info("잘 됨 : appointmentSearch");
                return appointmentService.appointmentSearch(aid);
        }

        @PostMapping("/cancel")
        public AppointmentDTO cancelAppointment(@RequestBody AppointmentDTO appointmentDTO) {
                log.info("잘 됨 : cancelAppointment");
                return appointmentService.cancelAppointment(appointmentDTO.getAid(), appointmentDTO.getCancelUid());
        }
}