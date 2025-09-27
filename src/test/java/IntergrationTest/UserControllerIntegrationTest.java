package IntergrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gof.ICNBack.Application;
import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Service.EmailService;
import com.gof.ICNBack.Service.OrganisationService;
import com.gof.ICNBack.Service.UserService;
import com.gof.ICNBack.Web.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class) // 明确指定配置类
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
    private User.InitialUser testInitialUser;
    private UserPayment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setPassword("thisispassword");
        testUser.setId("user123");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setCards(Arrays.asList("card1", "card2"));

        testInitialUser = new User.InitialUser(
                "newuser@example.com",
                "New User",
                "pass123",
                "1256"
        );

        testPayment = new UserPayment(
                "newuser@example.com",
                "pass123",
                99.99,
                "1256"
        );
    }

    @Test
    void testUserLogin_Success() throws Exception {
        when(userService.loginUser("test@example.com", "password123")).thenReturn(testUser);
        when(organisationService.getOrgCardsByIds(anyList())).thenReturn(List.of());

        mockMvc.perform(get("/user")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Test User"));

        verify(userService).loginUser("test@example.com", "password123");
    }

    @Test
    void testUserLogin_InvalidCredentials() throws Exception {
        when(userService.loginUser("wrong@example.com", "wrongpass")).thenReturn(null);

        mockMvc.perform(get("/user")
                        .param("email", "wrong@example.com")
                        .param("password", "wrongpass"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Error"));

        verify(userService).loginUser("wrong@example.com", "wrongpass");
    }

    @Test
    void testUserLogin_InvalidEmailFormat() throws Exception {
        mockMvc.perform(get("/user")
                        .param("email", "invalid-email")
                        .param("password", "password123"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testUserLogin_EmptyPassword() throws Exception {
        mockMvc.perform(get("/user")
                        .param("email", "test@example.com")
                        .param("password", ""))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testUserLogin_SQLInjectionAttempt() throws Exception {
        // 测试SQL注入攻击
        mockMvc.perform(get("/user")
                        .param("email", "test@example.com' OR '1'='1")
                        .param("password", "password123"))
                .andExpect(status().isBadRequest()) // 应该被输入过滤拦截
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }

    @Test
    void testUpdateUserInformation_Success() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(true);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());

        verify(userService).updateUser(any(User.class));
    }

    @Test
    void testUpdateUserInformation_Failure() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(false);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(header().exists("X-Error"));

        verify(userService).updateUser(any(User.class));
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

        verify(userService, never()).updateUser(any(User.class));
    }

    @Test
    void testValidateEmail_Success() throws Exception {
        when(emailService.generateValidationCode("test@example.com")).thenReturn("1256");

        mockMvc.perform(get("/user/getCode")
                        .param("email", "test@example.com"))
                .andExpect(status().isAccepted());

        verify(emailService).generateValidationCode("test@example.com");
    }

    @Test
    void testValidateEmail_Failure() throws Exception {
        when(emailService.generateValidationCode("test@example.com")).thenReturn(null);

        mockMvc.perform(get("/user/getCode")
                        .param("email", "test@example.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("X-Error"));

        verify(emailService).generateValidationCode("test@example.com");
    }

    @Test
    void testValidateEmail_InvalidEmail() throws Exception {
        mockMvc.perform(get("/user/getCode")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(emailService, never()).generateValidationCode(anyString());
    }

    @Test
    void testAddUserAccount_Success() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn("1256");
        when(userService.createUser(any(UserEntity.class))).thenReturn(true);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testInitialUser)))
                .andExpect(status().isCreated());

        verify(emailService).getValidationCode("newuser@example.com");
        verify(userService).createUser(any(UserEntity.class));
        //verify(emailService).clearValidationCode("newuser@example.com");
    }

    @Test
    void testAddUserAccount_InvalidCode() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn("654321"); // 错误的验证码

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testInitialUser)))
                .andExpect(status().isConflict())
                .andExpect(header().exists("X-Error"));

        verify(emailService).getValidationCode("newuser@example.com");
        verify(userService, never()).createUser(any(UserEntity.class));
    }

    @Test
    void testAddUserAccount_UserCreationFailed() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn("1256");
        when(userService.createUser(any(UserEntity.class))).thenReturn(false);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testInitialUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists("X-Error"));

        verify(emailService).getValidationCode("newuser@example.com");
        verify(userService).createUser(any(UserEntity.class));
    }

    //TODO: Implements email system before test
    @Test
    void testAddUserAccount_ExpiredCode() throws Exception {
        when(emailService.getValidationCode("newuser@example.com")).thenReturn(null);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testInitialUser)))
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
        UserPayment invalidPayment = new UserPayment(); // 缺少必要字段

        mockMvc.perform(post("/user/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayment)))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));
    }

    // 性能测试
    @Test
    void testUserLogin_Performance() throws Exception {
        when(userService.loginUser("test@example.com", "password123")).thenReturn(testUser);
        when(organisationService.getOrgCardsByIds(anyList())).thenReturn(List.of());

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/user")
                            .param("email", "test@example.com")
                            .param("password", "password123"))
                    .andExpect(status().isOk());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("100 login costs: " + duration + "ms");
        assertTrue(duration < 5000, "should finished within 5 second");
    }

    // 安全性测试 - 大量数据攻击
    @Test
    void testUserLogin_LargeInput() throws Exception {
        String largeEmail = "a".repeat(1000) + "@example.com";

        mockMvc.perform(get("/user")
                        .param("email", largeEmail)
                        .param("password", "password123"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error"));

        verify(userService, never()).loginUser(anyString(), anyString());
    }
}