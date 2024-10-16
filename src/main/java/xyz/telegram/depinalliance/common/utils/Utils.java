package xyz.telegram.depinalliance.common.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.core.HttpHeaders;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author holden on 25-Jul-2024
 */
public class Utils {
  private static final ModelMapper modelMapper;
  private static final String[] IP_HEADER_CANDIDATES = { "cf-connecting-ip", "X-Forwarded-For", "Proxy-Client-IP",
    "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };
  private static ObjectMapper rawMapper;

  static {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    rawMapper = new ObjectMapper();
    rawMapper = JsonMapper.builder().configure(SerializationFeature.INDENT_OUTPUT, true)
      .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
      .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true).build();
  }

  public static int getRandomNumber(int min, int max) {
    return new Random().nextInt(max - min) + min;
  }

  public static String printLogStackTrace(Exception e) {
    StringWriter errors = new StringWriter();
    e.printStackTrace(new PrintWriter(errors));
    return errors.toString();
  }

  public static <T, R> R copyFrom(T from, Class<R> type) {
    return modelMapper.map(from, type);
  }

  public static String convertObjectToString(Object object) {
    try {
      return rawMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static boolean validateAmountBigDecimal(BigDecimal amount) {
    return amount != null && amount != BigDecimal.ZERO && amount.compareTo(BigDecimal.ZERO) > 0;
  }

  public static <T> T toObject(String jsonString, Class<T> t) {
    try {
      return rawMapper.readValue(jsonString, t);
    } catch (Exception e) {
      return null;
    }
  }

  public static Calendar getCalendar() {
    Calendar utcTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    return utcTime;
  }

  public static Calendar getNewDay() {
    Calendar calendar = getCalendar();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar;
  }

  public static BigDecimal stripDecimalZeros(BigDecimal value) {
    if (value == null)
      return null;
    BigDecimal striped = (value.scale() > 0) ? value.stripTrailingZeros() : value;
    return (striped.scale() < 0) ? striped.setScale(0) : striped;
  }

  public static <T> List<T> mapToList(String entitiesJson, Class<T> entityClass) throws Exception {
    try {
      CollectionType listType = rawMapper.getTypeFactory().constructCollectionType(ArrayList.class, entityClass);
      List<T> entities = rawMapper.readValue(entitiesJson, listType);
      return entities;
    } catch (JsonParseException e) {
      throw e;
    }
  }

  public static String getClientIpAddress(HttpHeaders headers, HttpServerRequest httpServerRequest) {
    for (String header : IP_HEADER_CANDIDATES) {
      String ip = headers.getHeaderString(header);
      if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
        return ip;
      }
    }
    return httpServerRequest.remoteAddress().hostAddress();
  }

  public static String generateRandomString(int numberCharacter) {
    return RandomStringUtils.randomAlphanumeric(numberCharacter);
  }
}
