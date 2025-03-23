package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.UserDtls;
import com.ecom.repo.UserRepo;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserService userService;

	/*
	 * @Override public void onAuthenticationFailure(HttpServletRequest request,
	 * HttpServletResponse response, AuthenticationException exception) throws
	 * IOException, ServletException {
	 * 
	 * // At the login time we are accessing the email and set
	 * 
	 * String email = request.getParameter("username");
	 * 
	 * UserDtls userDtls = userRepo.findByEmail(email); if (userDtls != null) { if
	 * (userDtls.getIsEnable()) { if (userDtls.getAccountNonLocked()) { // check if
	 * user enter wrong password if (userDtls.getFailedAttempt() <
	 * AppConstant.ATTEMPT_TIME) { userService.increaseFailedAttempt(userDtls); }
	 * else { userService.userAccountLock(userDtls); exception = new
	 * LockedException("Your Account is Locked !. You have reached 3 attempt"); } }
	 * else { if (userService.unlockAccountTimeExpired(userDtls)) { exception = new
	 * LockedException("Your Account is unlocked !. You try to login"); } else {
	 * exception = new
	 * LockedException("Your account is locked ! Please try after 1 hour"); }
	 * 
	 * } } else { exception = new LockedException("Your account is inactive"); } }
	 * else { exception = new LockedException("Email and Password is incorrect"); }
	 * super.setDefaultFailureUrl("/signin?error");
	 * super.onAuthenticationFailure(request, response, exception); }
	 */
	
	
	/*@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	        AuthenticationException exception) throws IOException, ServletException {

	    String email = request.getParameter("username");
	    UserDtls userDtls = userRepo.findByEmail(email);

	    if (userDtls != null) {
	        if (!userDtls.getIsEnable()) {
	            exception = new LockedException("Your account is not verified. Please check your email.");
	        } else if (userDtls.getAccountNonLocked()) {
	            if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
	                userService.increaseFailedAttempt(userDtls);
	            } else {
	                userService.userAccountLock(userDtls);
	                exception = new LockedException("Your account is locked due to multiple failed attempts.");
	            }
	        }
	    } else {
	        exception = new LockedException("Invalid email or password.");
	    }

	    super.setDefaultFailureUrl("/signin?error");
	    super.onAuthenticationFailure(request, response, exception);
	}

}*/
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	        AuthenticationException exception) throws IOException, ServletException {

	    String email = request.getParameter("username");
	    UserDtls userDtls = userRepo.findByEmail(email);

	    if (userDtls != null) {
	        if (!userDtls.getIsEnable()) { 
	            // Account is not verified
	            exception = new LockedException("Your account is not verified. Please check your email.");
	        } else if (userDtls.getAccountNonLocked()) {
	            // Account is active and not locked
	            if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
	                userService.increaseFailedAttempt(userDtls);
	            } else {
	                userService.userAccountLock(userDtls);
	                exception = new LockedException("Your account is locked due to multiple failed attempts.");
	            }
	        } else {
	            // Account is locked, check if lock duration has expired
	            if (userService.unlockAccountTimeExpired(userDtls)) {
	                exception = new LockedException("Your account was unlocked! Please try logging in.");
	            } else {
	                exception = new LockedException("Your account is locked! Please try again after 1 hour.");
	            }
	        }
	    } else {
	        // Invalid credentials
	        exception = new LockedException("Invalid email or password.");
	    }

	    super.setDefaultFailureUrl("/signin?error");
	    super.onAuthenticationFailure(request, response, exception);
	}
}

