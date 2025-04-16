package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {
    @Test
    void cglib(){
        ConcreteService service = new ConcreteService();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class);
        enhancer.setCallback(new TimeMethodInterceptor(service));

        ConcreteService proxy = (ConcreteService) enhancer.create();
        log.info("target Class = {}", service.getClass());
        log.info("proxy Class = {}", proxy.getClass());

        proxy.call();
    }
}
