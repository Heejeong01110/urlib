package com.heez.urlib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class UrlibApplication {

  public static void main(String[] args) {
    SpringApplication.run(UrlibApplication.class, args);
  }

}
