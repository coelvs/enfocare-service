package com.enfocareservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.enfocareservice.entity.TokenEntity;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

	@Query(value = """
			SELECT t FROM TokenEntity t INNER JOIN t.userEntity u
			WHERE u.id = :id AND (t.expired = false OR t.revoked = false)
			""")
	List<TokenEntity> findAllValidTokenByUserId(Long id);

	Optional<TokenEntity> findByToken(String token);

	@Modifying
	@Transactional
	@Query("DELETE FROM TokenEntity t WHERE t.userEntity.id = :userId")
	void deleteByUserId(Long userId);

}
