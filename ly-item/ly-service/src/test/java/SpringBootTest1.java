import com.lx.entity.Brand;
import com.lx.entity.Category;
import com.lx.leyou.service.BrandService;
import com.lx.leyou.service.CategoryService;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootTest
public class SpringBootTest1 {

    @Test
    public void test() {
        BrandService brandService = new BrandService();
        brandService.queryListByLike("", 1, 5, "id", true);
    }

    @Test
    public void test1() {
        Brand brand = new Brand();
        brand.insert();
        System.out.println(brand.getId());
    }
}
