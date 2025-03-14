package com.enfocareservice.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profile")
public class ProfileEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "profile_id")
	private Long profileId;

	@Column(name = "email")
	private String email;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "gender")
	private String gender;

	@Column(name = "birthday")
	private Date birthday;

	@Column(name = "height")
	private Double height;

	@Column(name = "phone")
	private String phone;

	@Column(name = "weight")
	private Double weight;

	@Column(name = "classification")
	private String classification;

	@Column(name = "is_doctor")
	private Boolean isDoctor;

	@Column(name = "medical_field")
	private String medicalField;

	@Column(name = "biometric")
	private Boolean biometric;

	@Column(name = "age")
	private Integer age;

	@Column(name = "bmi")
	private Double bmi;

	@Column(name = "profile_setup")
	private String profileSetup;

	@Column(name = "bloodtype")
	private String bloodType;

	@Column(name = "avatar_directory")
	private String avatarDirectory;

	@Column(name = "license_number")
	private String licenseNumber;

	@Column(name = "is_approved")
	private Boolean isApproved = false;

	@Column(name = "approval_date")
	private Date approvalDate;

	public String getAvatarDirectory() {
		return avatarDirectory;
	}

	public void setAvatarDirectory(String avatarDirectory) {
		this.avatarDirectory = avatarDirectory;
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

	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
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

	public Date getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}

}
