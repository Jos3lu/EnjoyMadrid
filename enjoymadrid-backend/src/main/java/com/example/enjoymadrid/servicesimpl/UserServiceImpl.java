package com.example.enjoymadrid.servicesimpl;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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

		if (user.getPassword() != null && !user.getPassword().isBlank())
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		this.userRepository.save(user);	
	}
	
	@Override
	public User updateUser(Long userId, User updatedUser, String oldPassword) {
		User pastUser = getUser(userId);
				
		// Check if current password matches the password inserted in the form as current password
		if (oldPassword != null && !oldPassword.isBlank() 
				&& !passwordEncoder.matches(oldPassword, pastUser.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta!");
		}
		
		pastUser.setName(updatedUser.getName());
						
		// If user changes password, encode and save it
		if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
			pastUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		} 
				
		return this.userRepository.save(pastUser);
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

	@Override
	public void deleteTouristicPointOfUser(User user, TouristicPoint point) {
		user.getTouristicPoints().remove(point);
		this.userRepository.save(user);
	}
			
}
