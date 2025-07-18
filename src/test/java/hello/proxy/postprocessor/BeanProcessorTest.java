package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class BeanProcessorTest {
    @Test
    void basicConfig(){
        ApplicationContext applicationContext
                = new AnnotationConfigApplicationContext(BasicConfig.class);
        B b = applicationContext.getBean("beanA", B.class);
        b.helloB();

        Assertions.assertThrows(NoSuchBeanDefinitionException.class,
                () -> {applicationContext.getBean(A.class);});
    }

    @Configuration
    static class BasicConfig{
        @Bean(name = "beanA")
        public A a(){
            return new A();
        }
        @Bean
        public AToBPostProcessor aToBPostProcessor(){
            return new AToBPostProcessor();
        }
    }

    @Slf4j
    static class A{
        public void helloA(){
            log.info("helloA");
        }
    }

    @Slf4j
    static class B{
        public void helloB(){
            log.info("helloB");
        }
    }

    @Slf4j
    static class AToBPostProcessor implements BeanPostProcessor{
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            log.info("bean = {}, beanName = {}", bean, beanName);
            if(bean instanceof A){
                return new B();
            }
            return bean;
        }
    }

}
