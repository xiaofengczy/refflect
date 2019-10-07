package com.jzit.cn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * FileName: RestConfig Description:
 *
 * @author caozhongyu
 * @create 2019/10/6
 */
@Component
public class RestConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}