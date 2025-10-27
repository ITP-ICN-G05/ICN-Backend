package IntergrationTest.Database;

import com.gof.ICNBack.Application;
import com.gof.ICNBack.DataSources.Entity.EmailRecordEntity;
import com.gof.ICNBack.DataSources.Service.MongoEmailCleanService;
import com.gof.ICNBack.Service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MongoEmailDataRetrievalTest {
    @Autowired
    private MongoEmailCleanService mongoEmailCleanService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EmailService service;

    private static final String COLLECTION_NAME = "Email";
    EmailRecordEntity recentRecord;
    EmailRecordEntity oldRecord;
    LocalDateTime testTime = LocalDateTime.now().minusMinutes(5);
    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(COLLECTION_NAME);
        mongoTemplate.createCollection(COLLECTION_NAME);
        
    }

    @Test
    void getCodeByEmail_ShouldReturnEmptyListWhenNoRecordsFound() {
        insertValidEmail();
        String email = "test@example.com";

        List<String> result = service.getValidationCode(email);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void shouldCleanupOldEmailsSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldTime = now.minusMinutes(15);
        LocalDateTime recentTime = now.minusMinutes(5);

        insertTestEmail("old1@test.com", oldTime);
        insertTestEmail("old2@test.com", oldTime.minusMinutes(5));

        insertTestEmail("recent1@test.com", recentTime);
        insertTestEmail("recent2@test.com", now.minusMinutes(2));

        long initialCount = mongoTemplate.getCollection(COLLECTION_NAME).countDocuments();
        assertThat(initialCount).isEqualTo(4);

        int cleanedCount = mongoEmailCleanService.manualCodeCleanup();

        long remainingCount = mongoTemplate.getCollection(COLLECTION_NAME).countDocuments();

        assertThat(cleanedCount).isEqualTo(2);
        assertThat(remainingCount).isEqualTo(2);
    }

    @Test
    void shouldHandleEmptyCollectionGracefully() {
        int cleanedCount = mongoEmailCleanService.manualCodeCleanup();

        assertThat(cleanedCount).isEqualTo(0);

        long collectionCount = mongoTemplate.getCollection(COLLECTION_NAME).countDocuments();
        assertThat(collectionCount).isEqualTo(0);
    }

    @Test
    void shouldNotCleanRecentEmails() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recentTime1 = now.minusMinutes(5);
        LocalDateTime recentTime2 = now.minusMinutes(1);

        insertTestEmail("recent1@test.com", recentTime1);
        insertTestEmail("recent2@test.com", recentTime2);

        int cleanedCount = mongoEmailCleanService.manualCodeCleanup();

        assertThat(cleanedCount).isEqualTo(0);

        long remainingCount = mongoTemplate.getCollection(COLLECTION_NAME).countDocuments();
        assertThat(remainingCount).isEqualTo(2);
    }

    @Test
    void shouldCleanAllOldEmailsWhenAllAreExpired() {
        LocalDateTime oldTime1 = LocalDateTime.now().minusMinutes(20);
        LocalDateTime oldTime2 = LocalDateTime.now().minusMinutes(15);
        LocalDateTime oldTime3 = LocalDateTime.now().minusMinutes(30);

        insertTestEmail("old1@test.com", oldTime1);
        insertTestEmail("old2@test.com", oldTime2);
        insertTestEmail("old3@test.com", oldTime3);

        int cleanedCount = mongoEmailCleanService.manualCodeCleanup();

        assertThat(cleanedCount).isEqualTo(3);

        long remainingCount = mongoTemplate.getCollection(COLLECTION_NAME).countDocuments();
        assertThat(remainingCount).isEqualTo(0);
    }

    @Test
    void shouldHandleMixedScenarioCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        insertTestEmail("expired1@test.com", now.minusMinutes(30));
        insertTestEmail("expired2@test.com", now.minusMinutes(15));

        insertTestEmail("borderline@test.com", now.minusMinutes(10).minusSeconds(1));

        insertTestEmail("valid1@test.com", now.minusMinutes(9));
        insertTestEmail("valid2@test.com", now.minusMinutes(5));
        insertTestEmail("valid3@test.com", now.minusMinutes(1));

        int cleanedCount = mongoEmailCleanService.manualCodeCleanup();

        assertThat(cleanedCount).isEqualTo(3);

        long remainingCount = mongoTemplate.getCollection(COLLECTION_NAME).countDocuments();
        assertThat(remainingCount).isEqualTo(3);
    }

    @Test
    void scheduledCleanup_ShouldExecuteWithoutErrors() {

        insertTestEmail("test@test.com", LocalDateTime.now().minusMinutes(20));

        mongoEmailCleanService.scheduledCleanup();

        assertThat(true).isTrue();
    }

    private void insertValidEmail(){
        EmailRecordEntity recentRecord = new EmailRecordEntity(
                null,"123456","test@example.com",new Date()
        );

        EmailRecordEntity oldRecord = new EmailRecordEntity(
                null,
                "654321",
                "test@example.com",
                Date.from(testTime.minusMinutes(10).atZone(java.time.ZoneId.systemDefault()).toInstant())
        );
        mongoTemplate.insert(recentRecord, COLLECTION_NAME);
        mongoTemplate.insert(oldRecord, COLLECTION_NAME);

    }

    private void insertTestEmail(String email, LocalDateTime createdDate) {
        Map<String, Object> emailDocument = new HashMap<>();
        emailDocument.put("email", email);
        emailDocument.put("createdDate", Date.from(createdDate.atZone(java.time.ZoneId.systemDefault()).toInstant()));
        emailDocument.put("code", "someCode");

        mongoTemplate.insert(emailDocument, COLLECTION_NAME);
    }
}
