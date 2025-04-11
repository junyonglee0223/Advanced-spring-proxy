package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import hello.proxy.pureproxy.proxy.code.Subject;
import org.junit.jupiter.api.Test;

public class ProxyPatternTest {
    @Test
    void noProxyTest(){
        Subject subject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(subject);

        client.execute();
        client.execute();
        client.execute();
    }

    @Test
    void cacheProxyTest(){
        Subject subject = new RealSubject();
        CacheProxy proxy = new CacheProxy(subject);
        ProxyPatternClient client = new ProxyPatternClient(proxy);

        client.execute();
        client.execute();
        client.execute();
    }
}
