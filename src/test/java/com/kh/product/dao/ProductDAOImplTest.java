package com.kh.product.dao;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@SpringBootTest
public class ProductDAOImplTest {

  @Autowired
  ProductDAO productDAO;

  //등록
  @Test
  void save() {
    Product product = new Product();
    product.setPname("복사기");
    product.setQuantity(10L);
    product.setPrice(1000000L);

    Long productId = productDAO.save(product);
    log.info("findedProduct={}", productId);
    Assertions.assertThat(productId).isGreaterThan(0L);
//    Optional<Product> findedProduct = productDAO.findById(productId);
  }

  //조회
  @Test
  void findById() {
    Long productId = 288L;
    Optional<Product> product = productDAO.findById(productId);
//    if(product.isPresent()) {
//      log.info("product={}", product.get());
//    }else{
//      log.info("조회한 결과 없음");
//    }
//    Assertions.assertThat(product.stream().count())
//        .isEqualTo(1);
    Product findedProduct = product.orElseThrow();// 없으면 java.util.NoSuchElementException
    Assertions.assertThat(findedProduct.getPname()).isEqualTo("컴퓨터");
    Assertions.assertThat(findedProduct.getQuantity()).isEqualTo(1L);
    Assertions.assertThat(findedProduct.getPrice()).isEqualTo(100L);
  }

  //수정
  @Test
  @DisplayName("상품수정")
  void update() {
    Long productId = 288L;
    Product product = new Product();
    product.setPname("컴퓨터_수정");
    product.setQuantity(20L);
    product.setPrice(2000000L);
    int updatedRowCount = productDAO.update(productId, product);
    Optional<Product> findedProduct = productDAO.findById(productId);

    Assertions.assertThat(updatedRowCount).isEqualTo(1);
    Assertions.assertThat(findedProduct.get().getPname()).isEqualTo(product.getPname());
    Assertions.assertThat(findedProduct.get().getQuantity()).isEqualTo(product.getQuantity());
    Assertions.assertThat(findedProduct.get().getPrice()).isEqualTo(product.getPrice());
  }

  //삭제
  @Test
  @DisplayName("상품삭제")
  void delete() {
    Long productId = 283L;
    int deletedRowCount = productDAO.delete(productId);
    Optional<Product> findedProduct = productDAO.findById(productId);
    // Product product = findedProduct.orElseThrow();
    //case1)
//    Assertions.assertThat(findedProduct.ofNullable("없음").orElseThrow())
//        .isNotEqualTo("없음");

    //case2)
    Assertions.assertThatThrownBy(() -> findedProduct.orElseThrow())
        .isInstanceOf(NoSuchElementException.class);
  }

  //목록
  @Test
  @DisplayName("상품목록")
  void findAll() {
    List<Product> list = productDAO.findAll();
    //case1)
//    for(Product product : list){
//      log.info("product={}",product);
//    }
    //case2)
//    list.stream().forEach(product ->log.info("product={}",product));

    Assertions.assertThat(list.size()).isGreaterThan(0);
  }

}