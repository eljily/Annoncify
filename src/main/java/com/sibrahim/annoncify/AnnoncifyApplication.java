package com.sibrahim.annoncify;

import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class AnnoncifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnoncifyApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(UserRepository userRepository, ProductRepository productRepository){
		return args -> {
			User user = User.builder().name("sidibrahim").build();
			userRepository.save(user);
			Product product = Product.builder().description("best product in the world!")
					.price(50000)
					.createDate(new Date())
					.updateDate(new Date())
					.user(user)
					.build();
			productRepository.save(product);
		};
	}
}
