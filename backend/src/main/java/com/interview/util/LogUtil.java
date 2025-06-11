package com.interview.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;

public class LogUtil {

  private static final Logger log = org.slf4j.LoggerFactory.getLogger(LogUtil.class);

  private LogUtil() {
    // Private constructor to prevent instantiation
  }

  public static String formatLogMessage(String message, Object... args) {
    return String.format(message, args);
  }

  /**
   * Shamelessly copied from JHipster many years ago and I still frequently use this as it's very
   * helpful considering the same app can be run in very different environments. Logs the
   * application startup information including access URLs and active profiles.
   *
   * @param env the Spring Environment containing application properties
   */
  public static void logApplicationStartup(Environment env) {
    String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store"))
        .map(key -> "https").orElse("http");
    String serverPort = env.getProperty("server.port");
    String contextPath = Optional
        .ofNullable(env.getProperty("server.servlet.context-path"))
        .filter(LogUtil::isNotBlank)
        .orElse("/");
    String hostAddress = "localhost";
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      log.warn("The host name could not be determined, using `localhost` as fallback");
    }
    log.info("Application '{}' is running! Access URLs: " +
            "Local: {}://localhost:{}{} " +
            "External: {}://{}:{}{} " +
            "Profile(s): {}",
        env.getProperty("spring.application.name"), // NOSONAR
        protocol,
        serverPort,
        contextPath,
        protocol,
        hostAddress,
        serverPort,
        contextPath,
        env.getActiveProfiles()
    );
  }

  private static boolean isNotBlank(String s) {
    return !isBlank(s);
  }

  private static boolean isBlank(String s) {
    final int strLen = s == null ? 0 : s.length();
    if (strLen == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}
