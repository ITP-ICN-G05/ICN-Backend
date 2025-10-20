package com.gof.ICNBack.Web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gof.ICNBack.Entity.User;
import com.gof.ICNBack.Entity.UserPayment;
import com.gof.ICNBack.Security.JwtUtil;
import com.gof.ICNBack.Service.EmailService;
import com.gof.ICNBack.Service.OrganisationService;
import com.gof.ICNBack.Service.UserService;
import com.gof.ICNBack.Utils.PasswordUtil;
import static com.gof.ICNBack.Web.Utils.Validator.isValidEmail;
import static com.gof.ICNBack.Web.Utils.Validator.isValidInitialUser;
import static com.gof.ICNBack.Web.Utils.Validator.isValidPassword;
import static com.gof.ICNBack.Web.Utils.Validator.isValidPayment;
import static com.gof.ICNBack.Web.Utils.Validator.isValidUserProfileUpdate;

/// @TODO: error handling
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService repo;

    @Autowired
    EmailService email;

    @Autowired
    OrganisationService orgRepo;

    @Autowired
    Environment env;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordUtil passwordUtil;

    // Login Request DTO
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * New secure login endpoint using POST and BCrypt password verification
     * POST /user/login
     */
    @PostMapping("/login")
    public ResponseEntity<User.UserFull> UserLoginSecure(
            @RequestBody LoginRequest loginRequest) {

        String emailParam = loginRequest.getEmail();
        String passwordParam = loginRequest.getPassword();

        if (!isValidEmail(emailParam) || !isValidPassword(passwordParam)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        // Get user by email only
        User user = repo.getUserByEmail(emailParam);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Error", "invalid credentials")
                    .build();
        }

        // Verify password using BCrypt
        if (!passwordUtil.verifyPassword(passwordParam, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Error", "invalid credentials")
                    .build();
        }

        // Generate JWT tokens
        String token = jwtUtil.generateToken(emailParam);
        String refreshToken = jwtUtil.generateRefreshToken(emailParam);

        // Create UserFull with tokens
        User.UserFull userF = user.getFullUser(orgRepo.getOrgCardsByIds(user.getCards()));

        // Create new UserFull with tokens
        User.UserFull userFWithTokens = new User.UserFull(
                userF.id,
                userF.cards,
                userF.name,
                userF.email,
                userF.phone,
                userF.company,
                userF.role,
                userF.avatar,
                userF.VIP,
                userF.endDate,
                userF.createdAt,
                token,
                refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(userFWithTokens);
    }

    /**
     * @deprecated Use POST /user/login instead for security
     *             Legacy login endpoint - kept temporarily for backward
     *             compatibility
     */
    @Deprecated

    @GetMapping
    public ResponseEntity<User.UserFull> UserLogin(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password) {
        if (!isValidEmail(email) || !isValidPassword(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }
        User user = repo.loginUser(email, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("X-Error", "invalid user account").build();
        }

        // Generate JWT tokens
        String token = jwtUtil.generateToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // Create UserFull with tokens using the existing getFullUser method and then
        // add tokens
        User.UserFull userF = user.getFullUser(orgRepo.getOrgCardsByIds(user.getCards()));

        // Create new UserFull with tokens
        User.UserFull userFWithTokens = new User.UserFull(
                userF.id,
                userF.cards,
                userF.name,
                userF.email,
                userF.phone,
                userF.company,
                userF.role,
                userF.avatar,
                userF.VIP,
                userF.endDate,
                userF.createdAt,
                token,
                refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(userFWithTokens);
    }

    @PutMapping
    public ResponseEntity<?> updateUserInformation(
            @RequestBody User user) {
        // Use profile update validation (password not required)
        if (!isValidUserProfileUpdate(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        if (repo.updateUser(user)) {
            // Return success with empty JSON object
            // Frontend doesn't need the full user object for profile updates
            return ResponseEntity.status(HttpStatus.OK).body("{}");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("X-Error", "user not found or update failed").build();
    }

    @GetMapping("/getCode")
    public ResponseEntity<Void> validateEmail(
            @RequestParam(required = true) String email) {
        if (!isValidEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        String code = this.email.generateValidationCode(email);
        Boolean result = false;
        if (code != null) {
            result = this.email.sendCode(code, email);
        }
        return result ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("X-Error", "something wrong with the server")
                        .build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> addUserAccount(
            @RequestBody User.InitialUser initialUser) {
        // Log incoming registration request (excluding password for security)
        logger.info("User registration request received - email: {}, name: {}",
                initialUser != null ? initialUser.getEmail() : "null",
                initialUser != null ? initialUser.getName() : "null");

        // Validate input parameters
        int codeLength = env.getProperty("app.mail.code.length", Integer.class, 6);
        if (!isValidInitialUser(initialUser, codeLength)) {
            logger.warn(
                    "User registration validation failed - email: {}, Expected code length: {}, Actual code length: {}",
                    initialUser != null ? initialUser.getEmail() : "null",
                    codeLength,
                    initialUser != null && initialUser.getCode() != null ? initialUser.getCode().length() : 0);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        try {
            // Check if verification code matches
            List<String> storedCodes = email.getValidationCode(initialUser.getEmail());
            logger.debug("Verification code check - email: {}, code provided: {}, stored codes: {}",
                    initialUser.getEmail(),
                    initialUser.getCode(),
                    storedCodes != null && !storedCodes.isEmpty() ? "exists" : "not found");

            if (storedCodes != null && storedCodes.contains(initialUser.getCode())) {
                // Hash password before saving
                User user = initialUser.toUser();
                user.setPassword(passwordUtil.hashPassword(user.getPassword()));

                logger.info("Creating user account - email: {}", initialUser.getEmail());
                if (repo.createUser(user.toEntity())) {
                    logger.info("User account created successfully - email: {}", initialUser.getEmail());
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                } else {
                    logger.error("Failed to create user account (user may already exist) - email: {}",
                            initialUser.getEmail());
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .header("X-Error", "user already exists or database error")
                            .build();
                }
            } else {
                logger.warn("Invalid verification code - email: {}, provided code does not match",
                        initialUser.getEmail());
                return ResponseEntity.status(409).header("X-Error", "invalid validation code").build();
            }
        } catch (Exception e) {
            logger.error("Exception occurred during user registration - email: {}, error: {}",
                    initialUser != null ? initialUser.getEmail() : "null",
                    e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error", "server error: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String code,
            @RequestParam(required = true) String newPassword) {
        if (!isValidEmail(email) || !isValidPassword(newPassword) || code == null || code.length() != 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }

        try {
            // Verify the validation code
            if (!this.email.getValidationCode(email).contains(code)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .header("X-Error", "invalid validation code")
                        .build();
            }

            // Find user by email and update password
            User user = repo.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("X-Error", "user not found")
                        .build();
            }

            // Hash new password before saving
            user.setPassword(passwordUtil.hashPassword(newPassword));
            if (repo.updateUser(user)) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("X-Error", "failed to update password")
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error", "something wrong with the server")
                    .build();
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> userPayment(
            @RequestBody UserPayment userPayment) {
        if (!isValidPayment(userPayment)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "invalid input")
                    .build();
        }
        // TODO: create User payment, update account state
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("X-Error", "Service developing").build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .header("X-Error", "refresh token required")
                        .build();
            }

            // Validate refresh token
            if (jwtUtil.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .header("X-Error", "refresh token expired")
                        .build();
            }

            // Extract email from refresh token
            String userEmail = jwtUtil.getEmailFromToken(refreshToken);

            // Generate new access token
            String newToken = jwtUtil.generateToken(userEmail);
            String newRefreshTokenValue = jwtUtil.generateRefreshToken(userEmail);

            TokenResponse response = new TokenResponse(newToken, newRefreshTokenValue);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Error", "invalid refresh token")
                    .build();
        }
    }

    // Helper classes for token refresh
    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public static class TokenResponse {
        private String token;
        private String refreshToken;

        public TokenResponse(String token, String refreshToken) {
            this.token = token;
            this.refreshToken = refreshToken;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    // Bookmark API Endpoints

    /**
     * Add a bookmark for a user
     * POST /user/bookmarks/add
     */
    @PostMapping("/bookmarks/add")
    public ResponseEntity<Void> addBookmark(
            @RequestParam(required = true) String userId,
            @RequestParam(required = true) String companyId) {
        if (userId == null || userId.isEmpty() || companyId == null || companyId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "userId and companyId are required")
                    .build();
        }

        boolean success = repo.addBookmark(userId, companyId);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error", "failed to add bookmark")
                    .build();
        }
    }

    /**
     * Remove a bookmark for a user
     * POST /user/bookmarks/remove
     */
    @PostMapping("/bookmarks/remove")
    public ResponseEntity<Void> removeBookmark(
            @RequestParam(required = true) String userId,
            @RequestParam(required = true) String companyId) {
        if (userId == null || userId.isEmpty() || companyId == null || companyId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "userId and companyId are required")
                    .build();
        }

        boolean success = repo.removeBookmark(userId, companyId);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error", "failed to remove bookmark")
                    .build();
        }
    }

    /**
     * Get all bookmarks for a user
     * GET /user/bookmarks
     */
    @GetMapping("/bookmarks")
    public ResponseEntity<List<String>> getBookmarks(
            @RequestParam(required = true) String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "userId is required")
                    .build();
        }

        List<String> bookmarks = repo.getBookmarks(userId);
        return ResponseEntity.status(HttpStatus.OK).body(bookmarks);
    }

    /**
     * Sync bookmarks for a user (batch update)
     * POST /user/bookmarks/sync
     */
    @PostMapping("/bookmarks/sync")
    public ResponseEntity<Void> syncBookmarks(
            @RequestParam(required = true) String userId,
            @RequestBody List<String> bookmarks) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error", "userId is required")
                    .build();
        }

        boolean success = repo.syncBookmarks(userId, bookmarks);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error", "failed to sync bookmarks")
                    .build();
        }
    }
}
