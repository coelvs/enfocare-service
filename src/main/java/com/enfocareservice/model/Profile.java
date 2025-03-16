package com.enfocareservice.model;

import java.util.Date;

public class Profile {

	private Long profileId;
	private String email;
	private String firstname;
	private String lastname;
	private String gender;
	private Date birthday;
	private Double height;
	private String phone;
	private Double weight;
	private String classification;
	private Boolean isDoctor;
	private String medicalField;
	private Boolean biometric;
	private Integer age;
	private Double bmi;
	private String profileSetup;
	private String bloodType;
	private String avatarDirectory;
	private String licenseNumber;
	private Boolean isApproved;

	public String getAvatarDirectory() {
		return avatarDirectory;
	}

	public void setAvatarDirectory(String avatarDirectory) {
		this.avatarDirectory = avatarDirectory;
	}

	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public Boolean getIsDoctor() {
		return isDoctor;
	}

	public void setIsDoctor(Boolean isDoctor) {
		this.isDoctor = isDoctor;
	}

	public String getMedicalField() {
		return medicalField;
	}

	public void setMedicalField(String medicalField) {
		this.medicalField = medicalField;
	}

	public Boolean getBiometric() {
		return biometric;
	}

	public void setBiometric(Boolean biometric) {
		this.biometric = biometric;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Double getBmi() {
		return bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public String getProfileSetup() {
		return profileSetup;
	}

	public void setProfileSetup(String profileSetup) {
		this.profileSetup = profileSetup;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public Boolean getApproved() {
		return isApproved;
	}

	public void setApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

}
