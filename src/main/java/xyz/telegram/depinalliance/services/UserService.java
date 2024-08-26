package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.Level;
import xyz.telegram.depinalliance.entities.SystemConfig;
import xyz.telegram.depinalliance.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 24-Aug-2024
 */
@ApplicationScoped
public class UserService {
  @Transactional
  public User checkStartUser(Long id, String username, String refCode) {
    User user = User.findById(id);
    if (user == null) {
      user = new User();
      user.id = id;
      user.username = username;
      user.level = new Level(1L);
      User ref = null;
      if (StringUtils.isNotBlank(refCode)) {
        ref = User.findByCode(refCode);
        if (ref != null) {
          User.updatePointUser(ref.id, SystemConfig.findByKey(Enums.Config.POINT_REF));
        }
      }
      user.ref = ref;
      User.createUser(user);
      return user;
    }
    if (!user.username.equals(username)) {
      Map<String, Object> params = new HashMap<>();
      params.put("id", id);
      params.put("username", username);
      User.updateAccount("username = :username where id = :id", params);
    }
    return user;
  }
}
