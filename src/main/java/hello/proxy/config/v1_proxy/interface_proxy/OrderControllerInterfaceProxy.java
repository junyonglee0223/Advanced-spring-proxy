package hello.proxy.config.v1_proxy.interface_proxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.web.bind.annotation.RestController;

//OrderControllerV1Impl에 설정된 RestController annotation 삭제해야 함!
@RestController
public class OrderControllerInterfaceProxy implements OrderControllerV1{
    private final OrderControllerV1 target;
    private final LogTrace logTrace;

    public OrderControllerInterfaceProxy(OrderControllerV1 target, LogTrace logTrace) {
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public String request(String itemId) {
        TraceStatus status = null;
        try{
            status = logTrace.begin("OrderController.request()");

            String result = target.request(itemId);

            logTrace.end(status);

            return result;
        }catch (Exception e){
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Override
    public String noLog() {
        return target.noLog();
    }
}
