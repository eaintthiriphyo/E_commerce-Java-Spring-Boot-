package com.example.demo.controller;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.hibernate.bytecode.enhance.spi.interceptor.SessionAssociationMarkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/myshop")
public class HomeController {


	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CartService cartService;

	@ModelAttribute
	public void getUserDetails(HttpSession session, Model model) {

	    User userDetail = (User) session.getAttribute("user");
	    List<Category> catList=categoryService.allCategoryList();
		List<Product> products=productService.allProduct();

		model.addAttribute("catList",catList);
	    if (userDetail != null) { 
	        model.addAttribute("user", userDetail);
	        model.addAttribute("role", userDetail.getRole());
	        model.addAttribute("products", products);

			int countCart=cartService.getCartCount(userDetail.getId());
			model.addAttribute("countCart", countCart);
	    } else {
	        model.addAttribute("user", null);
	        model.addAttribute("role", null);
	    }
	}


	
	@GetMapping("/login")
	public String login(){
		
		return "login";
	}
	
	
	@GetMapping("/")
	public String home(Model model) {
		List<Product> products=productService.allProduct();
        model.addAttribute("products", products);

		return "home";
	}
	
	
	@GetMapping("/assign")
	public String register(Model model){
	    model.addAttribute("users", new User());
		return "register";
	}
	
	
	@PostMapping("/addRegister")
	public String saveRegister(@Valid @ModelAttribute("users") UserDTO users,BindingResult result,@RequestParam("file") MultipartFile file,HttpSession session,Model model) throws Throwable {
	    System.out.println("Password"+users.getPassword()+" Confrim"+users.getConfirmPassword()+""+users.getName());

		String pass=users.getPassword();
		String conPass=users.getConfirmPassword();
		  if (!pass.equals(conPass)) {
		        model.addAttribute("users", users);
		        return "register";
		    }
		    users.setPassword(passwordEncoder.encode(users.getPassword()));
		    users.setConfirmPassword(passwordEncoder.encode(users.getConfirmPassword()));

		    if (userService.existsByEmail(users.getEmail())) {
		        model.addAttribute("error", "Email already exists!");
		        model.addAttribute("users", users);
		        return "register"; 
		    }
		    

		    
//	
		 if (result.hasErrors()) {
		        model.addAttribute("users", users); 
		        return "register";
		    }

		try {
			String fileName=file.isEmpty()?"default.jpg":file.getOriginalFilename();
			
			users.setImage(fileName);
			User saveUser=userService.saveRegister(users);

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
	        return "redirect:/myshop/register";
		}

        return "redirect:/myshop/login";
	}
	@GetMapping("/product")
	public String product(Model model,@RequestParam(value="category",defaultValue="") String category) { 
		System.out.println("category"+category);
		List<Product> products=productService.allProduct();
		List<Category> categories=categoryService.allCategoryList();
		List<Product> proList;
		
		if(category == null || category.isEmpty() || "All".equals(category)) {
		    proList = products;
		} else {
		    proList = productService.productByCategory(category);
		}
		model.addAttribute("products",proList);
		model.addAttribute("catList",categories);

		return "/product";
	}
	
	@GetMapping("/searchProducts")
	public String searchProduct(@RequestParam String keyword,Model model) {
		List<Product> product=productService.searchProduct(keyword);
		 model.addAttribute("products", product);
		 model.addAttribute("keyword", keyword);
			return "product";

}
	
	}
