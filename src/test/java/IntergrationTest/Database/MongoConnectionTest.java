package IntergrationTest.Database;

import com.gof.ICNBack.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MongoConnectionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testMongoDBConnection() {
        assertNotNull(mongoTemplate, "MongoTemplate should not be null");

        String databaseName = mongoTemplate.getDb().getName();
        assertNotNull(databaseName, "Database name should not be null");
        System.out.println("Connected to database: " + databaseName);

        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            System.out.println("MongoDB ping successful");
        } catch (Exception e) {
            fail("MongoDB ping failed: " + e.getMessage());
        }
    }
}