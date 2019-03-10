package pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestBean {

    public TestBean(int age, String name) {
        this.age = age;
        this.name = name;
    }

    private String name;

    private int age;
}
