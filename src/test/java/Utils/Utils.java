package Utils;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Organisation.MongoOrganisationDao;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.MongoItemRepository;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

import static com.gof.ICNBack.DataSources.Utils.MongoUtils.processToItemEntity;
import static com.gof.ICNBack.DataSources.Utils.MongoUtils.processToOrganisations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class Utils {

    /**
     * Spacial DAO, only modifies Test_Collection
     */
    public static class TestOrganisationDao extends MongoOrganisationDao {

        private MongoTemplate mongoTemplate;
        private static final String TEST_COLLECTION = "Organisation_Test";

        public TestOrganisationDao(MongoTemplate template, MongoItemRepository repo){
            super();
            this.mongoTemplate = Mockito.spy(template);
            setField(this, "templates", mongoTemplate);
            setField(this, "repo", repo);
            when(mongoTemplate.getCollectionName(ItemEntity.class)).thenReturn(TEST_COLLECTION);
        }

        @Override
        public List<Organisation> searchOrganisations(
                double locationX,
                double locationY,
                double endX,
                double endY,
                Map<String, String> filterParameters,
                String searchString,
                Integer skip,
                Integer limit) {



            return super.searchOrganisations(
                    locationX,
                    locationY,
                    endX,
                    endY,
                    filterParameters,
                    searchString,
                    skip,
                    limit
            );
        }

        @Override
        public List<Organisation> getOrganisationsWithoutGeocode() {
            Query query = new Query((new Criteria().orOperator(where("Geocoded").exists(false), where("Geocoded").is(false))));
            return processToOrganisations(mongoTemplate.find(query, ItemEntity.class, TEST_COLLECTION));
        }

        @Override
        public void updateGeocode(List<Organisation> organisations) {
            for (ItemEntity e:processToItemEntity(organisations)){
                mongoTemplate.save(e, TEST_COLLECTION);
            }

        }
    }
}
