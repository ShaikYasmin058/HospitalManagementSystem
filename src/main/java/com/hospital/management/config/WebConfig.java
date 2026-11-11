package com.hospital.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/dashboard",
                        "/patients", "/patients/**",
                        "/doctors", "/doctors/**",
                        "/appointments", "/appointments/**",
                        "/medical-records", "/medical-records/**",
                        "/billing", "/billing/**",
                        "/staff", "/staff/**",
                        "/admin", "/admin/**",
                        "/reports", "/reports/**",
                        "/patient/dashboard",
                        "/patient/appointments",
                        "/patient/appointments/book",
                        "/patient/profile/new",
                        "/patient/profile",
                        "/patient/medical-records",
                        "/patient/billing",
                        "/patient/appointments/cancel/**",
                        "/doctor/dashboard",
                        "/doctor/appointments",
                        "/doctor/medical-records",
                        "/doctor/medical-records/**",
                        "/receptionist/dashboard"
                )
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/favicon.ico"
                );
    }

    // Serve uploaded doctor profile photos
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
