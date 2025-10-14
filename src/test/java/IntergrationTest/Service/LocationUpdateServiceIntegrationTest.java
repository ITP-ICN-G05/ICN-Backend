package IntergrationTest.Service;

import Utils.Utils;
import com.gof.ICNBack.Application;
import com.gof.ICNBack.DataSources.Organisation.OrganisationDao;
import com.gof.ICNBack.Repositories.MongoItemRepository;
import com.gof.ICNBack.Service.GoogleMapsGeocodingService;
import com.gof.ICNBack.Service.LocationUpdateService;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class LocationUpdateServiceIntegrationTest {

    private LocationUpdateService locationUpdateService;

    private OrganisationDao organisationDao;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoItemRepository repo;
    @Autowired
    private GoogleMapsGeocodingService geocodingService;

    // 配置源集合和测试集合的名称
    private static final String SOURCE_COLLECTION = "Organisation";
    private static final String TEST_COLLECTION = "Organisation_Test";

    @BeforeEach
    void setUp() {
        organisationDao = new Utils.Utils.TestOrganisationDao(mongoTemplate, repo);
        locationUpdateService = new LocationUpdateService(geocodingService, organisationDao);
        // clean data
        mongoTemplate.dropCollection(TEST_COLLECTION);

        // transfer data
        transferDataFromSourceToTest();
    }

    private void transferDataFromSourceToTest() {
        // check existence
        if (!mongoTemplate.collectionExists(SOURCE_COLLECTION)) {
            throw new IllegalStateException("source do not exist: " + SOURCE_COLLECTION);
        }

        // take 5 record
        Query query = new Query(
                where("Geocoded").exists(false)
        ).limit(5);

        List<Document> sourceDocuments = mongoTemplate.find(query, Document.class, SOURCE_COLLECTION);

        if (sourceDocuments.isEmpty()) {
            query = new Query().limit(5);
            sourceDocuments = mongoTemplate.find(query, Document.class, SOURCE_COLLECTION);

            // remove positions
            sourceDocuments.forEach(this::removeGeocodeFields);
        }

        // insert into testing data
        if (!sourceDocuments.isEmpty()) {
            mongoTemplate.insert(sourceDocuments, TEST_COLLECTION);
            System.out.println("moved " + sourceDocuments.size() + " data into test collection");
        } else {
            System.out.println("no data found!");
        }
    }

    private void removeGeocodeFields(Document document) {
        if (document.containsKey("Organizations")) {
            List<Document> organizations = (List<Document>) document.get("Organizations");
            organizations.forEach(org -> {
                org.remove("coord");
                org.remove("latitude");
                org.remove("longitude");
            });
        }
    }

    @Test
    void testUpdateLocationsWithGeocoding() {
        // inject mock Service
        GoogleMapsGeocodingService mockGeocodingService = createMockGeocodingService();
        setField(locationUpdateService, "geocodingService", mockGeocodingService);

        // record data state
        long beforeCount = mongoTemplate.getCollection(TEST_COLLECTION).countDocuments();
        System.out.println("data size before testing: " + beforeCount);

        // update location
        locationUpdateService.updateLocationsWithGeocoding();

        // check result
        List<Document> updatedOrganisations = mongoTemplate.findAll(Document.class, TEST_COLLECTION);

        long geocodedCount = updatedOrganisations.stream()
                .filter(doc -> doc.containsKey("Organizations"))
                .map(doc -> (List<Document>) doc.get("Organizations"))
                .flatMap(List::stream)
                .filter(org -> org.containsKey("Organisation: Coord"))
                .count();

        assertTrue(geocodedCount > 0, "should be at least one geocoded address but get : " + geocodedCount);
        System.out.println("get geocoded address of: " + geocodedCount);

        // check invocation
        verify(mockGeocodingService, atLeast(1)).geocodeAddress(anyString());
    }

    @Test
    void testUpdateLocationsWithEmptyData() {
        // check empty data
        mongoTemplate.dropCollection(TEST_COLLECTION);

        GoogleMapsGeocodingService mockGeocodingService = mock(GoogleMapsGeocodingService.class);
        setField(locationUpdateService, "geocodingService", mockGeocodingService);

        assertDoesNotThrow(() -> locationUpdateService.updateLocationsWithGeocoding());

        verify(mockGeocodingService, never()).geocodeAddress(anyString());
    }

    @Test
    void testUpdateLocationsWithGeocodingFailures() {
        // check service failed
        GoogleMapsGeocodingService mockGeocodingService = mock(GoogleMapsGeocodingService.class);
        when(mockGeocodingService.geocodeAddress(anyString())).thenReturn(Optional.empty());

        setField(locationUpdateService, "geocodingService", mockGeocodingService);

        // should not throw error
        assertDoesNotThrow(() -> locationUpdateService.updateLocationsWithGeocoding());

        verify(mockGeocodingService, atLeast(1)).geocodeAddress(anyString());

        // check no geocoding has been made
        List<Document> organisations = mongoTemplate.findAll(Document.class, TEST_COLLECTION);
        long geocodedCount = organisations.stream()
                .filter(doc -> doc.containsKey("Organizations"))
                .map(doc -> (List<Document>) doc.get("Organizations"))
                .flatMap(List::stream)
                .filter(org -> org.containsKey("coord"))
                .count();

        assertEquals(0, geocodedCount, "no geocode should exist");
    }

    @Test
    void testBatchProcessingBehavior() {
        // test batch task
        GoogleMapsGeocodingService mockGeocodingService = createMockGeocodingService();
        setField(locationUpdateService, "geocodingService", mockGeocodingService);

        long dataCount = mongoTemplate.getCollection(TEST_COLLECTION).countDocuments();
        System.out.println("number of records: " + dataCount);

        locationUpdateService.updateLocationsWithGeocoding();

        // same as the number of data
        verify(mockGeocodingService, atLeast((int) dataCount)).geocodeAddress(anyString());
    }

    private GoogleMapsGeocodingService createMockGeocodingService() {
        GoogleMapsGeocodingService mockService = mock(GoogleMapsGeocodingService.class);

        // return mock geocodes
        when(mockService.geocodeAddress(contains("Sydney"))).thenReturn(Optional.of(
                new GoogleMapsGeocodingService.GeocodingResult(-33.8688, 151.2093, "Sydney")));
        when(mockService.geocodeAddress(contains("Melbourne"))).thenReturn(Optional.of(
                new GoogleMapsGeocodingService.GeocodingResult(-37.8136, 144.9631, "Melbourne")));
        when(mockService.geocodeAddress(contains("Brisbane"))).thenReturn(Optional.of(
                new GoogleMapsGeocodingService.GeocodingResult(-27.4698, 153.0251, "Brisbane")));
        when(mockService.geocodeAddress(contains("Perth"))).thenReturn(Optional.of(
                new GoogleMapsGeocodingService.GeocodingResult(-31.9505, 115.8605, "Perth")));
        when(mockService.geocodeAddress(contains("Adelaide"))).thenReturn(Optional.of(
                new GoogleMapsGeocodingService.GeocodingResult(-34.9285, 138.6007, "Adelaide")));

        // return randomly for network test
        when(mockService.geocodeAddress(anyString())).thenAnswer(invocation -> {
            String address = invocation.getArgument(0);
            // 50%
            if (Math.random() > 0) {
                return Optional.of(new GoogleMapsGeocodingService.GeocodingResult(
                        -35.0 + Math.random() * 10,
                        115.0 + Math.random() * 10,
                        "Perth"
                ));
            } else {
                return Optional.empty();
            }
        });

        return mockService;
    }
}
