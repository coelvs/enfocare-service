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
import com.enfocareservice.entity.UserEntity;
import com.enfocareservice.repository.ProfileRepository;
import com.enfocareservice.repository.TokenRepository;
import com.enfocareservice.repository.UserRepository;
import com.enfocareservice.repository.UserStatusRepository;
import com.enfocareservice.repository.VoximplantAccountRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private VoximplantAccountRepository voximplantAccountRepository;

	@Autowired
	private UserStatusRepository userStatusRepository;

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
		Optional<UserEntity> userOpt = userRepository.findByEmail(email);

		if (doctorOpt.isPresent() && userOpt.isPresent()) {
			UserEntity user = userOpt.get();
			Long userId = user.getId();

			// 1. Delete from token table
			tokenRepository.deleteByUserId(userId);

			// 2. Delete from voximplant_account table
			voximplantAccountRepository.deleteByUser(email);

			// 3. Delete from user_status table
			userStatusRepository.deleteByEmail(email);

			// 4. Delete from profile table
			profileRepository.deleteByEmail(email);

			// 5. Delete from user table
			userRepository.delete(user);

			return ResponseEntity.ok("Doctor has been successfully rejected.");
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
