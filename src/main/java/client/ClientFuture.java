package client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClientFuture implements Runnable {
    private Object result;

    public abstract void run(Object result);

    @Override
    public void run() {
        run(result);
    }
}
