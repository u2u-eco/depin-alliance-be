package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 25-Jul-2024
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {
  @Id
  public Long id;
  @Column(name = "username", updatable = false, nullable = false)
  public String username;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ref_id")
  public User ref;
  @Column(unique = true)
  public String code;

  @Transactional
  public static User createUser(User user) {
    user.create();
    user.persist();
    user.code = RandomStringUtils.randomAlphanumeric(10);
    return user;
  }

  public static User findByCode(String code) {
    return find("code", code).firstResult();
  }

  public static int updateAccount(String query, Map<String, Object> params) {
    return update(query, params);
  }

  public static int updateBalanceUser(BigDecimal amount, long id) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("amount", amount);
    return updateAccount("balance = balance + :amount where id = :id and balance + :amount >=0", params);
  }

}
