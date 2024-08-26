package xyz.telegram.depinalliance.services;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;

/**
 * @author holden on 25-Jul-2024
 */
@ApplicationScoped
public class JwtService {
  @ConfigProperty(name = "login.time-out")
  long timeOut;

  public String generateToken(String telegramId) {
    return Jwt.upn(telegramId).expiresAt(System.currentTimeMillis() / 1000L + timeOut).issuedAt(Instant.now())
      .preferredUserName(telegramId).sign();
  }
}
