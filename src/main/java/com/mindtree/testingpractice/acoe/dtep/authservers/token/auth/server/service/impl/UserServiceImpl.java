package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.service.impl;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.Role;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.User;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.ApiResponse;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository.RoleRepository;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository.UserRepository;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.service.UserService;

@Service(value = "UserServiceImpl")
@Transactional
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Collection<User> readAllUsers() {
		Collection<User> users = userRepository
				.findAll().stream()
				.map(user -> modelMapper.map(user, User.class)).collect(Collectors.toList());
		return users;
		}

	@Override
	public ResponseEntity<?> updateUserById(Long id, User user) {
		
		Optional<User> optional = userRepository.findById(id);
		
		User ur = new User();
		
		if(optional.isPresent()) {
			ur = optional.get();
		}
		
		if(!ur.getEmail().equalsIgnoreCase(user.getEmail())) {
			if (userRepository.existsByEmail(user.getEmail())) {
				return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
			}else {
				ur.setEmail(user.getEmail());
			}
		}
		Optional<Role> opt = roleRepository.findByName(user.getRole().getName());
		Role role = new Role();
		if(opt.isPresent()) {
			role = opt.get();
		}
		
		ur.setFirstName(user.getFirstName());
		ur.setLastName(user.getLastName());
		ur.setTenantName(user.getTenantName());
		ur.setRole(role);
		
		User result = userRepository.save(ur);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{username}")
				.buildAndExpand(result.getUserName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User updated successfully"));
	}

	@Override
	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public void updateUserTenant(String username, String tenantName) {
		Optional<User> op = userRepository.findByUserName(username);
		User user = null;
		if(op.isPresent()) {
			user = op.get();
		}
		user.setTenantName(tenantName);
		if(user.getId() != null) {
			userRepository.save(user);
		}
	}

}
