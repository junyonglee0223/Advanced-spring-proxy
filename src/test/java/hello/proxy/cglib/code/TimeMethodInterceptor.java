package hello.proxy.cglib.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {
    private final Object target;

    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object obj, Method method,
                            Object[] args, MethodProxy proxy) throws Throwable {

        log.info("Time method start!!");
        long startTime = System.currentTimeMillis();

        Object result = method.invoke(target);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("Time method end!!");
        return result;
    }
}
