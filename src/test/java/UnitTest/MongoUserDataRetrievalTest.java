package UnitTest;

import com.gof.ICNBack.Application;
import com.gof.ICNBack.DataSources.Entity.UserEntity;
import com.gof.ICNBack.DataSources.User.UserDao;
import com.gof.ICNBack.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MongoUserDataRetrievalTest {
    @Autowired
    private UserDao userDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TEST_COLLECTION = "User";

    @BeforeEach
    public void initData() {
        // 清空测试数据
        mongoTemplate.dropCollection(TEST_COLLECTION);

        // 插入测试数据
        List<UserEntity> testUsers = Arrays.asList(
                new UserEntity(null, 0, "steve@example.com", "Steve", "pass", List.of("id_1", "id2")),
                new UserEntity(null, 1, "qie@example.com", "QiE", "pasdassw", List.of()),

                new UserEntity(null, 2, "zhou@example.com", "LaoZhou", "pass_shDSvas1", List.of("id_1")),

                new UserEntity(null, -1, "lucien@example.com", "Lucien", "pass", List.of()),

                new UserEntity(null, 0, "tyrone@example.com", "Tyrone", "pass", List.of())
        );
        for (UserEntity u : testUsers){
            userDao.create(u);
        }
    }

    @Test
    public void testFindUserByPair() {
        // 测试获取所有用户
        User user = userDao.getUserByPair("qie@example.com", "pasdassw");

        assertNotNull(user, "用户列表不应为null");

        // 验证用户数据完整性
        assertNotNull(user.getId(), "用户ID不应为null");
        assertNotNull(user.getName(), "用户名不应为null");
        assertNotNull(user.getEmail(), "邮箱不应为null");
        assertTrue(user.getEmail().contains("@"), "邮箱格式应正确");
    }

    @Test
    public void testFindOrgIdByEmail() {
        // 测试通过邮箱查找用户
        String testEmail = "steve@example.com";
        List<String> orgs = userDao.getOrgIdByUser(testEmail);

        assertNotNull(orgs, "failed to extract org names");

        System.out.println("find orgs: " + orgs);
    }

    @Test
    public void testDataConsistency() {
        // 测试数据一致性
        User userBefore = userDao.getUserByPair("qie@example.com", "pasdassw");

        // 插入新用户
        UserEntity newUser =  new UserEntity(userBefore.getId(), 2, "qie@example.com", "Qie", "pasdassw", List.of("id_1", "id2"));
        userDao.update(newUser);

        // 验证数据已更新
        User userAfter = userDao.getUserByPair("qie@example.com", "pasdassw");

        assertEquals(userAfter.getVIP(), 2);

        // 验证新用户存在
        assertNotNull(userAfter, "should find the user");
        assertEquals(userAfter.getId(), userBefore.getId());
    }
}
