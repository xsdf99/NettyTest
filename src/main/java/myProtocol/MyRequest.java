package myProtocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyRequest {

    public MyRequest() {

    }

    public MyRequest(String requestId, String rpcConstant, Object[] params) {
        this.requestId = requestId;
        this.rpcConstant = rpcConstant;
        this.params = params;
    }



    private int type;

    private String requestId;
    private String rpcConstant;
    private Object[] params;
}
