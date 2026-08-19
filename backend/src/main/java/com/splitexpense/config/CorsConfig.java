package com.splitexpense.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class CorsConfig {

    @Bean
    public Filter corsFilter() {

        return new Filter() {

            @Override
           public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain)
        throws IOException, ServletException {

                HttpServletRequest httpRequest =
                        (HttpServletRequest) request;

                HttpServletResponse httpResponse =
                        (HttpServletResponse) response;

                String origin =
                        httpRequest.getHeader("Origin");

                if ("http://localhost:5173".equals(origin)
                        || "https://split-smart-lake.vercel.app".equals(origin)) {

                    httpResponse.setHeader(
                            "Access-Control-Allow-Origin",
                            origin
                    );

                    httpResponse.setHeader(
                            "Access-Control-Allow-Methods",
                            "GET, POST, PUT, DELETE, OPTIONS"
                    );

                    httpResponse.setHeader(
                            "Access-Control-Allow-Headers",
                            "Content-Type"
                    );

                    httpResponse.setHeader(
                            "Access-Control-Allow-Credentials",
                            "true"
                    );

                    httpResponse.setHeader(
                            "Access-Control-Max-Age",
                            "1800"
                    );
                }

                if ("OPTIONS".equalsIgnoreCase(
                        httpRequest.getMethod())) {

                    httpResponse.setStatus(
                            HttpServletResponse.SC_OK
                    );

                    return;
                }

                chain.doFilter(
                        request,
                        response
                );
            }
        };
    }
}