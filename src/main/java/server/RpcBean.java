package server;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Getter
@Setter
public class RpcBean {

    public RpcBean(Object clazz, Method method) {
        this.clazz = clazz;
        this.method = method;

    }

    private Object clazz;

    private Method method;
}
