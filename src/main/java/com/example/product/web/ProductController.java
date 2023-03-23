package com.example.product.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductController {
  //등록양식
  @GetMapping("/add")
  public  String saveForm(){
    return "product/saveForm()";
  }
  //등록처리
  @PostMapping("/add")
  public String save(){
    return "redirect: /products/{id}/detail";
  }
  //조회
  @GetMapping("/{id}/detail")
  public String findById(){
    return "product/detailForm";
  }
  //수정양식
  @GetMapping("/{id}/edit")
    public String updateForm(){
    return "product/updateForm";
  }
  //수정
  @PostMapping("/{id}/edit")
  public String update(){
    return "redirect:/products/{id}/detail";
  }
  //삭제
  @GetMapping("/{id}/del")
  public String deleteById(){
    return "redirect:/products";
  }
  //목록
  @GetMapping
  public String findAll(){
    return "products/all";
  }

}
