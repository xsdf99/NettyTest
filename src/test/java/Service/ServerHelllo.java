package Service;

import server.Rpc;
import server.RpcMethod;
import utils.RpcConstant;

@Rpc
public class ServerHelllo {

    @RpcMethod(alias = RpcConstant.HELLO)
    public String getHello(String name) {
        return "hello " + name;
    }
}
