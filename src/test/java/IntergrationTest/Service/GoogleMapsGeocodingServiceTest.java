package IntergrationTest.Service;

import com.gof.ICNBack.Application;
import com.gof.ICNBack.Service.GoogleMapsGeocodingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.bson.assertions.Assertions.assertNotNull;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class GoogleMapsGeocodingServiceTest {

    private GoogleMapsGeocodingService geocodingService;

    @Autowired
    public GoogleMapsGeocodingServiceTest(GoogleMapsGeocodingService geocodingService){
        this.geocodingService = geocodingService;
    }

    @Test
    public void testGeocodingService() {
        // 准备几个地址
        String address1 = "4 Kings Road, New Lambton, NSW, 2305";
        String address2 = "Bapaume Road, MooreBank, NSW, 2170";
        String address3 = "41 Kings Road, New Lambton, NSW, 2305";

        // 测试地理编码服务
        assertNotNull(geocodingService.geocodeAddress(address1));
        assertNotNull(geocodingService.geocodeAddress(address2));
        assertNotNull(geocodingService.geocodeAddress(address3));
    }
}