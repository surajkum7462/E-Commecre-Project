package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ecom.model.UserDtls;
import com.ecom.repo.UserRepo;

public class UserDetailsServiceImpl implements UserDetailsService{

	
	
	@Autowired
	private UserRepo userRepo;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDtls user = userRepo.findByEmail(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("user not found");
		}
		return new CustomUser(user);
	}
	
	

}
