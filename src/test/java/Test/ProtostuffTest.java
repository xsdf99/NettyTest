package Test;

import pojo.TestBean;
import utils.SerializationUtil;

public class ProtostuffTest {
    public static void main(String[] args) {
        for (int i = 1; i < 500; i++) {
            TestBean testBean = new TestBean(i, "df" + i);
            byte[] bytes = SerializationUtil.serialize(testBean);
            TestBean testBean1 = SerializationUtil.deserialize(bytes, TestBean.class);
            System.out.println("age:" + testBean1.getAge() + "    name:" + testBean1.getName());
        }
    }
}
