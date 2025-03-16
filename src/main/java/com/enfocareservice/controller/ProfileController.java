package com.enfocareservice.controller;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.enfocareservice.model.Profile;
import com.enfocareservice.service.ProfileService;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/enfocare/profile")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@GetMapping("/{email}")
	public ResponseEntity<Profile> getProfileByEmail(@PathVariable String email) {

		System.err.println("getProfileByEmail Called");

		try {
			// Your ProfileService should have a method to retrieve a profile by email
			Profile profile = profileService.getProfileByEmail(email);

			if (profile != null) {
				return ResponseEntity.ok(profile);
			} else {
				// If no profile is found, return 404 Not Found
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			// Handle exceptions, e.g., log the error
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/specialization/{specialization}")
	public ResponseEntity<Profile> getProfileByMedicalField(@PathVariable String specialization) {

		System.err.println("getProfileByMedicalField Called");

		try {
			// Your ProfileService should have a method to retrieve a profile by email
			Profile profile = profileService.getProfileByMedicalField(specialization);

			if (profile != null) {
				return ResponseEntity.ok(profile);
			} else {
				// If no profile is found, return 404 Not Found
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			// Handle exceptions, e.g., log the error
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PatchMapping("/{email}")
	public ResponseEntity<Profile> updateProfile(@PathVariable String email, @RequestBody Profile updatedProfile) {
		try {
			Profile result = profileService.updateProfile(email, updatedProfile);
			return ResponseEntity.ok(result);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/save")
	public ResponseEntity<Profile> saveProfile(@RequestBody Profile profile) {
		System.err.println("profile called" + profile);

		try {
			// If profile indicates doctor, set isApproved to false
			if (Boolean.TRUE.equals(profile.getIsDoctor())) {
				profile.setApproved(false); // Pending approval
				System.err.println("Doctor profile detected. Setting isApproved=false");
			}

			Profile savedProfile = profileService.createProfile(profile);
			return ResponseEntity.ok(savedProfile);

		} catch (Exception e) {
			System.err.println("Error saving profile: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/avatar/{email}")
	public ResponseEntity<String> handleFileUpload(@PathVariable String email,
			@RequestParam("file") MultipartFile file) {

		System.err.println("handleFileUpload Called");

		try {
			// Your ProfileService should have a method to handle file upload
			profileService.uploadAvatarFile(email, file);
			return ResponseEntity.ok("File uploaded successfully");
		} catch (IOException e) {
			// Handle exceptions, e.g., log the error
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
		}
	}

	@GetMapping("/avatar/{email}")
	public ResponseEntity<Resource> getImage(@PathVariable String email) {
		// Retrieve the ProfileEntity from the database using the email
		Profile profile = profileService.getProfileByEmail(email);

		if (profile != null && profile.getAvatarDirectory() != null) {
			// Construct the full path to the image using the stored directory
			String directory = profile.getAvatarDirectory();

			Path imagePath = profileService.getFilePath(directory);

			// Create a FileSystemResource to represent the image file
			Resource resource = new FileSystemResource(imagePath.toFile());

			// Return the image as a ResponseEntity
			return ResponseEntity.ok()
					// You may add other headers like Content-Type if needed
					.body(resource);
		}

		// If the profile or image is not found, return a 404 Not Found response
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/phone/{phone}")
	public ResponseEntity<Profile> getByPhone(@PathVariable String phone) {

		System.err.println("getByPhone Called");

		try {
			// Your ProfileService should have a method to retrieve a profile by email
			Profile profile = profileService.getProfileByPhoneNumber(phone);

			if (profile != null) {
				return ResponseEntity.ok(profile);
			} else {
				// If no profile is found, return 404 Not Found
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			// Handle exceptions, e.g., log the error
			System.err.println(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

}
