package IntergrationTest.Service;

import com.gof.ICNBack.Application;
import com.gof.ICNBack.Service.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class EmailServiceIntegrationTest {

    @Autowired
    EmailService emailService;

    @Autowired
    private Configuration configuration;

    @Autowired
    MongoTemplate template;

    @BeforeEach
    void setUp() {
        template.dropCollection("Email");
    }

    @Test
    void testGenerateAndCacheCode() {
        String testEmail = "integration-test@example.com";

        String code = emailService.generateValidationCode(testEmail);
        emailService.cacheValidationCode(testEmail, code);
        List<String> cachedCodes = emailService.getValidationCode(testEmail);

        assertNotNull(code);
        assertEquals(4, code.length());
        assertTrue(cachedCodes.contains(code));
    }

    @Test
    void testSendCode_Integration() {
        String testEmail = "yueshan.li@student.unimelb.edu.au";
        String testCode = "999999";

        Boolean result = emailService.sendCode(testCode, testEmail);

        assertTrue(result);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testDefaultTemplate() {
        String templatePath = "email.ftl";
        assertDoesNotThrow(() -> {
            Template template = configuration.getTemplate(templatePath);
            assertNotNull(template);
        });
    }
}
