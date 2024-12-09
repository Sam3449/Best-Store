package com.sp.BestStore.controller;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sp.BestStore.models.Product;
import com.sp.BestStore.models.ProductDto;

import com.sp.BestStore.services.ProductRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductsController {
	@Autowired
	private ProductRepository repo;

	@GetMapping({ "", "/" })
	public String showProductList(Model model) {
		List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("products", products);
		return "products/index";
	}

	@GetMapping("/create")
	public String showCreatePage(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto);
		return "products/CreateProduct";
	}

	@PostMapping("/create")
	public String CreateProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
		if (productDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
		}

		if (result.hasErrors()) {
			return "products/CreateProducts";
		}

		// save image file

		MultipartFile image = productDto.getImageFile();
		Date createdAt = new Date();
		String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		Product product = new Product();
		
		product.setName(productDto.getName());
		product.setBrand(productDto.getBrand());
		product.setCategory(productDto.getcategory());
		product.setPrice(productDto.getPrice());
		product.setDescription(productDto.getDescription());
		product.setCreatedAt(createdAt);
		product.setImageFileName(storageFileName);
		
		repo.save(product);
		

		return "redirect:/products";

	}
	
	@GetMapping("/edit")
	public String showEditPage(Model model,@RequestParam Long id) {
		
		try {
			Product product = repo.findById(id).get();
			model.addAttribute("product" , product);
			
			ProductDto productDto = new ProductDto();
			productDto.setName(product.getName());
			productDto.setBrand(product.getBrand());
			productDto.setcategory(product.getCategory());
			productDto.setPrice(product.getPrice());
			productDto.setDescription(product.getDescription());
			
			model.addAttribute("productDto", productDto);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			return "redirect:/products";
		}
		
		
		return "products/EditProduct";
	}
	
	@PostMapping("/edit")
	public String upfateProduct(Model model,
			@RequestParam Long id,
			@Valid @ModelAttribute ProductDto productDto,
			BindingResult result) {
		
		try {
			Product product = repo.findById(id).get();
			model.addAttribute("product",product);
			if(result.hasErrors()) {
				return "products/EditProduct";
			}
			
			if(!productDto.getImageFile().isEmpty()) {
			String uploadDir = "public/image";
			Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
			try {
				Files.delete(oldImagePath);
			} catch (Exception e) {
				System.out.println("Exception :" + e.getMessage());
			}
			MultipartFile image = productDto.getImageFile();
			Date createdAt = new Date();
			
			String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
			try(InputStream inputstream = image.getInputStream()){
				Files.copy(inputstream, Paths.get(storageFileName + uploadDir),
						StandardCopyOption.REPLACE_EXISTING);
			}
			product.setImageFileName(storageFileName);
			
			}
			product.setName(productDto.getName());
			product.setBrand(productDto.getBrand());
			product.setCategory(productDto.getcategory());
			product.setPrice(productDto.getPrice());
			product.setDescription(productDto.getDescription());

			repo.save(product);
			
			
			
			
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
		return "redirect:/products";
	}
	
	@GetMapping("/delete")
	public String deleteProduct(@RequestParam Long id) {
		
		try {
			Product product = repo.findById(id).get();
			
			Path imagePath = Paths.get("public/images/" + product.getImageFileName());
			
			try {
				Files.delete(imagePath);
			}
			catch (Exception e) {
				System.out.println("Exception : "+ e.getMessage());

			}
			
			repo.delete(product);
		} catch (Exception e) {
			System.out.println("Esception : "+ e.getMessage());
		}
		return "redirect:/products";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}