package com.ecom.service;


import java.util.List;

import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.UserDtls;

public interface UserService {
	
	public UserDtls saveUser(UserDtls user,String url);
	
	public UserDtls getUserByEmail(String email);
	
	public List<UserDtls> getAllUsers(String role);

	public Boolean updateAccountStatus(int id, Boolean status);
	
	public void increaseFailedAttempt(UserDtls user);
	
	public void userAccountLock(UserDtls user);
	
	public boolean unlockAccountTimeExpired(UserDtls user);
	
	public void resetAttempt(int id);

	public void updateUserResetToken(String email, String resetToken);
	
	public UserDtls getUserByTokens(String token);
	
	public UserDtls updateUser(UserDtls user);
	
	public UserDtls updateUserProfile(UserDtls user,MultipartFile img);

	public UserDtls searchUserByEmail(String email);
	
	public Page<UserDtls> getAllUserPagination(Integer pageNo , Integer pageSize);
	
	public UserDtls saveAdmin(UserDtls user);
	
	Page<UserDtls> getUsersByRoleWithPagination(String role, Integer pageNo, Integer pageSize);

	public Boolean existsEmail(String email);
	
	public boolean verifyAccount(String verificationCode);

}
