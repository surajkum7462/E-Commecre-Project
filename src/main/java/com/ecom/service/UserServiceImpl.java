package com.ecom.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.UserDtls;
import com.ecom.repo.UserRepo;
import com.ecom.util.AppConstant;
import com.ecom.util.BucketType;
import com.ecom.util.CommonUtil;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Optional<UserDtls> dbUser;

	@Autowired
	@Lazy
	private CommonUtil commonUtil;
	
	
	@Autowired
	private FileService fileService;

	/*
	 * @Override public UserDtls saveUser(UserDtls user) {
	 * user.setRole("ROLE_USER"); user.setIsEnable(true); String encodePassword =
	 * passwordEncoder.encode(user.getPassword()); user.setPassword(encodePassword);
	 * user.setAccountNonLocked(true); user.setFailedAttempt(0);
	 * user.setLockTime(null); return userRepo.save(user); }
	 */

	@Override
	public UserDtls saveUser(UserDtls user, String siteURL) {
		user.setRole("ROLE_USER");
		user.setIsEnable(false); // User is disabled until verification
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);

		// Encode password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Generate verification code
		String verificationCode = UUID.randomUUID().toString();
		user.setVerificationCode(verificationCode);

		UserDtls savedUser = userRepo.save(user);

		// Send verification email
		String verifyURL = siteURL + "/verify?code=" + verificationCode;
		try {
			commonUtil.sendVerificationEmail(savedUser.getEmail(), verifyURL);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return savedUser;
	}

	@Override
	public boolean verifyAccount(String verificationCode) {
		UserDtls user = userRepo.findByVerificationCode(verificationCode);
		if (user == null) {
			return false; // Invalid code
		}
		user.setIsEnable(true);
		user.setVerificationCode(null); // Remove verification code after activation
		userRepo.save(user);
		return true;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public List<UserDtls> getAllUsers(String role) {
		return userRepo.findByRole(role);

	}

	@Override
	public Boolean updateAccountStatus(int id, Boolean status) {
		Optional<UserDtls> findByUser = userRepo.findById(id);

		if (findByUser.isPresent()) {
			UserDtls userDtls = findByUser.get();
			userDtls.setIsEnable(status);
			userRepo.save(userDtls);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepo.save(user);

	}

	/*
	 * @Override public void userAccountLock(UserDtls user) {
	 * user.setAccountNonLocked(false); user.setLockTime(new Date());
	 * userRepo.save(user);
	 * 
	 * }
	 */

	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(LocalDateTime.now());
		userRepo.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		LocalDateTime lockTime = user.getLockTime();

		if (lockTime == null) {
			return false; // No lock time set, no need to unlock
		}

		// Unlock time = Lock time + 1 hour
		LocalDateTime unlockTime = lockTime.plus(AppConstant.UNLOCK_DURATION, ChronoUnit.MILLIS);

		// Getting the current time from the system
		LocalDateTime currentTime = LocalDateTime.now();

		if (currentTime.isAfter(unlockTime)) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepo.save(user);
			return true;
		}
		return false;
	}

	/*
	 * @Override public boolean unlockAccountTimeExpired(UserDtls user) { long
	 * lockTime = user.getLockTime().getTime();
	 * 
	 * // setting the time 1 hr for whhich user get unlock their account long
	 * unlockTime = lockTime + AppConstant.UNLOCK_DURATION;
	 * 
	 * // Getting the current time from the system long currentTime =
	 * System.currentTimeMillis();
	 * 
	 * if (unlockTime < currentTime) { user.setAccountNonLocked(true);
	 * user.setFailedAttempt(0); user.setLockTime(null); userRepo.save(user); return
	 * true; } return false; }
	 */

	/*
	 * @Override public boolean unlockAccountTimeExpired(UserDtls user) { if
	 * (user.getLockTime() == null) { return false; } LocalDateTime unlockTime =
	 * user.getLockTime().plus(AppConstant.UNLOCK_DURATION, ChronoUnit.MILLIS);
	 * return LocalDateTime.now().isAfter(unlockTime); }
	 */

	@Override
	public void resetAttempt(int userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		// TODO Auto-generated method stub
		UserDtls findByEmail = userRepo.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepo.save(findByEmail);

	}

	@Override
	public UserDtls getUserByTokens(String token) {
		return userRepo.findByResetToken(token);
	}

	@Override
	public UserDtls updateUser(UserDtls user) {
		return userRepo.save(user);
	}

	@Override
	public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {
		UserDtls dbUser = userRepo.findById(user.getId()).get();

		if (!img.isEmpty()) {
			
			String imageUrl = commonUtil.getImageUrl(img, BucketType.PROFILE.getId());
			
			
			dbUser.setProfileImage(imageUrl);
		}

		if (!ObjectUtils.isEmpty(dbUser)) {
			dbUser.setName(user.getName());
			dbUser.setMobileNumber(user.getMobileNumber());
			dbUser.setAddress(user.getAddress());
			dbUser.setCity(user.getCity());
			dbUser.setState(user.getState());
			dbUser.setPincode(user.getPincode());
			dbUser = userRepo.save(dbUser);
		}
		try {
			if (!img.isEmpty()) {
				/*
				 * *File saveFile = new ClassPathResource("static/img").getFile(); Path path =
				 * Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
				 * File.separator + img.getOriginalFilename()); // System.out.println(path);
				 * Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 */
				fileService.uploadFileS3(img, BucketType.PROFILE.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbUser;
	}

	@Override
	public UserDtls searchUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public Page<UserDtls> getAllUserPagination(Integer pageNo, Integer pageSize) {
		PageRequest pageble = PageRequest.of(pageNo, pageSize);
		return userRepo.findAll(pageble);
	}

	@Override
	public UserDtls saveAdmin(UserDtls user) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnable(true);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);
		return userRepo.save(user);
	}

	@Override
	public Page<UserDtls> getUsersByRoleWithPagination(String role, Integer pageNo, Integer pageSize) {

		PageRequest pageable = PageRequest.of(pageNo, pageSize);
		return userRepo.findByRole(role, pageable);
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepo.existsByEmail(email);

	}

}
