package hello.proxy.app.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//controller proxy 직접 설정하지 않을 경우 restController 사용!!
//여기서 사용해도 proxy 설정이 되지 않는다
//@RestController
public class OrderControllerV1Impl implements OrderControllerV1{
    private final OrderServiceV1 orderService;

    public OrderControllerV1Impl(OrderServiceV1 orderService) {
        this.orderService = orderService;
    }

    @Override
    //@GetMapping("/v1/request")
    public String request(@RequestParam("itemId") String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @Override
    //@GetMapping("/v1/no-log")
    public String noLog() {
        return "ok";
    }
}
