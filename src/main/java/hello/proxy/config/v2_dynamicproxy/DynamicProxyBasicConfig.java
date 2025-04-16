package hello.proxy.config.v2_dynamicproxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceBasicHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class DynamicProxyBasicConfig {
    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace){
        OrderControllerV1 controllerV1Impl = new OrderControllerV1Impl(orderServiceV1(logTrace));
        return new OrderControllerInterfaceProxy(controllerV1Impl, logTrace);
    }
    /*
    * 원래는 아래처럼 LogTraceBasicHandler 사용한 Proxy로 하려고 했지만
    * RestController 인식 문제로 기존 interfaceProxy 만든 방식으로 한다.
    * */
//    @Bean
//    public OrderControllerV1 orderControllerV1(LogTrace logTrace){
//        OrderControllerV1 controllerV1Impl = new OrderControllerV1Impl(orderServiceV1(logTrace));
//        OrderControllerV1 controllerV1Proxy = (OrderControllerV1) Proxy.newProxyInstance(
//                OrderControllerV1.class.getClassLoader(),
//                new Class[]{OrderControllerV1.class},
//                new LogTraceBasicHandler(controllerV1Impl, logTrace)
//        );
//        return controllerV1Proxy;
//    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace){
        OrderServiceV1 serviceV1Impl = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        return (OrderServiceV1) Proxy.newProxyInstance(
                OrderServiceV1.class.getClassLoader(),
                new Class[]{OrderServiceV1.class},
                new LogTraceBasicHandler(serviceV1Impl, logTrace)
        );
    }
    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace){
        OrderRepositoryV1 repositoryV1Impl = new OrderRepositoryV1Impl();
        return (OrderRepositoryV1) Proxy.newProxyInstance(
                OrderRepositoryV1.class.getClassLoader(),
                new Class[]{OrderRepositoryV1.class},
                new LogTraceBasicHandler(repositoryV1Impl, logTrace)
        );
    }
}