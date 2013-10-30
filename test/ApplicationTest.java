import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }
}
