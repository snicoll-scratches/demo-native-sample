package com.example.demonativesample;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoNativeSampleApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(DemoNativeSampleApplication.class)
				// TODO: this is necessary as the deduce algorithm does not work in native
				.web(WebApplicationType.SERVLET)
				.run(args);
	}

	@Bean
	public String one() {
		return "one";
	}

}
