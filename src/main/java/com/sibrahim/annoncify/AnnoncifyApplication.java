package com.sibrahim.annoncify;

import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.RoleEnum;
import com.sibrahim.annoncify.repository.ImageRespository;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class AnnoncifyApplication {
	public static void main(String[] args) {
		SpringApplication.run(AnnoncifyApplication.class, args);
	}
}