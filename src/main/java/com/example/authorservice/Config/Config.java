package com.example.authorservice.Config;

import com.example.authorservice.Mapper.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public Mapper mapper() {
        return new Mapper();
    }
}
