package ru.holyway.georeminder.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplatesConfiguration {

    @Bean
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter serviceHttpMessageConverter) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
        restTemplate.getMessageConverters().add(serviceHttpMessageConverter);

        return restTemplate;
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate(MappingJackson2HttpMessageConverter serviceHttpMessageConverter) {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
        asyncRestTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
        asyncRestTemplate.getMessageConverters().add(serviceHttpMessageConverter);

        return asyncRestTemplate;
    }

    @Bean
    public MappingJackson2HttpMessageConverter serviceHttpMessageConverter(ObjectMapper serviceObjectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(serviceObjectMapper);

        return converter;
    }

    @Bean
    public ObjectMapper serviceObjectMapper() {
        ObjectMapper objectMapper;
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }
}
