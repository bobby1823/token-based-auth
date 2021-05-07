package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.controller;

import java.net.URI;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.dto.SignInDTO;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.exception.AppException;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.Role;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.RoleName;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.User;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.ApiResponse;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.JwtAuthenticationResponse;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.LoginRequest;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.SignUpRequest;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository.RoleRepository;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository.UserRepository;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.security.JwtTokenProvider;

/**
 * @author M1030451
 *
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins= {"http://poc-docker.eastus.cloudapp.azure.com:4200", "http://localhost:4200"})
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider tokenProvider;

	@PostMapping("/signin")
	@CrossOrigin(origins= {"http://poc-docker.eastus.cloudapp.azure.com:4200", "http://localhost:4200"})
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			String jwt = tokenProvider.generateToken(authentication);
			Cookie cookie = WebUtils.getCookie(request, "JWT");
			String token = jwt;
			if (cookie == null || token != null && !token.equals(cookie.getValue())) {
				cookie = new Cookie("JWT", token);
				cookie.setPath("/");
				cookie.setValue(token);
				cookie.setMaxAge(60 * 60 * 24);
				cookie.setHttpOnly(true);
				cookie.setDomain("localhost");

				cookie.setSecure(false);

				response.addCookie(cookie);
				// HttpHeaders headers = new HttpHeaders();
				// headers.add("Set-Cookie", cookie.toString());
				// return new ResponseEntity<>(new
				// JwtAuthenticationResponse(jwt), headers, HttpStatus.OK);
			}
			return ResponseEntity.ok(new SignInDTO(new JwtAuthenticationResponse(jwt), authentication.getPrincipal()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new String("Invalid username or password"));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (userRepository.existsByUserName(signUpRequest.getUserName())) {
			return new ResponseEntity(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
		}

		// Creating user's account
		User user = new User(signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getUserName(),
				signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getTenantName());
		
		if(user.getPassword().isEmpty() || user.getPassword().equals(null)) {
			user.setPassword("Mindtree@12");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("User Role not set."));

		user.setRole(userRole);

		User result = userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{username}")
				.buildAndExpand(result.getUserName()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
	}
}