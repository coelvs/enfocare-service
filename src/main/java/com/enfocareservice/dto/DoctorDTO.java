package com.enfocareservice.dto;

import java.util.Date;

public class DoctorDTO {
	private String firstname;
	private String lastname;
	private String email;
	private String licenseNumber;
	private Date birthday; // ✅ Include Date of Birth

	// Constructor
	public DoctorDTO(String firstname, String lastname, String email, String licenseNumber, Date birthday) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.licenseNumber = licenseNumber;
		this.birthday = birthday;
	}

	// Getters
	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getEmail() {
		return email;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public Date getBirthday() {
		return birthday;
	}
}
