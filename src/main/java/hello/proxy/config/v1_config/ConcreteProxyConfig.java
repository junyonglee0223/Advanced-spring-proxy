package hello.proxy.config.v1_config;


import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v1_config.concrete_proxy.OrderControllerConcreteProxy;
import hello.proxy.config.v1_config.concrete_proxy.OrderRepositoryConcreteProxy;
import hello.proxy.config.v1_config.concrete_proxy.OrderServiceConcreteProxy;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcreteProxyConfig {
    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace){
        OrderControllerV2 orderControllerV2Impl = new OrderControllerV2(orderServiceV2(logTrace));
        return new OrderControllerConcreteProxy(orderControllerV2Impl, logTrace);
    }
    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace logTrace){
        OrderServiceV2 orderServiceV2Impl = new OrderServiceV2(orderRepositoryV2(logTrace));
        return new OrderServiceConcreteProxy(orderServiceV2Impl, logTrace);
    }
    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace){
        OrderRepositoryV2 orderRepositoryV2Impl = new OrderRepositoryV2();
        return new OrderRepositoryConcreteProxy(orderRepositoryV2Impl, logTrace);
    }
}
