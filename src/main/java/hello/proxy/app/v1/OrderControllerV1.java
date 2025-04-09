package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.*;

public interface OrderControllerV1 {
    String request(String itemId);

    String noLog();
}
