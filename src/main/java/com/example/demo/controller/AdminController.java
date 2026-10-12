package com.example.demo.controller;

import java.awt.desktop.UserSessionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Category;
import com.example.demo.entity.OrderDetails;
import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/myshop/admin")
public class AdminController {
	@Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
	private OrderService orderService;
	
  
  @Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;

    @ModelAttribute
    public void getUserDetails(HttpSession session, Model model) {

        User userDetail = (User) session.getAttribute("user");

        if (userDetail != null) { 
            model.addAttribute("user", userDetail);
            model.addAttribute("role", userDetail.getRole());
        } else {
            model.addAttribute("user", null);
            model.addAttribute("role", null);
        }
    }

	
	

	@GetMapping("/dashboard")
	public String dashboard() {
		return "admin/dashboard";
	}
	
	@GetMapping("/viewUsers")
	public String viewUsers(Model model) {
		List<User> users=userService.getUserbyRole();
		model.addAttribute("users",users);
		return "admin/users";
	}
	
	@GetMapping("/addAdmin")
	public String addAdmin(Model model) {
		 model.addAttribute("admin", new User());
		List<User> admins=userService.getAdminbyRole();
		model.addAttribute("admins",admins);
		

		return "admin/addAdmin";
	}
	
	@PostMapping("/saveAdmin")
	public String saveAdmin(@ModelAttribute("admin") User admin,HttpSession session) {
		String pass=admin.getPassword();
	    admin.setPassword(passwordEncoder.encode(pass));
		userService.saveAdmin(admin);

		return "redirect:/myshop/admin/addAdmin";
	}
	@GetMapping("/deleteAdmin/{id}")
	public String deleteAdmin(@PathVariable int id,HttpSession session) {
		userService.deleteAdmin(id);

		return "redirect:/myshop/admin/addAdmin";
	}
	
	
	@GetMapping("/editProfile")
	public String editProfile(@RequestParam int u_id,Model model) {
	User user=userService.GetByUserId(u_id);
	model.addAttribute("user",user);
	return "editUserProfile";
	}


@PostMapping("/updateProfile")
public String updateProfile(
		@ModelAttribute User user,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
	User usr=(User) session.getAttribute("user");
	String email=usr.getEmail();
	User oldUser=userService.getUserByEmail(email);
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
	return "redirect:/myshop/admin/editProfile?u_id=" + oldUser.getId();
}

@PostMapping("/changePassword")
public String changePassword(@RequestParam String oldPassword,@RequestParam String newPassword,HttpSession session) {
	
	User usr=(User) session.getAttribute("user");
	String email=usr.getEmail();
	User oldUser=userService.getUserByEmail(email);
	
	if(!passwordEncoder.matches(oldPassword, oldUser.getPassword())){
        return "redirect:/myshop/admin/editProfile?u_id=" + oldUser.getId();
	}
	oldUser.setPassword(passwordEncoder.encode(newPassword));
	userService.updateUser(oldUser);
    return "redirect:/myshop/admin/editProfile?u_id=" + oldUser.getId();
}

@GetMapping("deleteUser/{id}")
public String deleteUser(@PathVariable int id,HttpSession session) {
	userService.deleteUser(id);
	 return "redirect:/myshop/admin/viewUsers";
}

@GetMapping("/viewOrders")
public String viewOrder(Model model) {
	
	
	
	List<OrderDetails> orList=orderService.allOrderLisr();
	if(orList.isEmpty()) {
		model.addAttribute("orderEmpty",orList);

	}else {
		model.addAttribute("orderList",orList);

	}

	return "admin/customerOrders";
}
@PostMapping("/updateOrderStatus")
public String orderUpdateStatus(@RequestParam("orderId") int orderId,@RequestParam("status") String status) {
	orderService.orderStatusUpdate(orderId,status);
	return "redirect:/myshop/admin/viewOrders";
}
@GetMapping("/deleteUserAcc/{id}")
public String deleteUserAcc(@PathVariable int id,Model model) {
	userService.deleteUserAcc(id);
	List<User> users=userService.getUserbyRole();
	model.addAttribute("users",users);
	return"/admin/users";
}

}
