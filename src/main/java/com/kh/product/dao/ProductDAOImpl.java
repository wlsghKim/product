package com.kh.product.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductDAOImpl implements ProductDAO{

  private final NamedParameterJdbcTemplate template;


  /**
   * 등록
   *
   * @param product 상품
   * @return 상품아이디
   */
  @Override
  public Long save(Product product) {
    StringBuffer sb = new StringBuffer();
    sb.append("insert into product(pid,pname,quantity,price) ");
    sb.append("values(product_pid_seq.nextval, :pname, :quantity, :price) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(product);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    template.update(sb.toString(),param,keyHolder,new String[]{"pid"});
//    template.update(sb.toString(),param,keyHolder,new String[]{"pid","pname"});

    long pid = keyHolder.getKey().longValue(); //상품아이디

    //String pname = (String)keyHolder.getKeys().get("pname");
    return pid;
  }

  /**
   * 조회
   *
   * @param pid 상품아이디
   * @return 상품
   */
  @Override
  public Optional<Product> findById(Long pid) {
    StringBuffer sb = new StringBuffer();
    sb.append("select pid, pname, quantity, price ");
    sb.append("  from product ");
    sb.append(" where pid = :id ");

    try {
      Map<String, Long> param = Map.of("id", pid);

      Product product = template.queryForObject(
          sb.toString(), param, productRowMapper());
      return Optional.of(product);
    } catch (EmptyResultDataAccessException e) {
      //조회결과가 없는 경우
      return Optional.empty();
    }
  }

  /**
   * 수정
   *
   * @param pid 상품아이디
   * @param product   상품
   * @return 수정된 레코드 수
   */
  @Override
  public int update(Long pid, Product product) {
    StringBuffer sb = new StringBuffer();
    sb.append("update product ");
    sb.append("   set pname = :pname, ");
    sb.append("       quantity = :quantity, ");
    sb.append("       price = :price ");
    sb.append(" where pid = :id ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("pname",product.getPname())
        .addValue("quantity",product.getQuantity())
        .addValue("price",product.getPrice())
        .addValue("id",pid);

    return template.update(sb.toString(),param);
  }

  /**
   * 삭제
   *
   * @param pid 상품아이디
   * @return 삭제된 레코드 수
   */
  @Override
  public int delete(Long pid) {
    String sql = "delete from product where pid = :id ";
    return template.update(sql,Map.of("id",pid));
  }

  /**
   * 부분삭제
   *
   * @param
   * @return
   */
  @Override
  public int deleteParts(List<Long> pids) {
    String sql = "delete from product where pid in ( :ids ) ";
    Map<String, List<Long>> param = Map.of("ids", pids);
    return template.update(sql,param);
  }

  /**
   * 전체 삭제
   *
   * @return 삭제한 레코드 건수
   */
  @Override
  public int deleteAll() {
    String sql = "delete from product ";
    Map<String,String> param = new LinkedHashMap<>();
    int deletedRowCnt = template.update(sql, param);
    return deletedRowCnt;
  }

  /**
   * 목록
   *
   * @return 상품목록
   */
  @Override
  public List<Product> findAll() {
    StringBuffer sb = new StringBuffer();
    sb.append("select pid, pname, quantity, price ");
    sb.append("  from product ");

    List<Product> list = template.query(
        sb.toString(),
        BeanPropertyRowMapper.newInstance(Product.class)  // 레코드 컬럼과 자바객체 멤버필드가 동일한 이름일경우, camelcase지원
    );

    return list;
  }

  class RowMapperImpl implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
      Product product = new Product();
      product.setPid(rs.getLong("pid"));
      product.setPname(rs.getString("pname"));
      product.setQuantity(rs.getLong("quantity"));
      product.setPrice(rs.getLong("price"));
      return product;
    }
  }

//  RowMapper<Product> rowMapper = new RowMapper<Product>() {
//    @Override
//    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
//      Product product = new Product();
//      product.setProductId(rs.getLong("product_id"));
//      product.setPname(rs.getString("pname"));
//      product.setQuantity(rs.getLong("quantity"));
//      product.setPrice(rs.getLong("price"));
//      return product;
//    }
//  };
//
//  RowMapper<Product> rowMapper2 = (rs, rowNum) -> {
//      Product product = new Product();
//      product.setProductId(rs.getLong("product_id"));
//      product.setPname(rs.getString("pname"));
//      product.setQuantity(rs.getLong("quantity"));
//      product.setPrice(rs.getLong("price"));
//      return product;
//  };

  //수동 매핑
  private RowMapper<Product> productRowMapper() {
    return (rs, rowNum) -> {
      Product product = new Product();
      product.setPid(rs.getLong("pid"));
      product.setPname(rs.getString("pname"));
      product.setQuantity(rs.getLong("quantity"));
      product.setPrice(rs.getLong("price"));
      return product;
    };
  }

  //자동매핑 : 테이블의 컬럼명과 자바객체 타입의 멤버필드가 같아야한다.
  // BeanPropertyRowMapper.newInstance(자바객체타입)

  /**
   * 상품존재유무
   *
   * @param pid 상품아이디
   * @return
   */
  @Override
  public boolean isExist(Long pid) {
    boolean isExist = false;
    String sql = "select count(*) from product where pid = :pid";

    Map<String,Long> param = Map.of("pid",pid);
    Integer integer = template.queryForObject(sql, param, Integer.class);
    isExist = (integer > 0) ? true : false;
    return isExist;
  }

  /**
   * 등록된 상품수
   *
   * @return 레코드 건수
   */
  @Override
  public int countOfRecord() {
    String sql = "select count(*) from product ";
    Map<String,String> param = new LinkedHashMap<>();
    Integer rows = template.queryForObject(sql, param, Integer.class);
    return rows;
  }
}


