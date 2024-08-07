package com.the.man.product.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.the.man.common.model.vo.PageInfo;
import com.the.man.common.template.Pagination;
import com.the.man.product.model.service.ProductService;
import com.the.man.product.model.vo.Product;
import com.the.man.product.model.vo.ProductPhoto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {
	
	
	private final ProductService productService;
	
	@GetMapping("padding")
	public String padding() {
		return "product/padding";
	}
	
	@GetMapping("productUproad")
	public String productUproad() {
		return "product/productUproad";
	}
	
	public String saveFile(MultipartFile upfile, HttpSession session) {
	    String originName = upfile.getOriginalFilename();
	    String ext = originName.substring(originName.lastIndexOf("."));
	    String uuid = UUID.randomUUID().toString(); // UUID 생성
	    String changeName = uuid + ext; // UUID와 파일 확장자를 결합하여 고유한 파일 이름 생성
	    String savePath = session.getServletContext().getRealPath("/resources/productFile/");
	    
	    try {
	        upfile.transferTo(new File(savePath + changeName));
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return "resources/productFile/" + changeName;
	}
	
	
	
	@PostMapping("/insert")
	public String insert(@ModelAttribute Product product,
					     @RequestParam("upfiles1") MultipartFile[] upfiles1,
					     @RequestParam("upfiles2") MultipartFile[] upfiles2,
					     HttpSession session, Model model) {
		
		System.out.println("제품명 : " + product.getProductName());
		
		productService.insert(product);
		//int eventNo = event.getEventNo();
		
		int productNo = product.getProductNo();
		System.out.println("제품번호 : " + productNo);

		
		if(productNo > 0) {
			List<MultipartFile> allFiles = new ArrayList<MultipartFile>();
			
			for(MultipartFile file : upfiles1) {
				if(!file.isEmpty()) {
					allFiles.add(file);
				}
			}
			
			for(MultipartFile file : upfiles2) {
				if(!file.isEmpty()) {
					allFiles.add(file);
				}
			}
			
			for(int i = 0; i < allFiles.size(); i++) {
				MultipartFile upfile = allFiles.get(i);
				String originName = upfile.getOriginalFilename();
				String changeName = saveFile(upfile, session);
				
				ProductPhoto productPhoto = new ProductPhoto();
				
				productPhoto.setProductNo(productNo);
				productPhoto.setOriginName(originName);
				productPhoto.setChangeName(changeName);
				
				if(i < upfiles1.length) {
					productPhoto.setFileLevel(1);
				} else {
					productPhoto.setFileLevel(2);
				}
				
				productService.insertImg(productPhoto);
			}
			session.setAttribute("alertMsg", "성공");
			return "redirect:/";
		} else {
			model.addAttribute("errorMsg", "실패");
			return "common/errorPage";
		}
	}
	
	@GetMapping
	public String allEvents(@RequestParam(value="page", defaultValue="1") int page, Model model) {
		PageInfo pi = Pagination.getPageInfo(productService.selectListCount(), page, 6, 5);
		model.addAttribute("event", productService.allEvents(pi));
		model.addAttribute("pageInfo", pi);
		
		return "event/eventList";
	}
	
	

}
