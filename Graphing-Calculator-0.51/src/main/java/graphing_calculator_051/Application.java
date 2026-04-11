 package graphing_calculator_051;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.run("2^3^2+2");
			}
		}