package hello.proxy.app.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


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
