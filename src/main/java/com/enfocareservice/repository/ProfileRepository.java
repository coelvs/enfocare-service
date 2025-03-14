package com.enfocareservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.enfocareservice.entity.ProfileEntity;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

	Optional<ProfileEntity> findByEmail(String email);

	Optional<ProfileEntity> findByMedicalField(String medicalField);

	ProfileEntity findByPhone(String phoneNumber);

	@Query("SELECT p FROM ProfileEntity p WHERE p.isDoctor = true AND p.isApproved = false")
	List<ProfileEntity> findUnapprovedDoctors();

}
