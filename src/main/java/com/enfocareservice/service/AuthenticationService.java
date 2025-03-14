package com.enfocareservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.enfocareservice.entity.ProfileEntity;
import com.enfocareservice.entity.TokenEntity;
import com.enfocareservice.entity.UserEntity;
import com.enfocareservice.model.AuthenticationRequest;
import com.enfocareservice.model.AuthenticationResponse;
import com.enfocareservice.model.RegisterRequest;
import com.enfocareservice.model.Role;
import com.enfocareservice.model.TokenType;
import com.enfocareservice.repository.ProfileRepository;
import com.enfocareservice.repository.TokenRepository;
import com.enfocareservice.repository.UserRepository;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private UserStatusService userStatusService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest registerRequest) {

		AuthenticationResponse authenticationResponse = new AuthenticationResponse();

		UserEntity userEntity = new UserEntity();

		userEntity.setEmail(registerRequest.getEmail());
		userEntity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		userEntity.setRole(Role.USER);

		userRepository.save(userEntity);

		userStatusService.registerUserStatus(registerRequest.getEmail());

		String jwtToken = jwtService.generateToken(userEntity);

		authenticationResponse.setToken(jwtToken);

		return authenticationResponse;
	}

	public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
				authenticationRequest.getPassword()));

		UserEntity userEntity = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();

		// ✅ Prevent unqualified doctors from logging in
		if (userEntity.getRole() == Role.DOCTOR) {
			ProfileEntity profile = profileRepository.findByEmail(userEntity.getEmail())
					.orElseThrow(() -> new RuntimeException("Profile not found"));

			if (!profile.getApproved()) {
				throw new RuntimeException("Your account is pending approval. Please wait for admin verification.");
			}
		}

		userStatusService.setUserOnline(userEntity.getEmail());

		revokeAllUserTokens(userEntity);
		String jwtToken = jwtService.generateToken(userEntity);
		String refreshToken = jwtService.generateRefreshToken(userEntity);
		saveUserToken(userEntity, jwtToken);

		AuthenticationResponse authenticationResponse = new AuthenticationResponse();
		authenticationResponse.setToken(jwtToken);
		authenticationResponse.setEmail(jwtService.extractUsername(jwtToken));
		authenticationResponse.setExpiration(jwtService.extractExpiration(jwtToken));
		authenticationResponse.setRefreshToken(refreshToken);

		return authenticationResponse;
	}

	private void saveUserToken(UserEntity user, String jwtToken) {

		TokenEntity tokenEntity = new TokenEntity();
		tokenEntity.setUserEntity(user);
		tokenEntity.setToken(jwtToken);
		tokenEntity.setTokenType(TokenType.BEARER);
		tokenEntity.setExpired(false);
		tokenEntity.setRevoked(false);

		tokenRepository.save(tokenEntity);
	}

	private void revokeAllUserTokens(UserEntity user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(false);
			token.setRevoked(false);
		});
		tokenRepository.saveAll(validUserTokens);
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException, StreamWriteException, DatabindException, java.io.IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			var user = this.userRepository.findByEmail(userEmail).orElseThrow();
			if (jwtService.isTokenValid(refreshToken, user)) {
				var accessToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				AuthenticationResponse authResponse = new AuthenticationResponse();

				authResponse.setToken(accessToken);
				authResponse.setRefreshToken(refreshToken);
				authResponse.setEmail(user.getEmail());
				authResponse.setResponseCode(200);

				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}

	public boolean checkTokenValidity(String token, String userEmail) {
		System.err.println("checkTokenValidity is Called with data : token " + token + " email " + userEmail);
		boolean isValid = tokenRepository.findByToken(token).map(t -> !t.getExpired() && !t.getRevoked()).orElse(false);
		var user = this.userRepository.findByEmail(userEmail).orElseThrow();
		return jwtService.isTokenValid(token, user) && isValid;
	}

}
