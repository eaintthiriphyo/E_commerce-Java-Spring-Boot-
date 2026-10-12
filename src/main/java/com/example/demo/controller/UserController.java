package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Category;
import com.example.demo.entity.OrderDetails;
import com.example.demo.entity.Payment;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/myshop/user")
public class UserController {


	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;
	@Autowired
	private CartService cartService;
	@Autowired
	private  PaymentService paymentService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

   @Autowired
   private OrderService orderService;
   @ModelAttribute
	public void getUserDetails(HttpSession session, Model model) {

	    User userDetail = (User) session.getAttribute("user");
	    List<Category> catList=categoryService.allCategoryList();
		model.addAttribute("catList",catList);
	    if (userDetail != null) { 
	        model.addAttribute("user", userDetail);
	        model.addAttribute("role", userDetail.getRole());
			int countCart=cartService.getCartCount(userDetail.getId());
			model.addAttribute("countCart", countCart);
	    } else {
	        model.addAttribute("user", null);
	        model.addAttribute("role", null);
	    }
	}


	@GetMapping("/viewProduct/{id}")
	public String viewProduct(@PathVariable int id, Model model) {
		List<Category> catList=categoryService.allCategoryList();
		model.addAttribute("cartList",catList);
		Product product = productService.getProductById(id);
		model.addAttribute("product", product);
		return "user/viewProduct";
	}

	@GetMapping("/viewCart")
	public String viewCard(HttpSession session,Model model) {
		User user = (User) session.getAttribute("user");
		int u_id = user.getId();
		String u_email=user.getEmail();
		User getUser=userService.getUserByEmail(u_email);
		List<Cart> cartList=cartService.getCartByUser(u_id);
		model.addAttribute("cartEmpty", cartList.isEmpty());

		    model.addAttribute("cartList", cartList);
			model.addAttribute("user", getUser);
			boolean cartEmpty = cartList.isEmpty();
			Double finalPrice = 0.0;
		    model.addAttribute("cartEmpty", cartEmpty);
		    if (!cartEmpty) {
		        finalPrice = cartList.get(cartList.size() - 1).getTotalOrderPrice();
		    }		model.addAttribute("totalOrderPrice",finalPrice);
		
		List<Payment> p=paymentService.getPaymentList();

		model.addAttribute("payment",p);

		return "user/cart";
	}

	@PostMapping("/addToCard/{p_id}")
	public String addToCard(@PathVariable int p_id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		int u_id = user.getId();
		Cart saveCart = cartService.saveCart(p_id, u_id);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errCartMessage", "Product add to cart Fail");
		} 			

		return "redirect:/myshop/user/viewProduct/" + p_id;
	}
	@GetMapping("/updateQuantity")
	public String updateQuantity(@RequestParam String value,@RequestParam int c_id) {
		cartService.updateQuantity(value,c_id);
		return "redirect:/myshop/user/viewCart";
	}

	@GetMapping("/deleteCart")
	public String deleteCart(@RequestParam int c_id) {
		cartService.deleteCart(c_id);
		return "redirect:/myshop/user/viewCart";
	}
	
	@GetMapping("/editProfile")
		public String editProfile(@RequestParam int u_id,Model model) {
		User user=userService.GetByUserId(u_id);
		model.addAttribute("user",user);
		return "editUserProfile";
		}
	
	
	@PostMapping("/updateProfile")
	public String updateProfile(HttpSession session,@ModelAttribute UserDTO user,@RequestParam("file") MultipartFile file) throws IOException {
        User usr = (User) session.getAttribute("user");
        User oldUser = userService.getUserByEmail(usr.getEmail()); 
		oldUser.setName(user.getName());
		oldUser.setPhone(user.getPhone());
		oldUser.setAddress(user.getAddress());
		oldUser.setCity(user.getCity());
		oldUser.setState(user.getState());
		
			
		try {
			String fileName=file.isEmpty()?oldUser.getImage():file.getOriginalFilename();
			
			oldUser.setImage(fileName);
			User saveUser=userService.updateUser(oldUser);

			if(saveUser !=null && !file.isEmpty()) {
				String uploadDir="uploads/user_img";
				File dir=new File(uploadDir);
				if(!dir.exists()) {
					dir.mkdirs();
				}
				Path path = Paths.get(uploadDir, fileName); 
				try (InputStream inputStream = file.getInputStream()) {
				    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
				}

			}


		}catch (Exception e) {

		}
	    return "redirect:/myshop/";

	}
	
	@PostMapping("/changePassword")
	public String changePassword(HttpSession session,@RequestParam String oldPassword,@RequestParam String newPassword) {
		
		User usr = (User) session.getAttribute("user");
        User oldUser = userService.getUserByEmail(usr.getEmail()); 
		
		if(!passwordEncoder.matches(oldPassword, oldUser.getPassword())){
			session.setAttribute("errMessage", "Old password is incorrect");
	        return "redirect:/myshop/user/editProfile?u_id=" + oldUser.getId();
		}
		oldUser.setPassword(passwordEncoder.encode(newPassword));
		userService.updateUser(oldUser);
		session.setAttribute("succMessage", "Password changed successfully");
	    return "redirect:/myshop/";
	}
	@GetMapping("/deleteAccount/{id}")
	public String deleteAccount(@PathVariable int id,HttpSession session) {
		User user=userService.GetByUserId(id);
		userService.deleteUser(user.getId());
		 session.invalidate();
		return "redirect:/myshop/login/";
	}
	@GetMapping("/order/success")
	public String goSuccessPage()
	{
		return "success";
	}
	
	@PostMapping("/saveOrder")
	public String saveOrder(Principal p, @RequestParam Payment payment) {
		User user=userService.getUserByEmail(p.getName());
		orderService.saveOrder(user,payment);
        return "redirect:/myshop/user/order/success";

	}
	
	@GetMapping("/orderDetails")
	public String viewOrderDetails(HttpSession session,Model model){
		User user=(User)session.getAttribute("user");
		int u_id=user.getId();
		List<OrderDetails> orList=orderService.getOrderListByUserId(u_id);
		if(orList.isEmpty()) {
			model.addAttribute("orderEmpty",orList);

		}else {
			model.addAttribute("orderList",orList);

		}
		return "user/orderDetails";
	}
	
	
}


