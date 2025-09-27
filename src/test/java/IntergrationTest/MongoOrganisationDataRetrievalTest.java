package IntergrationTest;

import com.gof.ICNBack.Application;
import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Organisation.OrganisationDao;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.MongoOrganisationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MongoOrganisationDataRetrievalTest {
    @Autowired
    private OrganisationDao orgDao;

    @Autowired
    private MongoOrganisationRepository repo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testRepo() {
        List<ItemEntity> items = repo.findByOrganisationId("0017F00001ueJZy");

        assertNotNull(items, "org should not be null");

        assertNotEquals(items.size(), 0, "len should not be null");
    }

    @Test
    public void testGetOrganisationById() {
        Organisation org = orgDao.getOrganisationById("0017F00001ueJZy");

        assertNotNull(org, "org should not be null");

        assertNotNull(org.getAddress(), "addr should not be null");
        assertNotNull(org.getItems(), "items should not be null");

        System.out.println("find org: " + org);
        System.out.println("find items: " + org.getItems());
    }

    @Test
    public void testGetOrganisationsWithoutGeocode() {
        List<Organisation> org = orgDao.getOrganisationsWithoutGeocode();

        assertNotNull(org, "org should not be null");

        assertNull(org.get(0).getCoord(), "Coord should be null");

        System.out.println("find  orgs: " + org.size());
    }
}
