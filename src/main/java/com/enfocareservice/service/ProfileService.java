package com.enfocareservice.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.enfocareservice.entity.ProfileEntity;
import com.enfocareservice.model.Profile;
import com.enfocareservice.model.mapper.ProfileMapper;
import com.enfocareservice.repository.ProfileRepository;

@Service
public class ProfileService {

	@Value("${avatar.dir}")
	private String avatarDir;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private ProfileMapper profileMapper;

	public Profile getProfileByEmail(String email) {
		ProfileEntity profileEntity = profileRepository.findByEmail(email).orElse(null);
		return profileEntity != null ? profileMapper.map(profileEntity) : null;
	}

	public Profile getProfileByMedicalField(String medicalField) {
		ProfileEntity profileEntity = profileRepository.findByMedicalField(medicalField).orElse(null);
		return profileEntity != null ? profileMapper.map(profileEntity) : null;
	}

	public Profile createProfile(Profile profile) {

		ProfileEntity profileEntity = new ProfileEntity();

		profileEntity.setAge(profile.getAge());
		profileEntity.setBiometric(profile.getBiometric());
		profileEntity.setBirthday(profile.getBirthday());
		profileEntity.setBmi(profile.getBmi());
		profileEntity.setClassification(profile.getClassification());
		profileEntity.setEmail(profile.getEmail());
		profileEntity.setFirstname(profile.getFirstname());
		profileEntity.setLastname(profile.getLastname());
		profileEntity.setGender(profile.getGender());
		profileEntity.setHeight(profile.getHeight());
		profileEntity.setIsDoctor(profile.getIsDoctor());
		profileEntity.setMedicalField(profile.getMedicalField());
		profileEntity.setPhone(profile.getPhone());
		profileEntity.setProfileSetup(profile.getProfileSetup());
		profileEntity.setWeight(profile.getWeight());
		profileEntity.setBloodType(profile.getBloodType());
		profileEntity.setLicenseNumber(profile.getLicenseNumber());

		System.out.println("Saving License Number: " + profile.getLicenseNumber()); // Debug print

		Profile result = profileMapper.map(profileRepository.save(profileEntity));

		return result;
	}

	public void uploadAvatarFile(String email, MultipartFile file) {
		// Create the directory if it doesn't exist
		Path directoryPath = Paths.get(avatarDir);
		try {
			Files.createDirectories(directoryPath);
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Generate a unique file name (you might want to add more logic for uniqueness)
		String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

		// Combine the directory path and the unique file name to get the full path
		Path filePath = Paths.get(avatarDir, uniqueFileName);

		// Save the file to the specified location
		try {
			Files.write(filePath, file.getBytes());
			Profile profile = new Profile();

			profile.setAvatarDirectory(uniqueFileName);

			updateProfile(email, profile);
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Now, you can save the file details to the database or perform any additional
		// logic
		System.out.println("File uploaded successfully for user " + email);
		System.out.println("File Name: " + file.getOriginalFilename());
		System.out.println("File Size: " + file.getSize() + " bytes");
		System.out.println("File Path: " + filePath.toAbsolutePath());

		// You can add more logic based on your requirements
	}

	public Profile updateProfile(String email, Profile updatedProfile) {
		System.err.println("COCK");
		ProfileEntity existingProfileEntity = profileRepository.findByEmail(email).orElse(null);

		if (existingProfileEntity != null) {
			// Update fields with non-null values from the updated profile

			if (StringUtils.hasText(updatedProfile.getEmail())) {
				existingProfileEntity.setEmail(updatedProfile.getEmail());
			}
			if (StringUtils.hasText(updatedProfile.getFirstname())) {
				existingProfileEntity.setFirstname(updatedProfile.getFirstname());
			}
			if (StringUtils.hasText(updatedProfile.getLastname())) {
				existingProfileEntity.setLastname(updatedProfile.getLastname());
			}
			if (StringUtils.hasText(updatedProfile.getGender())) {
				existingProfileEntity.setGender(updatedProfile.getGender());
			}
			if (updatedProfile.getBirthday() != null) {
				existingProfileEntity.setBirthday(updatedProfile.getBirthday());
			}
			if (updatedProfile.getHeight() != null) {
				existingProfileEntity.setHeight(updatedProfile.getHeight());
			}
			if (StringUtils.hasText(updatedProfile.getPhone())) {
				existingProfileEntity.setPhone(updatedProfile.getPhone());
			}
			if (updatedProfile.getWeight() != null) {
				existingProfileEntity.setWeight(updatedProfile.getWeight());
			}
			if (StringUtils.hasText(updatedProfile.getClassification())) {
				existingProfileEntity.setClassification(updatedProfile.getClassification());
			}
			if (updatedProfile.getIsDoctor() != null) {
				existingProfileEntity.setIsDoctor(updatedProfile.getIsDoctor());
			}
			if (StringUtils.hasText(updatedProfile.getMedicalField())) {
				existingProfileEntity.setMedicalField(updatedProfile.getMedicalField());
			}
			if (updatedProfile.getBiometric() != null) {
				existingProfileEntity.setBiometric(updatedProfile.getBiometric());
			}
			if (updatedProfile.getAge() != null) {
				existingProfileEntity.setAge(updatedProfile.getAge());
			}
			if (updatedProfile.getBmi() != null) {
				existingProfileEntity.setBmi(updatedProfile.getBmi());
			}
			if (StringUtils.hasText(updatedProfile.getProfileSetup())) {
				existingProfileEntity.setProfileSetup(updatedProfile.getProfileSetup());
			}
			if (StringUtils.hasText(updatedProfile.getBloodType())) {
				existingProfileEntity.setBloodType(updatedProfile.getBloodType());
			}

			if (StringUtils.hasText(updatedProfile.getAvatarDirectory())) {
				existingProfileEntity.setAvatarDirectory(updatedProfile.getAvatarDirectory());
			}

			// Save the updated entity
			ProfileEntity updatedEntity = profileRepository.save(existingProfileEntity);

			// Map and return the updated profile
			return profileMapper.map(updatedEntity);
		}

		// Return null if the profile doesn't exist
		return null;
	}

	public Path getFilePath(String filename) {

		return Paths.get(avatarDir, filename);

	}

	public Profile getProfileByPhoneNumber(String phoneNumber) {
		Profile profile = null;

		ProfileEntity profileEntity = profileRepository.findByPhone(phoneNumber);

		if (profileEntity != null) {
			profile = profileMapper.map(profileEntity);
		}

		return profile;

	}

}
