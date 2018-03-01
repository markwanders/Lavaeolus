package com.example.lavaeolus;

import com.example.lavaeolus.dao.UserRepository;
import com.example.lavaeolus.dao.domain.Role;
import com.example.lavaeolus.dao.domain.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
@Configuration
@ComponentScan
public class LavaeolusApplication {

	public static void main(String[] args) {
		SpringApplication.run(LavaeolusApplication.class, args);
	}

	@Bean
    @Profile("local")
	public InitializingBean insertDefaultUsers() {
		return new InitializingBean() {
			@Autowired
			private UserRepository userRepository;

			@Override
			public void afterPropertiesSet() throws Exception {
				addUser("admin", "admin");
				addUser("user", "user");
			}

			private void addUser(String username, String password) {
				User user = new User();
				user.setUsername(username);
                user.setPassword(new BCryptPasswordEncoder().encode(password));
                user.setRole(username.equals("admin") ? Role.ADMIN : Role.USER);
				userRepository.save(user);
			}
		};
	}
}
