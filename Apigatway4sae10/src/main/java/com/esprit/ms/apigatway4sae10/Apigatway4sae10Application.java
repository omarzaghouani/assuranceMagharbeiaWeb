package com.esprit.ms.apigatway4sae10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

@SpringBootApplication
public class Apigatway4sae10Application {

    public static void main(String[] args) {
        SpringApplication.run(Apigatway4sae10Application.class, args);
        
    }
    @Bean
    public RouteLocator GetrouteApiGatway(RouteLocatorBuilder builder) {
           /* return builder.routes()
                    .route("Apigatway4sae10"); r -> r.path("/candidats/**").uri("http://localhost:8081"))*/
       /* return builder.routes()
                .route("Apigatway4sae10",
                r -> r.path("/candidats/**").uri("lb://MSCandidat4SAE10"))
                        .route("Msjob",
                                r -> r.path("/jobs/**").uri("lb://Msjob")
               ).build();*/
        return builder.routes()
                .route("Apigatway4sae10",
                        r -> r.path("/refund/**").uri("lb://refund-web-distibue"))

                .route("Assurance",
                        r -> r.path("/auth/**").uri("lb://assurance-web-distibue")
                ).build();


    }




    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200"); // Frontend origin
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
