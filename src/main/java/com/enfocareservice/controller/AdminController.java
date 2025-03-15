package com.enfocareservice.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enfocareservice.dto.DoctorDTO;
import com.enfocareservice.entity.ProfileEntity;
import com.enfocareservice.repository.ProfileRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ProfileRepository profileRepository;

	@PutMapping("/approve-doctor/{email}")
	public ResponseEntity<String> approveDoctor(@PathVariable String email,
			@RequestParam(required = false) String licenseNumber) {
		Optional<ProfileEntity> doctorOpt = profileRepository.findByEmail(email);

		if (doctorOpt.isPresent()) {
			ProfileEntity doctor = doctorOpt.get();

			// ✅ Set approval fields
			doctor.setApproved(true);
			doctor.setApprovalDate(new Date());

			// ✅ Update license number if provided
			if (licenseNumber != null && !licenseNumber.isEmpty()) {
				doctor.setLicenseNumber(licenseNumber);
			}

			profileRepository.save(doctor);
			return ResponseEntity.ok("Doctor approved successfully.");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.");
	}

	@PutMapping("/reject-doctor/{email}")
	public ResponseEntity<String> rejectDoctor(@PathVariable String email) {
		Optional<ProfileEntity> doctorOpt = profileRepository.findByEmail(email);

		if (doctorOpt.isPresent()) {
			profileRepository.delete(doctorOpt.get());
			return ResponseEntity.ok("Doctor registration rejected.");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.");
	}

	@GetMapping("/pending-doctors")
	public ResponseEntity<List<DoctorDTO>> getPendingDoctors() {
		List<ProfileEntity> pendingDoctors = profileRepository.findUnapprovedDoctors();

		// ✅ Convert ProfileEntity to DoctorDTO
		List<DoctorDTO> doctorDTOs = pendingDoctors.stream().map(doctor -> new DoctorDTO(doctor.getFirstname(),
				doctor.getLastname(), doctor.getEmail(), doctor.getLicenseNumber(), doctor.getBirthday() // ✅ Fetch Date
																											// of Birth
		)).collect(Collectors.toList());

		return ResponseEntity.ok(doctorDTOs);
	}
}
