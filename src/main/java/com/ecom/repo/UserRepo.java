package com.ecom.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.UserDtls;

public interface UserRepo extends JpaRepository<UserDtls, Integer>{

	public UserDtls findByEmail(String email);

	public List<UserDtls> findByRole(String role);
	
	public UserDtls findByResetToken(String token);

	public Page<UserDtls> findByRole(String role, PageRequest pageable);
	
	public Boolean existsByEmail(String email); 
	
	
	public UserDtls findByVerificationCode(String verificationCode);
}
