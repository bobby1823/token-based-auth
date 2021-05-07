package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.dto;

import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.payload.JwtAuthenticationResponse;

public class SignInDTO {
	
	private JwtAuthenticationResponse jwt;
	
	private Object principal;
	
	public SignInDTO() {
	}
	
	

	public SignInDTO(JwtAuthenticationResponse jwt, Object principal) {
		this.jwt = jwt;
		this.principal = principal;
	}

	public JwtAuthenticationResponse getJwt() {
		return jwt;
	}

	public void setJwt(JwtAuthenticationResponse jwt) {
		this.jwt = jwt;
	}

	public Object getPrincipal() {
		return principal;
	}

	public void setPrincipal(Object principal) {
		this.principal = principal;
	}


}
