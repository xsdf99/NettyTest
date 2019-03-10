package client;

import utils.RpcConstant;

public class ClientMain {

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        rpcClient.start();
        //启动客户端任务处理
        ClientManager.start();
        ClientManager.remoteMethod(RpcConstant.HELLO, new Object[]{"df"}, new ClientFuture() {
            @Override
            public void run(Object result) {
                System.out.println("client get Server return :" + result);
            }
        });
    }
}
