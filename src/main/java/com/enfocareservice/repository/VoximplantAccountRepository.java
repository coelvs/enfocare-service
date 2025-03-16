package com.enfocareservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.enfocareservice.entity.VoximplantAccountEntity;

@Repository
public interface VoximplantAccountRepository extends JpaRepository<VoximplantAccountEntity, Long> {

	VoximplantAccountEntity findByUser(String email);

	@Modifying
	@Transactional
	@Query("DELETE FROM VoximplantAccountEntity v WHERE v.user = :user")
	void deleteByUser(String user);

}
