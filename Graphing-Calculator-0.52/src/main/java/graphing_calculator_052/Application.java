 package graphing_calculator_052;

//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import graphing_calculator_052.Routers.Dispatcher;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		//SpringApplication.run(Application.class, args);
		long startTime = TimeManager.getPreciseTime();
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.run("y=1/x", new Identification("GF"));
		System.out.println("Time to compile: " + (TimeManager.getPreciseTime()- startTime) + "ms");
			}
		}       