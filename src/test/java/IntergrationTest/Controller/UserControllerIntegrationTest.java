package IntergrationTest.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gof.ICNBack.Application;
import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Service.EmailService;
import com.gof.ICNBack.Service.OrganisationService;
import com.gof.ICNBack.Service.UserService;
import com.gof.ICNBack.Web.Entity.CreateUserRequest;
import com.gof.ICNBack.Web.Entity.UpdateUserRequest;
import io.pebbletemplates.pebble.extension.core.Sha256Filter;
import org.apache.tomcat.util.security.MD5Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.creation.bytebuddy.MockMethodInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private OrganisationService organisationService;

    private User testUser;
    private CreateUserRequest testCreateUserRequest;
    private UserPayment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setPassword("7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662");
        testUser.setId("user123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setOrganisationIds(Arrays.asList("card1", "card2"));

        testCreateUserRequest = new CreateUserRequest(
                "newuser@example.com",
                "New User",
                "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662",
                "1256"
        );

        testPayment = new UserPayment(
                "newuser@example.com",
                "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662",
                99.99,
                "1256"
        );
    }

    @Test
    void testUserLogin_Success() throws Exception {
        when(userService.loginUser("test@example.com", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662")).thenReturn(testUser);
        when(organisationService.getOrgCardsByIds(anyList())).thenReturn(List.of());

        mockMvc.perform(post("/user")
                        .param("email", "test@example.com")
                        .param("password", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Test User"));

        verify(userService).loginUser("test@example.com", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662");
    }

    @Test
    void testUserLogin_InvalidCredentials() throws Exception {
        when(userService.loginUser("wrong@example.com", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662")).thenReturn(null);

        mockMvc.perform(post("/user")
                        .param("email", "wrong@example.com")
                        .param("password", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Error"));

        verify(userService).loginUser("wrong@example.com", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662");
    }

    @Test
    void testUserLogin_InvalidEmailFormat() throws Exception {
        mockMvc.perform(post("/user")
                        .param("email", "invalid-email")
                        .param("password", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testUserLogin_EmptyPassword() throws Exception {
        mockMvc.perform(post("/user")
                        .param("email", "test@example.com")
                        .param("password", ""))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testUserLogin_SQLInjectionAttempt() throws Exception {
        mockMvc.perform(post("/user")
                        .param("email", "test@example.com' OR '1'='1")
                        .param("password", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testUpdateUserInformation_Success() throws Exception {
        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(true);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());

        verify(userService).updateUser(any(UpdateUserRequest.class));
    }

    @Test
    void testUpdateUserInformation_Failure() throws Exception {
        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(false);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Error"));

        verify(userService).updateUser(any(UpdateUserRequest.class));
    }

    @Test
    void testUpdateUserInformation_MaliciousInput() throws Exception {
        User maliciousUser = new User();
        maliciousUser.setName("<script>alert('xss')</script>");
        maliciousUser.setEmail("test@example.com");

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maliciousUser)))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).updateUser(any(UpdateUserRequest.class));
    }

    @Test
    void testValidateEmail_Failure() throws Exception {
        when(emailService.generateValidationCode("test@example.com")).thenReturn(null);

        mockMvc.perform(post("/user/getCode")
                        .param("email", "test@example.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("X-Error"));

        verify(emailService).generateValidationCode("test@example.com");
    }

    @Test
    void testValidateEmail_InvalidEmail() throws Exception {
        mockMvc.perform(post("/user/getCode")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(emailService, never()).generateValidationCode(anyString());
    }

    @Test
    void testAddUserAccount_Success() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn(List.of("1256"));
        when(userService.createUser(any(UserEntity.class))).thenReturn(true);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateUserRequest)))
                .andExpect(status().isCreated());

        verify(emailService).getValidationCode("newuser@example.com");
        verify(userService).createUser(any(UserEntity.class));
        //verify(emailService).clearValidationCode("newuser@example.com");
    }

    @Test
    void testAddUserAccount_InvalidCode() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn(List.of("654321"));

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateUserRequest)))
                .andExpect(status().isConflict())
                .andExpect(header().exists("X-Error"));

        verify(emailService).getValidationCode("newuser@example.com");
        verify(userService, never()).createUser(any(UserEntity.class));
    }

    @Test
    void testAddUserAccount_UserCreationFailed() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn(List.of("1256"));
        when(userService.createUser(any(UserEntity.class))).thenReturn(false);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateUserRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("X-Error"));

        verify(emailService).getValidationCode("newuser@example.com");
        verify(userService).createUser(any(UserEntity.class));
    }

    @Test
    void testAddUserAccount_ExpiredCode() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn(null);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateUserRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("X-Error"));

        verify(emailService).getValidationCode("newuser@example.com");
        verify(userService, never()).createUser(any(UserEntity.class));
    }

    @Test
    void testUserPayment_ServiceUnavailable() throws Exception {
        mockMvc.perform(post("/user/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPayment)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().exists("X-Error"));
    }

    @Test
    void testUserPayment_InvalidData() throws Exception {
        UserPayment invalidPayment = new UserPayment();

        mockMvc.perform(post("/user/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayment)))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));
    }

    @Test
    void testUserLogin_Performance() throws Exception {
        when(userService.loginUser("test@example.com", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662")).thenReturn(testUser);
        when(organisationService.getOrgCardsByIds(anyList())).thenReturn(List.of());

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            mockMvc.perform(post("/user")
                            .param("email", "test@example.com")
                            .param("password", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662"))
                    .andExpect(status().isOk());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("100 login costs: " + duration + "ms");
        assertTrue(duration < 5000, "should finished within 5 second");
    }

    @Test
    void testUserLogin_LargeInput() throws Exception {
        String largeEmail = "a".repeat(1000) + "@example.com";

        mockMvc.perform(post("/user")
                        .param("email", largeEmail)
                        .param("password", "7636f2ca97568363a757b6e4c255fcf55410fa2e84b277ada79951f425e3b662"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }
}