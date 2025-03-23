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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.repo.ProductRepo;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.FileService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.BucketType;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private FileService fileService;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ProductService productService;
	private Boolean deleteProduct;
	
	//Aws
	

	@GetMapping("/")
	public String index() {
		return "admin/index";
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

	// Add category Implementation

	@GetMapping("/category")
	public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize) {
		// m.addAttribute("categorys", categoryService.getAllCategory());

		Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);
		List<Category> categorys = page.getContent();
		m.addAttribute("categorys", categorys);
		// m.addAttribute("productsSize",products.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		m.addAttribute("pageSize", pageSize);
		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, HttpSession session, @RequestParam MultipartFile file)
			throws IOException {
		//*String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        //Aws
		
		String imageUrl = commonUtil.getImageUrl(file, BucketType.CATEGORY.getId());
		
		
		category.setImageName(imageUrl);
		
		
		
		// https://suraj-shopping-cart-category.s3.eu-north-1.amazonaws.com/wash.jpeg
		
		
		
		
		
		
		
		

		boolean existCategory = categoryService.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Category name Already Exists");
		} else {

			Category saveCategory = categoryService.saveCategory(category);
			// saveCategoty==null is same
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved ! Internal Server error");
			} else {
				/*
				 *  *File saveFile = new ClassPathResource("static/img").getFile(); Path path =
				 * Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" +
				 * File.separator + file.getOriginalFilename()); System.out.println(path);
				 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 */
				// Starting AWS deploy
				fileService.uploadFileS3(file, 1);
				
				
				session.setAttribute("successMsg", "Saved Successfully");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("successMsg", "Deleted Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Wrong on Server");
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, HttpSession session, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));

		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());
		//*String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		//Aws
		String imageUrl = commonUtil.getImageUrl(file, BucketType.CATEGORY.getId());
		
		
		if (!ObjectUtils.isEmpty(category)) {
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageUrl);

		}

		Category updateCategory = categoryService.saveCategory(oldCategory);
		if (!ObjectUtils.isEmpty(updateCategory)) {
			if (!file.isEmpty()) {
				/*
				 * *File saveFile = new ClassPathResource("static/img").getFile(); Path path =
				 * Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" +
				 * File.separator + file.getOriginalFilename()); System.out.println(path);
				 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 */
				//Aws
				fileService.uploadFileS3(file, 1);
			}
			session.setAttribute("successMsg", "Updated Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Went wrong on server");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	// End category Implementation

	// Add Product Implementation

	

	@GetMapping("/loadAddProduct")
	public String loadAdddProduct(Model m) {
		List<Category> allCategory = categoryService.getAllCategory();
		m.addAttribute("categories", allCategory);
		return "admin/add_product";
	}

	@PostMapping("/saveProduct")
	public String addProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		//*String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		String imageUrl = commonUtil.getImageUrl(image, BucketType.PRODUCT.getId());
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		product.setImage(imageUrl);

		Product saveProduct = productService.saveProduct(product);
		if (!ObjectUtils.isEmpty(saveProduct)) {
			/*
			 * *File saveFile = new ClassPathResource("static/img").getFile(); Path path =
			 * Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" +
			 * File.separator + image.getOriginalFilename()); // System.out.println(path);
			 * Files.copy(image.getInputStream(), path,
			 * StandardCopyOption.REPLACE_EXISTING);
			 */
			fileService.uploadFileS3(image, BucketType.PRODUCT.getId());
			session.setAttribute("successMsg", "Saved Successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
		}

		return "redirect:/admin/loadAddProduct";
	}

	// End Add Products Implementation

	// Start View Product Implementation

	@GetMapping("/products")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize) {

		/*
		 * if (ch != null && ch.length() > 0) { products =
		 * productService.searchProduct(ch); } else { products =
		 * productService.getAllProducts(); }
		 */
		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}

		m.addAttribute("products", page.getContent());
		// m.addAttribute("productsSize",products.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		m.addAttribute("pageSize", pageSize);

		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("successMsg", "Deleted Succesfully");
		} else {
			session.setAttribute("errorMsg", "Something Went Wrong on Server");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, HttpSession session,
			@RequestParam("file") MultipartFile image) {
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("successMsg", "Updated Succesfully");
			} else {
				session.setAttribute("errorMsg", "Something Went Wrong on Server");
			}
		}

		return "redirect:/admin/editProduct/" + product.getId();
	}

	@GetMapping("/users")
	public String getAllUser(Model m,@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize,@RequestParam String role) {
		//List<UserDtls> allUsers = userService.getAllUsers("ROLE_USER");
		//m.addAttribute("users", allUsers);
		
		Page<UserDtls> page = userService.getUsersByRoleWithPagination(role, pageNo, pageSize);
		
		m.addAttribute("users", page.getContent()); //
		m.addAttribute("seus",false);
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		m.addAttribute("pageSize", pageSize);
		
		m.addAttribute("role",role);
		
		
		
		
		
		
		
		
		return "/admin/users";
	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, 
	                                      @RequestParam int id, 
	                                      @RequestParam String role,
	                                      @RequestParam int pageNo, 
	                                      @RequestParam(defaultValue = "3") int pageSize, 
	                                      HttpSession session) {
	    Boolean f = userService.updateAccountStatus(id, status);
	    
	    if (f) {
	        session.setAttribute("successMsg", "Account Status updated");
	    } else {
	        session.setAttribute("errorMsg", "Something Went Wrong on Server");
	    }

	    return "redirect:/admin/users?role=" + role + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
	}



	@GetMapping("/orders")
	public String orders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize) {
		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);

		m.addAttribute("orders", page.getContent()); //
		m.addAttribute("srch", false);
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());
		m.addAttribute("pageSize", pageSize);
         
		
		

		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam int id, @RequestParam int st, HttpSession session) {
		String statusN = null;
		for (OrderStatus status : OrderStatus.values()) {
			if (status.getId() == st) {
				statusN = status.getName();
			}
		}
		System.out.println("Staus..........." + statusN);
		ProductOrder updateOrder = orderService.updateOrderStatus(id, statusN);
		try {
			commonUtil.sendMailProductOrder(updateOrder, statusN);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("successMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "Status not updated");
		}

		return "redirect:/admin/orders";
	}

	@GetMapping("/search-order")
	public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize) {
		if (orderId != null  && orderId.length()>0) {
			ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());
			if (ObjectUtils.isEmpty(order)) {
				session.setAttribute("errorMsg", "Order ID is not present");
				m.addAttribute("orderDtls", null);
			} else {
				m.addAttribute("orderDtls", order);
			}
			m.addAttribute("srch", true);
		} else {
			/*
			 * List<ProductOrder> allOrders = orderService.getAllOrders();
			 * m.addAttribute("orderDtls", allOrders); m.addAttribute("srch", false);
			 */
			
			Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
			m.addAttribute("orders", page);
			m.addAttribute("srch", false);//

			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());
			m.addAttribute("pageSize", pageSize);	
			
			
			
			
		}
		return "/admin/orders";
	}
	
	@GetMapping("/search-user")
    public String searchUser(
            @RequestParam(required = false) String email, 
            @RequestParam String role, // ✅ Set default role
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize,
            Model m, HttpSession session) {

        // If email is not provided, show paginated users
        if (email == null || email.trim().isEmpty()) {
            Page<UserDtls> page = userService.getAllUserPagination(pageNo, pageSize);

            // Add pagination attributes
            m.addAttribute("users", page.getContent());
            m.addAttribute("seus", false);
            m.addAttribute("pageNo", page.getNumber());
            m.addAttribute("totalElements", page.getTotalElements());
            m.addAttribute("totalPages", page.getTotalPages());
            m.addAttribute("isFirst", page.isFirst());
            m.addAttribute("isLast", page.isLast());
            m.addAttribute("pageSize", pageSize);
            m.addAttribute("userch", null); // Reset search result

            // ✅ Redirect with role parameter
            return "redirect:/admin/users?role=" + role + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
        } else {
            email = email.trim();
            UserDtls user = userService.searchUserByEmail(email);

            if (ObjectUtils.isEmpty(user)) {
                session.setAttribute("errorMsg", "User Email doesn't exist");
                m.addAttribute("userch", null);
            } else {
                m.addAttribute("userch", user);
            }

            m.addAttribute("seus", true);
            m.addAttribute("role", role); // ✅ Pass role to the model
        }

        return "/admin/users";
    }
	
	@GetMapping("/add-admin")
	public String loadAdminAdd()
	{
		return "/admin/add_admin";
	}
	
	
	
	@PostMapping("/save-admin")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {
		//*String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		String imageUrl = commonUtil.getImageUrl(file, BucketType.PROFILE.getId());
		user.setProfileImage(imageUrl);
		UserDtls saveUser = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				/*
				 * *File saveFile = new ClassPathResource("static/img").getFile(); Path path =
				 * Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" +
				 * File.separator + file.getOriginalFilename()); System.out.println(path);
				 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 */
				fileService.uploadFileS3(file, BucketType.PROFILE.getId());

			}
			session.setAttribute("successMsg", "Admin add Successfully");

		} else {
			session.setAttribute("errorMsg", "Something went Wrong on server");
		}

		return "redirect:/admin/add-admin";
	}
	
	@GetMapping("/profile")
	public String profile()
	{
		return "admin/profile";
	}
	
	
	
	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user,@RequestParam MultipartFile img,HttpSession session)
	{
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if(ObjectUtils.isEmpty(updateUserProfile))
		{
			session.setAttribute("successMsg", "Profile not updated");
			
		}
		else
		{
			session.setAttribute("successMsg", "Profile Updated");
		}
		
		return "redirect:/admin/profile";
	}
	
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,
	                             Principal p, HttpSession session,RedirectAttributes redirectAttributes) {
	    UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

	    boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
	    if (matches) {
	        String encodePassword = passwordEncoder.encode(newPassword);
	        loggedInUserDetails.setPassword(encodePassword);
	        UserDtls updateUser = userService.updateUser(loggedInUserDetails);

	        if (ObjectUtils.isEmpty(updateUser)) {
	            session.setAttribute("errorMsg", "Password not Updated");
	            return "redirect:/user/profile";
	        } else {
	        	redirectAttributes.addFlashAttribute("successMsg", "Password Updated. Please login again.");
	            session.invalidate(); // Destroy the session and logout the user
	            return "redirect:/signin"; // Redirect to the login page
	        }
	    } else {
	        session.setAttribute("errorMsg", "Current Password incorrect");
	        return "redirect:/admin/profile";
	    }
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
