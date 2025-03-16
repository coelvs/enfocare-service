package com.enfocareservice.config;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.enfocareservice.entity.UserEntity;
import com.enfocareservice.repository.TokenRepository;
import com.enfocareservice.repository.UserRepository;
import com.enfocareservice.service.JwtService;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository,
			TokenRepository tokenRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		jwt = authHeader.substring(7);
		logger.info("Received JWT: {}", jwt); // Log the received JWT

		userEmail = jwtService.extractUsername(jwt);
		logger.info("Extracted User Email: {}", userEmail);

		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// ✅ Fetch user directly from database using UserRepository
			Optional<UserEntity> userOpt = userRepository.findByEmail(userEmail);

			if (userOpt.isPresent()) {
				UserEntity user = userOpt.get();
				boolean isTokenValid = tokenRepository.findByToken(jwt).map(t -> !t.getExpired() && !t.getRevoked())
						.orElse(false);

				if (jwtService.isTokenValid(jwt, user) && isTokenValid) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							user, null, user.getAuthorities());

					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				} else {
					logger.debug("Token validation failed");
				}

			} else {
				logger.debug("User details not found for the user: " + userEmail);
			}

		}
		filterChain.doFilter(request, response);
	}

}