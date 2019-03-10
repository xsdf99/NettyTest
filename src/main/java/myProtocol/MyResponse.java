package myProtocol;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MyResponse {

    private String requestId;
    private Object result;

    private int type;
}
