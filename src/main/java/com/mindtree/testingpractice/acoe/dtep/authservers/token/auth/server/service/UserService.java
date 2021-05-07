package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.service;

import java.util.Collection;

import org.springframework.http.ResponseEntity;

import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.User;


public interface UserService {
	
	public Collection<User> readAllUsers();

	public ResponseEntity<?> updateUserById(Long id, User user);

	public void deleteUserById(Long id);

	public void updateUserTenant(String username, String tenantName);
}
