package com.kh.product.web.rest;

import lombok.Data;

@Data
public class UpdateRest {
  private Long productId;
  private String pname;
  private Long quantity;
  private Long price;
}