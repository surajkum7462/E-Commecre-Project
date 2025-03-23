package com.ecom.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CategoryService categoryService;
	
	
	@Autowired
	private CartService cartService;
	
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	
	
	
	
	
	
	
	
	@ModelAttribute
	public void getUserDetails(Principal p,Model m)
	{
		if(p!=null)
		{
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user",userDtls);
			int countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart",countCart);
		}
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys",allActiveCategory);
	}
	
	
	@GetMapping("/addCart")
	public String addToCart(@RequestParam int pid , @RequestParam int uid,HttpSession session)
	{
	        Cart saveCart = cartService.saveCart(pid, uid);
	        if(ObjectUtils.isEmpty(saveCart))
	        {
	        	session.setAttribute("errorMsg", "Product add to cart failed");
	        }
	        else
	        {
	        	session.setAttribute("successMsg", "Product added to cart");
	        }
		
		return "redirect:/product/"+pid;
	}
	
	@GetMapping("/cart")
	public String loadCartPage(Principal p , Model m)
	{
		
		UserDtls user=getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts",carts);
		if(carts.size()>0) {
		m.addAttribute("totalOrderPrice",carts.get(carts.size()-1).getTotalOrderPrice());
		}
		return "/user/cart";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	@GetMapping("/cartQuantityUpdate")
	public String updatCartQuantity(@RequestParam String sy , @RequestParam int cid)
	{
		cartService.updateQuantity(sy,cid);
		return "redirect:/user/cart";
	}
	
	
	@GetMapping("/orders")
	public String orderPage(Principal p ,Model m)
	{
		UserDtls user=getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts",carts);
		if(carts.size()>0) {
		Double orderPrice=carts.get(carts.size()-1).getTotalOrderPrice();
		Double totalOrderPrice=carts.get(carts.size()-1).getTotalOrderPrice()+150+100;
		m.addAttribute("orderPrice",orderPrice);
		m.addAttribute("totalOrderPrice",totalOrderPrice);
		}
		return "/user/order";
	}
	
	@GetMapping("/success")
	public String success()
	{
		return "/user/success";
	}
	
	
	@PostMapping("/save-orders")
	public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal p) throws UnsupportedEncodingException, MessagingException
	{
		//System.out.println(orderRequest);
		UserDtls user = getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), orderRequest);
		
		return "redirect:/user/success";
	}
	
	
	@GetMapping("/user-orders")
	public String myOrder(Model m,Principal p)
	{
		UserDtls user = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(user.getId());
		m.addAttribute("orders",orders);
		return "/user/my_orders";
	}
	
	
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam int id , @RequestParam int st,HttpSession session)
	{
		String statusN = null;
		for (OrderStatus status : OrderStatus.values()) {
            if (status.getId() == st) {
            	statusN= status.getName();
            }
        }
		//System.out.println("Staus..........."+statusN);
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
		
		
		
		
		
		if(!ObjectUtils.isEmpty(updateOrder))
		{
			session.setAttribute("successMsg", "Status Updated");
		}
		else
		{
			session.setAttribute("errorMsg", "Status not updated");
		}
		
		return "redirect:/user/user-orders";
	}
	
	@GetMapping("/profile")
	public String profile()
	{
		
		
		return "/user/profile";
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
		
		return "redirect:/user/profile";
	}
	
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,
	                             Principal p, HttpSession session,RedirectAttributes redirectAttributes) {
	    UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

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
	        return "redirect:/user/profile";
	    }
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
