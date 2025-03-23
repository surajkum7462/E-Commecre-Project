package com.ecom.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;


@Component
public class CommonUtil {
	
	@Autowired
	private  JavaMailSender mailSender; 
	
	//Aws
    @Value("${aws.s3.bucket.category}")
	private String categoryBucket;

	@Value("${aws.s3.bucket.product}")
	private String productBucket;

	@Value("${aws.s3.bucket.profile}")
	private String profileBucket;

	
	
	@Autowired
	private UserService userService;

	public  Boolean sendMail(String url , String reciepentEmail) throws UnsupportedEncodingException, MessagingException {
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("kumarsuraj7462998828@gmail.com", "Suraj E-Commerce");
		
		helper.setTo(reciepentEmail);
		
		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password</p>"
				+ "<p>Click the link below to change your password:</p>"+"<p><a href=\""+url
				+"\">Change my Password</a></p>";
		
		helper.setSubject("Password Reset");
		helper.setText(content,true);
		mailSender.send(message);
		
		
		
		
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {
		// It will give https://loclahost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();
		// It will give https://loclahost:8080/
		return siteUrl.replace(request.getServletPath(),"");
		 
	}
	
	
	String msg=null;
	
	public Boolean sendMailProductOrder(ProductOrder order,String statusCode) throws MessagingException, UnsupportedEncodingException
	{
		
		msg = "<p>Hello [[name]]</p><br><p>Thank You! Your Order is <b>[[orderStatus]].</b></p>"
			      +"<p><b>Product Details:<b></p>"
			      +"<p>Name : [[productName]]</p>"
			      +"<p>Category : [[category]]</p>"
			      +"<p>Quantity : [[quantity]]</p>"
			      +"<p>Price : [[price]] </p>"
			      +"<p>Payment Type : [[paymentType]]</p>";
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("kumarsuraj7462998828@gmail.com", "Suraj E-Commerce");
		
		helper.setTo(order.getOrderAddress().getEmail());
		
		msg=msg.replace("[[name]]", order.getOrderAddress().getFirstName());
		msg=msg.replace("[[orderStatus]]", statusCode);
			
		
		
		
		msg=msg.replace("[[productName]]", order.getProduct().getTitle());
		msg=msg.replace("[[category]]", order.getProduct().getCategory());
		msg=msg.replace("[[quantity]]", order.getQuantity().toString());
		msg=msg.replace("[[price]]", order.getPrice().toString());
		msg=msg.replace("[[paymentType]]", order.getPaymentType());
		
		
		helper.setSubject("Product Order Status");
		helper.setText(msg,true);
		mailSender.send(message);
		
		
		
		
		return true;
	}
	
	
	public  UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	
	
	public Boolean sendVerificationEmail(String recipientEmail, String url) throws UnsupportedEncodingException, MessagingException {
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);

	    helper.setFrom("kumarsuraj7462998828@gmail.com", "Suraj E-Commerce");
	    helper.setTo(recipientEmail);
	    helper.setSubject("Email Verification");

	    String content = "<p>Hello,</p>" +
	            "<p>Thank you for registering. Please click the link below to verify your email:</p>" +
	            "<p><a href=\"" + url + "\">Verify my Email</a></p>";

	    helper.setText(content, true);
	    mailSender.send(message);

	    return true;
	}

	
	
	public String getImageUrl(MultipartFile file,Integer bucketType)
	{
		String bucketName=null;
		
		
		if (bucketType == 1) {
			bucketName = categoryBucket;
		} else if (bucketType == 2) {
			bucketName = productBucket;
		} else {
			bucketName = profileBucket;
		}
		// https://suraj-shopping-cart-category.s3.eu-north-1.amazonaws.com/wash.jpeg
		
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		
		
		String url="https://"+bucketName+".s3.eu-north-1.amazonaws.com/"+imageName;
		return url;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
