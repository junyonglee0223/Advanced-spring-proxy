package hello.proxy.app.v2;

import hello.proxy.app.v1.OrderRepositoryV1;

public class OrderRepositoryV2{
    public void save(String itemId) {
        if(itemId.equals("ex")){
            throw new IllegalArgumentException("exception occurs!!");
        }
        sleep(1000);
    }
    private void sleep(int mills){
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
