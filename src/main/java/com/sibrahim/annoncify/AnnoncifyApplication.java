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
@EnableScheduling
public class AnnoncifyApplication implements CommandLineRunner {

	//	private final ImageRespository imageRespository;
//	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public AnnoncifyApplication(ImageRespository imageRespository, ProductRepository productRepository, UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(AnnoncifyApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder, UserRepository userRepository, ProductRepository productRepository) {
		return args -> {
//			User user = User.builder().name("admin")
//					.phoneNumber("36537673")
//					.email("sidi@gmail.com")
//					.role(RoleEnum.ADMIN)
//					.createDate(new Date())
//					.password(passwordEncoder.encode("12345"))
//					.build();
//			userRepository.save(user);
//			Product product = Product.builder().description("best product in the world!")
//					.price(50000)
//					.createDate(new Date())
//					.updateDate(new Date())
//					.user(user)
//					.build();
//			productRepository.save(product);
		};
	}

	//@Bean
	@Transactional
	CommandLineRunner commandLineRunner(ImageRespository imageRespository, ProductRepository productRepository) {
		return args -> {

		};
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
//		var user  = userRepository.findById(149L).get();
//		System.out.println(user.isEnabled());
//		user.setEnabled(true);
//		System.out.println(user.isEnabled());
//		userRepository.save(user);
	}
	int i = 0;
	@Scheduled(fixedDelay = 5000) // Run every 5 seconds
	public void sendGetRequest() throws InterruptedException {
		i = i+1;
		String url = "https://annoncify.onrender.com/swagger-ui/index.html#/category-controller/getAll";
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(url, String.class);
		Thread.sleep(100000);
		System.out.println("GET request sent to " + url + i);
	}
}