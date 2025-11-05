package IntergrationTest.Service;

import Utils.Utils;
import com.gof.ICNBack.Application;
import com.gof.ICNBack.DataSources.Organisation.OrganisationDao;
import com.gof.ICNBack.DataSources.User.UserDao;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Entity.User;

import com.gof.ICNBack.Repositories.MongoItemRepository;
import com.gof.ICNBack.Repositories.MongoUserRepository;
import com.gof.ICNBack.Service.OrganisationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class OrganisationServiceExistingDataTest {

    @Autowired
    private OrganisationService organisationService;

    private OrganisationDao organisationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MongoUserRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoItemRepository orgRepo;

    private static final List<String> EXISTING_ORGANISATION_IDS = Arrays.asList(
            "0017F00001ueJZy", "0019s000003x15k", "0019s000005Cl7e",
            "001On00000De6f9", "001On00000JfaSS", "001On00000OCsrr"
    );

    private static final String EXISTING_ITEM_ID = "68c4d6b4650396bdb02dc217";

    @BeforeEach
    void setUp() {
        organisationDao = new Utils.Utils.TestOrganisationDao(mongoTemplate, orgRepo);
        setField(organisationService, "organisationDao", organisationDao);
    }

    @Test
    void testGetOrgCards() {
        // setup parameters
        Map<String, String> filterParameters = new HashMap<>();
        String searchString = "instrumentation";
        Integer skip = 0;
        Integer limit = 10;

        List<Organisation.OrganisationCard> cards = organisationService.getOrgCards(
                -30, 0, 0, 130, filterParameters, searchString, skip, limit);

        assertNotNull(cards, "result should not be null");

        // check returns
        if (!cards.isEmpty()) {
            Organisation.OrganisationCard firstCard = cards.get(0);
            assertNotNull(firstCard.getName(), "name should not be null");
        }

        System.out.println("find " + cards.size() + " cards");
    }

    @Test
    void testGetOrg_WithExistingOrganisation() {
        // using existingId
        String existingOrgId = "0017F00001ueJZy";

        // create testing user
        User testUser = createTestUserIfNeeded();

        Organisation result = organisationService.getOrg(existingOrgId, testUser.getId());

        // null for free user
        if (result != null) {
            assertNotNull(result.getName());
        }

        System.out.println("result: " + (result != null ? "found" : "not found"));
    }

    @Test
    void testGetOrgCardsByIds_WithExistingIds() {
        // testing organisation cards
        List<String> existingIds = Arrays.asList(
                "0017F00001ueJZy", "0019s000003x15k", "0019s000005Cl7e"
        );

        List<Organisation.OrganisationCard> cards = organisationService.getOrgCardsByIds(existingIds);

        assertNotNull(cards, "should not be null");

        // return same number as asked
        assertEquals(cards.size(), existingIds.size());

        System.out.println("find " + cards.size() + " org cards");
    }

    @Test
    void testSearchWithLocation_UsingExistingData() {
        // testing location search
        int locationY = 100;
        int locationX = -47;
        int lenY = 144;
        int lenX = -17;

        Map<String, String> filterParameters = new HashMap<>();
        String searchString = null;
        Integer skip = 0;
        Integer limit = 5;

        List<Organisation.OrganisationCard> cards = organisationService.getOrgCards(
                locationX, locationY, lenX, lenY, filterParameters, searchString, skip, limit);

        assertNotNull(cards);
        System.out.println("find " + cards.size() + " orgs");
    }

    @Test
    void testGetOrgCards_WithFilterParameters() {
        int locationY = 100;
        int locationX = -47;
        int lenY = 144;
        int lenX = -17;

        // Using filter parameters
        Map<String, String> filterParameters = new HashMap<>();
        filterParameters.put("Sector Name", "Critical Minerals");
        filterParameters.put("Item Name", "instrumentation");

        String searchString = null;
        Integer skip = 0;
        Integer limit = 10;

        List<Organisation.OrganisationCard> cards = organisationService.getOrgCards(
                locationX, locationY, lenX, lenY, filterParameters, searchString, skip, limit);

        assertNotNull(cards);
        for (Organisation.OrganisationCard o : cards){
            assertEquals(o.getItems().get(0).getItemName(), "instrumentation");
            assertEquals(o.getItems().get(0).getSectorName(), "Critical Minerals");
        }
        System.out.println("find " + cards.size() + " orgs");
    }

    @Test
    void testGetOrg_VIPLevelAccessControl() {
        // check primitive right
        String existingOrgId = "0017F00001ueJZy";

        // create different users
        User freeUser = createTestUser("test_free_user", "Free User", 0);
        User vipUser = createTestUser("test_vip_user", "VIP User", 1);
        User premiumUser = createTestUser("test_premium_user", "Premium User", 2);

        // test free user
        Organisation resultFree = organisationService.getOrg(existingOrgId, freeUser.getId());
        assertNull(resultFree);

        // test premium user
        Organisation resultVip = organisationService.getOrg(existingOrgId, vipUser.getId());
        assertNotNull(resultVip);

        // test premium T2 user
        Organisation resultPremium = organisationService.getOrg(existingOrgId, premiumUser.getId());
        assertNotNull(resultPremium);
    }

    @Test
    void testDataConsistency() {
        // Data consistence
        for (String orgId : EXISTING_ORGANISATION_IDS) {
            // create user
            User testUser = createTestUser("test_consistency_user", "Test User", 2);

            Organisation org = organisationService.getOrg(orgId, testUser.getId());

            if (org != null) {
                // verify records
                assertNotNull(org.get_id());
                assertNotNull(org.getItems());

                if (!org.getItems().isEmpty()) {
                    Item first = org.getItems().get(0);
                    assertNotNull(first.getId());
                    assertNotNull(first.getDetailedItemId());
                    assertNotNull(first.getOrganisationCapability());
                }
            }
        }

        System.out.println("finished consistency check");
    }

    @Test
    void testPaginationWithExistingData() {
        // page function
        Map<String, String> filterParameters = new HashMap<>();
        String searchString = null;

        // p1
        List<Organisation.OrganisationCard> page1 = organisationService.getOrgCards(
                0, 0, 0, 0, filterParameters, searchString, 0, 2);

        // p2
        List<Organisation.OrganisationCard> page2 = organisationService.getOrgCards(
                0, 0, 0, 0, filterParameters, searchString, 2, 2);

        assertNotNull(page1);
        assertNotNull(page2);

        System.out.println("p1 have : " + page1.size());
        System.out.println("p2 have : " + page2.size());

        if (page1.size() == 2 && page2.size() > 0) {
            if (!page1.isEmpty() && !page2.isEmpty()) {
                assertNotEquals(page1.get(0).getName(), page2.get(0).getName());
            }
        }
    }

    private User createTestUserIfNeeded() {
        String testUserId = "test_user_existing_data";

        // check if exist
        User existingUser = userDao.getUserById(testUserId);
        if (existingUser != null) {
            return existingUser;
        }

        User testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Test User for Existing Data");
        testUser.setVIP(2);
        testUser.setEmail("test_existing_data@example.com");

        repository.save(testUser.toEntity());
        return testUser;
    }

    private User createTestUser(String id, String name, int vipLevel) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setVIP(vipLevel);
        user.setEmail(name.toLowerCase().replace(" ", "") + "@test.com");

        if (userDao.getUserById(id) == null) {
            repository.save(user.toEntity());
        }

        return user;
    }
}