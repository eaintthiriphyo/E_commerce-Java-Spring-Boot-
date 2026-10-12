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
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/myshop/admin")
public class ProductController {

    private final OrderService orderService;

	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CartService cartService;
	
	@Autowired
	private UserService userService;

    ProductController(OrderService orderService) {
        this.orderService = orderService;
    }
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


	@GetMapping("/addProduct")
	public String product(Model model) {
		List<Category> cats = categoryService.allCategoryList();
		model.addAttribute("catList", cats);
		return "admin/addProduct";
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,HttpSession session)
			throws IOException {

		
		
		Double discountPrice=product.getPrice();
		product.setDiscountPrice(discountPrice);
		try {
			String fileName=file.isEmpty()?"default.jpg":file.getOriginalFilename();
			
			product.setImage(fileName);
			Product saveProduct=productService.saveProduct(product);

			if(saveProduct !=null && !file.isEmpty()) {
				String uploadDir="uploads/product_img";
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

		return "redirect:/myshop/admin/viewProduct";
	}

	@GetMapping("/viewProduct")
	public String viewProduct(Model model) {
		List<Product> products = productService.allProduct();
		model.addAttribute("products", products);
		return "admin/viewProduct";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id) {
		productService.deleteProduct(id);
		return "redirect:/myshop/admin/viewProduct";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model model) {
		Product p = productService.getProductById(id);
		model.addAttribute("product", p);
		model.addAttribute("catList", categoryService.allCategoryList());
		return "admin/editProduct";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file) throws IOException {
		Product oldProduct = productService.getProductById(product.getId());
		
		
		oldProduct.setDiscount(product.getDiscount());
		oldProduct.setName(product.getName());
		oldProduct.setPrice(product.getPrice());
		oldProduct.setDescription(product.getDescription());
		oldProduct.setCategory(product.getCategory());
		oldProduct.setStock(product.getStock());

		
		Double discountPrice=product.getPrice();
		oldProduct.setDiscountPrice(discountPrice);
		
		try {
			String fileName=file.isEmpty()?oldProduct.getImage():file.getOriginalFilename();
			
			oldProduct.setImage(fileName);
			Product saveProduct=productService.saveProduct(oldProduct);

			if(saveProduct !=null && !file.isEmpty()) {
				String uploadDir="uploads/product_img";
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

		productService.saveProduct(oldProduct);
		return "redirect:/myshop/admin/viewProduct";
	}

	@GetMapping("/discount/{id}")
	public String viewAddDiscount(@PathVariable int id,Model model) {
		Product p = productService.getProductById(id);
		model.addAttribute("product", p);
		return "admin/discountPage";
	}
	@PostMapping("/addDiscount")
	public String addDiscount(@ModelAttribute Product p) {
		Product oldProduct=productService.getProductById(p.getId());
		Integer discount=p.getDiscount();
		oldProduct.setDiscount(discount);
		
	    Double discountPrice = oldProduct.getPrice() - (oldProduct.getPrice() * discount / 100.0);
		oldProduct.setDiscountPrice(discountPrice);
		productService.saveProduct(oldProduct);
		return "redirect:/myshop/admin/viewProduct";
	}
	
}
