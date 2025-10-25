package UnitTest;

import com.gof.ICNBack.Web.Utils.Validator;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.*;

@Test
public class ValidatorTest {

    @Test
    public void testEmailValidator(){
        assertTrue(Validator.isValidEmail("test@example.com"));
        assertFalse(Validator.isValidEmail("\"test\"[@]example.com"));
    }

    @Test
    public void testPasswordValidator(){
        assertFalse(Validator.isValidPassword("password123"));
        assertFalse(Validator.isValidPassword("asszxfg12asddf23"));
        assertFalse(Validator.isValidPassword("\"   *   \""));
    }
}
