import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JunitDemo {

    private final Calculator calculator = new Calculator();

    class Calculator {
        public String add(int a, int b) {
            return String.valueOf(a + b);
        }
    }

    @Test
    public void testAddCorrect() {
        String result = calculator.add(2, 3);
        assertEquals(String.valueOf(5), result);
    }

    // 测试模拟出错情况
    @Test
    public void testAddIncorrect() {
        String result = calculator.add(2, 3);
        // 使用不正确的断言来模拟出错情形
        assertEquals(String.valueOf(6), result);
    }
}
