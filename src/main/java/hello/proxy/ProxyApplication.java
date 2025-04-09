package hello.proxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@Import({AppV1Config.class, AppV2Config.class})
//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "hello.proxy.app")
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
