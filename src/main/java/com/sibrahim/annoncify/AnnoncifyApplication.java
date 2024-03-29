package com.sibrahim.annoncify;

import com.sibrahim.annoncify.entity.Image;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class AnnoncifyApplication {

//	private final ImageRespository imageRespository;
//	private final ProductRepository productRepository;

//	public AnnoncifyApplication(ImageRespository imageRespository, ProductRepository productRepository) {
//		this.imageRespository = imageRespository;
//		this.productRepository = productRepository;
//	}

	public static void main(String[] args) {
		SpringApplication.run(AnnoncifyApplication.class, args);
	}

	//@Bean
	CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder, UserRepository userRepository, ProductRepository productRepository) {
		return args -> {
			User user = User.builder().name("admin")
					.phoneNumber("36537673")
					.email("sidi@gmail.com")
					.role(RoleEnum.ADMIN)
					.createDate(new Date())
					.password(passwordEncoder.encode("12345"))
					.build();
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

	@Bean
	@Transactional
	CommandLineRunner commandLineRunner(ImageRespository imageRespository, ProductRepository productRepository) {
		return args -> {

		};
	}


}
