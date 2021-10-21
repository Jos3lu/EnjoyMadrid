package com.enjoymadrid.serviceslogic;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.UserService;

@Service
public class UserServiceLogic implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceLogic(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User getUser(Long userId) {
		return this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
	}
	
	@Override
	public User getUserByUsername(String username) {
		return this.userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));
	}
		
	@Override
	public void createUser(User user) {
		if (this.userRepository.existsByUsername(user.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede crear la cuenta, el nombre de usuario ya existe");
		}
				
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		this.userRepository.save(user);	
	}
	
	@Override
	public User updateUser(Long userId, User updatedUser, String oldPassword) {
		User pastUser = getUser(userId);
		
		updatedUser.setId(pastUser.getId());
		updatedUser.setPhoto(pastUser.getPhoto());
		updatedUser.setRoutes(pastUser.getRoutes());
		updatedUser.setComments(pastUser.getComments());
		
		// Check if current password matches the password inserted in the form as current password
		if (oldPassword != null && !oldPassword.isBlank() 
				&& !passwordEncoder.matches(oldPassword, pastUser.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no coincide con la que se ha ingresado");
		}
						
		// If user changes password, encode and save it
		if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
			updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		} else {
			updatedUser.setPassword(pastUser.getPassword());
		}
				
		
		return this.userRepository.save(updatedUser);
	}
	
	@Override
	public User updateUserImage(Long userId, MultipartFile imageUser) {
		User user = getUser(userId);
		try {
			user.setPhoto(imageUser.getBytes());
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la foto de perfil", e);
		}
		return this.userRepository.save(user);
	}
		
	@Override
	public void deleteUser(Long userId) {
		User user = getUser(userId);
		this.userRepository.delete(user);
	}
			
}
