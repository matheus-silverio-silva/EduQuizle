package br.com.eduquizle.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = {
        "br.com.eduquizle.api",
        "controllers"
})public class EduquizleApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduquizleApiApplication.class, args);
    }
}