package com.enfocareservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.enfocareservice.entity.ProfileEntity;
import com.enfocareservice.entity.UserEntity;
import com.enfocareservice.repository.ProfileRepository;
import com.enfocareservice.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Load user for login
		UserEntity user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// ✅ Check if the user is a doctor and approved
		ProfileEntity profile = profileRepository.findByEmail(user.getEmail()).orElse(null);
		if (profile != null && Boolean.TRUE.equals(profile.getIsDoctor())
				&& !Boolean.TRUE.equals(profile.getApproved())) {
			throw new UsernameNotFoundException("Your doctor profile is pending approval.");
		}

		return user; // This returns your UserEntity which implements UserDetails
	}
}
