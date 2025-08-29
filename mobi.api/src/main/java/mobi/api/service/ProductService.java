package mobi.api.service;

import mobi.api.payload.request.ProductRequest;
import mobi.api.repository.CategoryRepository;
import mobi.api.repository.ProductRepository;
import mobi.model.entity.Category;
import mobi.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
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
     * @param productRequest Đối tượng Product cần tạo.
     * @return Sản phẩm đã tạo.
     */
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true), // Xóa toàn bộ cache "products"
            @CacheEvict(value = "product", key = "#product.id", condition = "#product.id != null") // Xóa cache "product" nếu có id
    })
    public Product createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());

        // Gán Category nếu categoryId được cung cấp
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found with id: " + productRequest.getCategoryId()));
            product.setCategory(category);
        }

        System.out.println("Saving product to DB: " + product.getName() + " (Caching disabled)");
        return productRepository.save(product);
    }

    @Cacheable(value = "productById", key = "#id")
    public Optional<Product> getProductById(Long id) {
        System.out.println("Find product from DB: " + id);
        return productRepository.findById(id);
    }

    /**
     * Cập nhật sản phẩm hiện có.
     * Khi cập nhật, cần xóa cache của "products" và cache của "product" cụ thể này.
     *
     * @param id             ID của sản phẩm cần cập nhật.
     * @param productRequest Đối tượng Product với thông tin cập nhật.
     * @return Optional chứa sản phẩm đã cập nhật.
     */
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product", key = "#id")
    })
    public Optional<Product> updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).map(existingProduct -> {
            System.out.println("Updating product in DB: " + existingProduct.getName() + " (Caching disabled)");
            existingProduct.setName(productRequest.getName());
            existingProduct.setPrice(productRequest.getPrice());
            existingProduct.setDescription(productRequest.getDescription());

            // Cập nhật Category nếu categoryId được cung cấp
            if (productRequest.getCategoryId() != null) {
                Category category = categoryRepository.findById(productRequest.getCategoryId())
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found with id: " + productRequest.getCategoryId()));
                existingProduct.setCategory(category);
            } else {
                // Nếu categoryId là null, có thể hiểu là muốn gỡ liên kết danh mục
                existingProduct.setCategory(null);
            }

            return productRepository.save(existingProduct);
        });
    }

    /**
     * Xóa sản phẩm theo ID.
     * Khi xóa, cần xóa cache của "products" và cache của "product" cụ thể này.
     *
     * @param id ID của sản phẩm cần xóa.
     */
    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product", key = "#id")
    })
    public void deleteProduct(Long id) {
        System.out.println("Deleting product from DB with ID: " + id + " (Caching disabled)");
        productRepository.deleteById(id);
    }


}
