package com.enfocareservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.enfocareservice.entity.UserStatusEntity;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatusEntity, Long> {

	UserStatusEntity findByEmail(String email);

	@Modifying
	@Transactional
	@Query("DELETE FROM UserStatusEntity us WHERE us.userId = :userId")
	void deleteByUserId(Long userId);

}
