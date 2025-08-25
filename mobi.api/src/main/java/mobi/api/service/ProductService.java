package mobi.api.service;

import mobi.api.repository.ProductRepository;
import mobi.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Lấy tất cả các sản phẩm.
     * Kết quả sẽ được cache với tên "products"
     *
     * @return Danh sách các sản phẩm.
     */
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        System.out.println("Fetching all products from DB..."); // Để thấy khi nào dữ liệu được lấy từ DB
        return productRepository.findAll();
    }

    /**
     * Tạo sản phẩm mới.
     * Khi tạo mới, cần xóa cache của "products" (vì danh sách thay đổi)
     * và xóa cache của "product" nếu id đó có thể trùng.
     *
     * @param product Đối tượng Product cần tạo.
     * @return Sản phẩm đã tạo.
     */
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true), // Xóa toàn bộ cache "products"
            @CacheEvict(value = "product", key = "#product.id", condition = "#product.id != null") // Xóa cache "product" nếu có id
    })
    public Product createProduct(Product product) {
        System.out.println("Saving product to DB: " + product.getName());
        return productRepository.save(product);
    }
}
