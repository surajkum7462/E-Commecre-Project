package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.FileService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.BucketType;
import com.ecom.util.CommonUtil;

import ch.qos.logback.core.util.StringUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CartService cartService;
	
	
	@Autowired
	private FileService fileService;
	
	
	@GetMapping("/aboutme")
	public String about()
	{
		return "about";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			int countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/")
	public String index(Model m) {
		List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
				.sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId()))
				.limit(10).toList();
		List<Product> allActiveProduct = productService.getAllActiveProducts("").stream()
				
				.sorted((p1, p2) -> Integer.compare(p2.getId(), p1.getId()))

				.limit(10).toList();
		m.addAttribute("category",allActiveCategory);
		m.addAttribute("products",allActiveProduct);
		
		
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(Model m, 
			@RequestParam(value = "category", defaultValue = "") String category ,
			@RequestParam(name="pageNo" , defaultValue = "0") Integer pageNo ,
			@RequestParam(name="pageSize" , defaultValue = "6") Integer pageSize,@RequestParam(defaultValue = "") String ch) {
		// System.out.println("Category="+category);
		List<Category> categories = categoryService.getAllActiveCategory();
	
		m.addAttribute("paramVal", category);
		m.addAttribute("categories", categories);
		//Pagination
		
		/*
		 * List<Product> products = productService.getAllActiveProducts(category);
		 * m.addAttribute("products", products);
		 */
		Page<Product> page=null;
		if(StringUtils.isEmpty(ch))
		{
			page=productService.getAllActiveProductPagination(pageNo, pageSize, category);
		}
		else
		{
			page=productService.searchActiveProductPagination(pageNo,pageSize,category,ch);
		}
		
		
		
		//Page<Product> page = productService.getAllActiveProductPagination(pageNo, pageSize ,category);
		List<Product> products = page.getContent();
		m.addAttribute("products",products);
		m.addAttribute("productsSize",products.size());
		m.addAttribute("pageNo",page.getNumber());
		m.addAttribute("totalElements",page.getTotalElements());
		m.addAttribute("totalPages",page.getTotalPages());
		m.addAttribute("isFirst",page.isFirst());
		m.addAttribute("isLast",page.isLast());
		m.addAttribute("pageSize",pageSize);
		

		return "product";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);

		m.addAttribute("product", productById);
		return "view_product";
	}

	// User Details save implementation

	/*
	 * @PostMapping("/saveUser") public String saveUser(@ModelAttribute UserDtls
	 * user, @RequestParam("img") MultipartFile file, HttpSession session) throws
	 * IOException {
	 * 
	 * Boolean existsEmail=userService.existsEmail(user.getEmail());
	 * 
	 * if(existsEmail) { session.setAttribute("errorMsg",
	 * "User Email Already Exists"); } else { String imageName = file.isEmpty() ?
	 * "default.jpg" : file.getOriginalFilename(); user.setProfileImage(imageName);
	 * UserDtls saveUser = userService.saveUser(user);
	 * 
	 * if (!ObjectUtils.isEmpty(saveUser)) { if (!file.isEmpty()) { File saveFile =
	 * new ClassPathResource("static/img").getFile(); Path path =
	 * Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
	 * File.separator + file.getOriginalFilename()); System.out.println(path);
	 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	 * 
	 * } session.setAttribute("successMsg", "Registration Successfully");
	 * 
	 * } else { session.setAttribute("errorMsg", "Something went Wrong on server");
	 * }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * return "redirect:/register"; }
	 */
	
   /*----------------*/
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session, HttpServletRequest request)
	        throws IOException {

	    Boolean existsEmail = userService.existsEmail(user.getEmail());

	    if (existsEmail) {
	        session.setAttribute("errorMsg", "User Email Already Exists");
	    } else {
	       //* String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
	    	
	    	String imageUrl = commonUtil.getImageUrl(file, BucketType.PROFILE.getId());
	        user.setProfileImage(imageUrl);

	        // Generate Verification Code
	        String verificationCode = UUID.randomUUID().toString();
	        user.setVerificationCode(verificationCode);
	        user.setIsEnable(false);  // User disabled until verified

	        String siteURL = CommonUtil.generateUrl(request);
	        userService.saveUser(user, siteURL);  // Save user and send email

	        if (!file.isEmpty()) {
				/*
				 * *File saveFile = new ClassPathResource("static/img").getFile(); Path path =
				 * Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
				 * File.separator + file.getOriginalFilename());
				 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 */
	        	fileService.uploadFileS3(file, BucketType.PROFILE.getId());
	        	
	        }
	        session.setAttribute("successMsg", "Registration successful! Please check your email for verification.");
	    }

	    return "redirect:/register";
	}

	@GetMapping("/verify")
	public String verifyAccount(@RequestParam("code") String code, Model model) {
	    boolean verified = userService.verifyAccount(code);

	    if (verified) {
	        model.addAttribute("msg", "Your account has been successfully verified. You can now log in.");
	    } else {
	        model.addAttribute("msg", "Invalid or expired verification link.");
	    }

	    return "message";
	}

	
	
	
	
	
	/*----------------*/
	
	
	
	

	// Forgot Password Implementation

	@GetMapping("/forgot-password")
	public String showForgotPassword() {

		return "forgot_password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {
		UserDtls userByTokens = userService.getUserByTokens(token);
		if (ObjectUtils.isEmpty(userByTokens)) {
			m.addAttribute("msg", "Your Link is invalid");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model m) {
		UserDtls userByTokens = userService.getUserByTokens(token);
		if (ObjectUtils.isEmpty(userByTokens)) {
			m.addAttribute("msg", "Your Link is invalid");
			return "error";
		} else {
			userByTokens.setPassword(passwordEncoder.encode(password));
			userByTokens.setResetToken(null);
			userService.updateUser(userByTokens);
			// session.setAttribute("successMsg", "Password changed successfully");
			m.addAttribute("msg", "Password changed successfully");
			return "message";
		}

	}

	// Sending the email link

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		UserDtls userByEmail = userService.getUserByEmail(email);
		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Email is not present");
		} else {
			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);
			// Generate URL in email like this :
			// http://localhost:8080/reset-password?token=kihfiuyfgferjhiy434jkjgu

			// Here we have find http://localhost:8080/forgot-password
			// this url but we set reset-passord in url so see geneareteUrl method
			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMail(url, email);
			if (sendMail) {

				session.setAttribute("successMsg", "Password resetting link is sent in your email");
			} else {
				session.setAttribute("errorMsg",
						"Something Went wrong on server! Verification link is not sent! Please After Some time");
			}
		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/search")
	public String searchProduct(@RequestParam String ch, Model m) {
		List<Product> searchProduct = productService.searchProduct(ch);
		m.addAttribute("products", searchProduct);
		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories", categories);

		return "product";
	}

}
