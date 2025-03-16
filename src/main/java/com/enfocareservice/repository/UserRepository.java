package com.enfocareservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.enfocareservice.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	@Query("SELECT u FROM UserEntity u WHERE u.userEmail = :email") // CORRECT ✅
	Optional<UserEntity> findByEmail(@Param("email") String email);

	Optional<UserEntity> findById(Integer senderId);

}
