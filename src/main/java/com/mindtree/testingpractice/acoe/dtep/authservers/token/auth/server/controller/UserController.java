package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.exception.ResourceNotFoundException;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.User;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.PagedResponse;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.PollResponse;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.UserIdentityAvailability;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.UserProfile;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.UserSummary;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository.PollRepository;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository.UserRepository;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository.VoteRepository;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.security.CurrentUser;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.security.UserPrincipal;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.service.PollService;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.service.UserService;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.util.AppConstants;

/**
 * @author M1030451
 *
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins= {"http://poc-docker.eastus.cloudapp.azure.com:4200", "http://localhost:4200"})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollService pollService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName(), currentUser.getLastName());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUserName(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        long pollCount = pollRepository.countByCreatedBy(user.getId());
        long voteCount = voteRepository.countByUserId(user.getId());

        UserProfile userProfile = new UserProfile(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getCreatedAt(), pollCount, voteCount);

        return userProfile;
    }

    @GetMapping("/users/{username}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
                                                         @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsCreatedBy(username, currentUser, page, size);
    }


    @GetMapping("/users/{username}/votes")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
                                                       @CurrentUser UserPrincipal currentUser,
                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
    
    @RequestMapping(path = { "/users" }, method = { RequestMethod.GET })
	public Collection<User> readAllUsers(HttpServletRequest request) {
		return userService.readAllUsers();
	}
    
    @RequestMapping(path = { "/users/{id}" }, method = { RequestMethod.PUT })
    @CrossOrigin(origins= {"http://poc-docker.eastus.cloudapp.azure.com:4200", "http://localhost:4200"})
   	public ResponseEntity<?> updateUserById(@PathVariable(name = "id") Long id, @RequestBody User user ,HttpServletRequest request) {
   		return userService.updateUserById(id, user);
   	}
    
    @RequestMapping(path = { "/users/{id}" }, method = { RequestMethod.DELETE })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @CrossOrigin(origins= {"http://poc-docker.eastus.cloudapp.azure.com:4200", "http://localhost:4200"})
   	public void deleteUserById(@PathVariable Long id,HttpServletRequest request) {
    	userService.deleteUserById(id);
   	}
    
    @GetMapping("/users/{username}/{tenantName}")
    @CrossOrigin(origins= {"http://poc-docker.eastus.cloudapp.azure.com:4200", "http://localhost:4200"})
    public void updateUserTenant(@PathVariable String username, @PathVariable String tenantName, HttpServletRequest request) {
    	userService.updateUserTenant(username, tenantName);
    }
}
