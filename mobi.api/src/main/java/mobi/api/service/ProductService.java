package mobi.api.service;

import mobi.api.repository.ProductRepository;
import mobi.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
}
