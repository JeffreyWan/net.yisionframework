package net.yision.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.thetransactioncompany.cors.CORSFilter;
import net.yision.framework.repository.MyRepositoryBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeffrey on 15/9/19.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableJpaRepositories(
        basePackages = "${product.basePackages}",
        repositoryFactoryBeanClass = MyRepositoryBeanFactory.class
)
@EntityScan("${product.basePackages}")
@ComponentScan("${product.basePackages}")
public class Application extends SpringBootServletInitializer {

    @Value("${jackson.indent.output}")
    private boolean jacksonIndentOutput = false;

    @Bean
    public FilterRegistrationBean Filters() {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cors.allowOrigin", "*");
        parameters.put("cors.supportedMethods", "GET, POST, HEAD, PUT, DELETE");
        parameters.put("cors.supportedHeaders", "Accept, Origin, X-Requested-With, Content-Type, Last-Modified");

        Filter filter = new CORSFilter();
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        filterBean.setFilter(filter);
        filterBean.setInitParameters(parameters);
        filterBean.addUrlPatterns("/*");

        return filterBean;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();
        if (jacksonIndentOutput) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeString("");
            }
        });
        Hibernate4Module hibernate4Module = new Hibernate4Module();
        hibernate4Module.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);

        return mapper
                .registerModule(new JodaModule())
                .registerModule(hibernate4Module);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.profiles("production")
                .sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
