package hello.proxy.app.v1;

public class OrderRepositoryV1Impl implements OrderRepositoryV1{
    @Override
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
