package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.validation.ObjectError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {
    @Test
    void reflection0(){
        Hello target = new Hello();

        log.info("call A starts!!");
        String resultA = target.callA();
        log.info("result = {}", resultA);

        log.info("call B starts!!");
        String resultB = target.callB();
        log.info("result = {}", resultB);
    }

    @Test
    void reflection1() throws Exception{
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");
        Hello target = new Hello();

        log.info("call A starts!!");
        Method methodCallA = classHello.getMethod("callA");
        Object resultA = methodCallA.invoke(target);
        log.info("result = {}", resultA);

        log.info("call B starts!!");
        Method methodCallB = classHello.getMethod("callB");
        Object resultB = methodCallB.invoke(target);
        log.info("result = {}", resultB);
    }

    @Test
    void reflection2() throws Exception{
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");
        Hello target = new Hello();

        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    private void dynamicCall(Method method, Object target) throws Exception{
        log.info("start!!");
        Object result = method.invoke(target);
        log.info("result = {}", result);
    }
    @Slf4j
    static class Hello{
        public String callA(){
            log.info("call A!!");
            return "A";
        }
        public String callB(){
            log.info("call B!!");
            return "B";
        }
    }
}
