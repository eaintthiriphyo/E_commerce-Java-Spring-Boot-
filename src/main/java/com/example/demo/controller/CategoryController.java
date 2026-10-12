package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/myshop/admin/")
public class CategoryController {
@Autowired
private CategoryService categoryService;
@Autowired
private CartService cartService;

@Autowired
private UserService userService;
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


	@GetMapping("/category")
	public String category(Model model) {
		List<Category> categoryList=categoryService.allCategoryList();
		model.addAttribute("categoryList",categoryList);
		return "admin/category";
	}
	
	@PostMapping("/addCategory")
		public String addCategory(@ModelAttribute Category category,HttpSession session,@RequestParam("file") MultipartFile file) throws IOException 
	{
		
		boolean flag=categoryService.existsByTitle(category.getTitle());
		if(flag) {
	        return "redirect:/myshop/admin/category";

		}else {
			try {
				String fileName=file.isEmpty()?"default.jpg":file.getOriginalFilename();
				category.setImage(fileName);
				Category saveCategory=categoryService.addCategory(category);
				if(saveCategory !=null && !file.isEmpty()) {
					String uploadDir="uploads/category_img";
					File dir=new File(uploadDir);
					if(!dir.exists()) {
						dir.mkdirs();
					}
					Path path = Paths.get(uploadDir, fileName); // uploadDir is your folder path
					try (InputStream inputStream = file.getInputStream()) {
					    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
					}

				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		

		return "redirect:/myshop/admin/category";
	}
	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id,HttpSession session) {
		boolean flag=categoryService.deleteCategoryById(id);	
	
		return "redirect:/myshop/admin/category";
		
	}
	@GetMapping("/editCategory/{id}")
	public String editCategory(@PathVariable int id,Model model) {
		Category category=categoryService.getCategoryById(id);
		model.addAttribute("category",category);
		return "admin/editCategory";

	}
	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file) throws IOException {
		Category oldCat=categoryService.getCategoryById(category.getId());
		oldCat.setTitle(category.getTitle());
		try {
			String fileName=file.isEmpty()?oldCat.getImage():file.getOriginalFilename();
			oldCat.setImage(fileName);
			Category saveCategory=categoryService.addCategory(oldCat);
			if(saveCategory !=null && !file.isEmpty()) {
				String uploadDir="uploads/category_img";
				File dir=new File(uploadDir);
				if(!dir.exists()) {
					dir.mkdirs();
				}
				Path path = Paths.get(uploadDir, fileName); // uploadDir is your folder path
				try (InputStream inputStream = file.getInputStream()) {
				    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
				}

			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		return "redirect:/myshop/admin/category";

	}
	
	
}
