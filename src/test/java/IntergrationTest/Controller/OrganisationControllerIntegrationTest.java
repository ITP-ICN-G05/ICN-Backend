package IntergrationTest.Controller;

import com.gof.ICNBack.Application;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Entity.Item;
import com.gof.ICNBack.Service.OrganisationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gof.ICNBack.Web.Entity.SearchOrganisationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrganisationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrganisationService organisationService;

    private Organisation.OrganisationCard orgCard1, orgCard2;
    private Organisation organisation;
    private List<Item> sampleItems;

    @BeforeEach
    void setUp() {
        sampleItems = new ArrayList<>();
        sampleItems.add(new Item());
        sampleItems.add(new Item());

        orgCard1 = new Organisation.OrganisationCard(
                "uid001",
                "Company A",
                "123 Main St",
                "Sydney",
                "NSW",
                "2000",
                new ArrayList<>(),
                0.0,
                0.0
        );

        orgCard2 = new Organisation.OrganisationCard(
                "uid002",
                "Company B",
                "456 Oak Ave",
                "Melbourne",
                "VIC",
                "3000",
                new ArrayList<>(),
                0.0,
                0.0
        );

        organisation = new Organisation(
                "org-123",
                "Company A",
                new ArrayList<>(sampleItems),
                "123 Main St",
                "Sydney",
                "NSW",
                "2000",
                new GeoJsonPoint(151.2093, -33.8688) // Sydney coordinates
        );
    }

    @Test
    public void testSearchOrganisation_Success() throws Exception {
        List<Organisation.OrganisationCard> mockCards = Arrays.asList(orgCard1, orgCard2);

        when(organisationService.getOrgCards(any()))
                .thenReturn(mockCards);

        mockMvc.perform(get("/organisation/general")
                        .param("locationX", "151")
                        .param("locationY", "-33")
                        .param("lenX", "10")
                        .param("lenY", "10")
                        .param("searchString", "company")
                        .param("skip", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Company A"))
                .andExpect(jsonPath("$[0].street").value("123 Main St"))
                .andExpect(jsonPath("$[0].city").value("Sydney"))
                .andExpect(jsonPath("$[0].state").value("NSW"))
                .andExpect(jsonPath("$[0].zip").value("2000"))
                .andExpect(jsonPath("$[1].name").value("Company B"))
                .andExpect(jsonPath("$[1].city").value("Melbourne"));
    }

    @Test
    public void testSearchOrganisation_WithFilterParameters() throws Exception {
        List<Organisation.OrganisationCard> mockCards = Arrays.asList(orgCard1);

        when(organisationService.getOrgCards(any()))
                .thenReturn(mockCards);

        mockMvc.perform(get("/organisation/general")
                        .param("locationX", "150")
                        .param("locationY", "-35")
                        .param("lenX", "152")
                        .param("lenY", "-32")
                        .param("filterParameters", "{\"city\": \"Sydney\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].city").value("Sydney"));
    }

    @Test
    public void testSearchOrganisation_EmptyResult() throws Exception {
        when(organisationService.getOrgCards(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/organisation/general")
                        .param("locationX", "151")
                        .param("locationY", "-33")
                        .param("lenX", "10")
                        .param("lenY", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testSearchOrgByIds_Success() throws Exception {
        List<Organisation.OrganisationCard> mockCards = Arrays.asList(orgCard1, orgCard2);

        when(organisationService.getOrgCardsByIds(anyList()))
                .thenReturn(mockCards);

        mockMvc.perform(get("/organisation/generalByIds")
                        .param("ids", "org-123", "org-456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Company A"))
                .andExpect(jsonPath("$[1].name").value("Company B"));
    }

    @Test
    public void testSearchOrgByIds_SingleId() throws Exception {
        List<Organisation.OrganisationCard> mockCards = Collections.singletonList(orgCard1);

        when(organisationService.getOrgCardsByIds(anyList()))
                .thenReturn(mockCards);

        mockMvc.perform(get("/organisation/generalByIds")
                        .param("ids", "org-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Company A"));
    }

    @Test
    public void testSearchOrgByIds_EmptyList() throws Exception {
        when(organisationService.getOrgCardsByIds(anyList()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/organisation/generalByIds")
                        .param("ids", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testSearchOrgByIds_MissingIdsParam() throws Exception {
        mockMvc.perform(get("/organisation/generalByIds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchOrganisationDetail_Success() throws Exception {
        when(organisationService.getOrg(eq("org-123"), eq("testUser")))
                .thenReturn(organisation);

        mockMvc.perform(get("/organisation/specific")
                        .param("organisationId", "org-123")
                        .param("user", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._id").value("org-123"))
                .andExpect(jsonPath("$.name").value("Company A"))
                .andExpect(jsonPath("$.street").value("123 Main St"))
                .andExpect(jsonPath("$.city").value("Sydney"))
                .andExpect(jsonPath("$.state").value("NSW"))
                .andExpect(jsonPath("$.zip").value("2000"))
                .andExpect(jsonPath("$.latitude").exists())
                .andExpect(jsonPath("$.longitude").exists());
    }

    @Test
    public void testSearchOrganisationDetail_NotFound() throws Exception {
        when(organisationService.getOrg(eq("non-existent-id"), eq("testUser")))
                .thenReturn(null);

        mockMvc.perform(get("/organisation/specific")
                        .param("organisationId", "non-existent-id")
                        .param("user", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Error"));
    }

    @Test
    public void testSearchOrganisationDetail_MissingParams() throws Exception {
        mockMvc.perform(get("/organisation/specific")
                        .param("user", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/organisation/specific")
                        .param("organisationId", "org-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchOrganisationDetail_WithItems() throws Exception {
        Organisation orgWithItems = new Organisation(
                "org-123",
                "Company with Items",
                new ArrayList<>(sampleItems),
                "789 Item St",
                "Brisbane",
                "QLD",
                "4000",
                new GeoJsonPoint(153.0251, -27.4698)
        );

        when(organisationService.getOrg(eq("org-123"), eq("testUser")))
                .thenReturn(orgWithItems);

        mockMvc.perform(get("/organisation/specific")
                        .param("organisationId", "org-123")
                        .param("user", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    public void testPaginationParameters() throws Exception {
        List<Organisation.OrganisationCard> mockCards = Arrays.asList(orgCard1);

        SearchOrganisationRequest request = new SearchOrganisationRequest(eq(151), eq(-33), eq(10), eq(10),
                "{}", eq("test"), eq(5), eq(10));

        when(organisationService.getOrgCards(request))
                .thenReturn(mockCards);

        mockMvc.perform(get("/organisation/general")
                        .param("locationX", "151")
                        .param("locationY", "-33")
                        .param("lenX", "10")
                        .param("lenY", "10")
                        .param("searchString", "test")
                        .param("skip", "5")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testOrganisationCardStructure() throws Exception {
        List<Organisation.OrganisationCard> mockCards = Collections.singletonList(orgCard1);

        when(organisationService.getOrgCards(any()))
                .thenReturn(mockCards);

        mockMvc.perform(get("/organisation/general")
                        .param("locationX", "151")
                        .param("locationY", "-33")
                        .param("lenX", "10")
                        .param("lenY", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].items").exists())
                .andExpect(jsonPath("$[0].street").exists())
                .andExpect(jsonPath("$[0].city").exists())
                .andExpect(jsonPath("$[0].state").exists())
                .andExpect(jsonPath("$[0].zip").exists())
                // information in organisation do not exist
                .andExpect(jsonPath("$[0]._id").doesNotExist())
                .andExpect(jsonPath("$[0].coord").doesNotExist());
    }
}