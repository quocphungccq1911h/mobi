package mobi.api.controller;

import mobi.api.service.ProductService;
import mobi.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lấy tất cả các sản phẩm.
     * GET /api/products
     *
     * @return Danh sách các sản phẩm.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("permitAll()")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Tạo sản phẩm mới.
     * POST /api/products
     *
     * @param product Đối tượng Product cần tạo.
     * @return ResponseEntity chứa sản phẩm đã tạo và status CREATED.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        System.out.println("ProductController: createProduct called with name: " + product.getName());
        Product savedProduct = productService.createProduct(product);
        // Đảm bảo savedProduct không null trước khi trả về
        if (savedProduct != null) {
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } else {
            // Trường hợp lỗi khi lưu sản phẩm, trả về lỗi server
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy sản phẩm theo ID.
     * Mọi người đều có thể truy cập.
     * GET /api/products/{id}
     *
     * @param id ID của sản phẩm.
     * @return ResponseEntity chứa sản phẩm hoặc status NOT_FOUND.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        System.out.println("ProductController: getProductById called for ID: " + id);
        Optional<Product> product = productService.getProductById(id);

        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cập nhật sản phẩm hiện có.
     * Chỉ ADMIN mới có thể truy cập.
     * PUT /api/products/{id}
     *
     * @param id             ID của sản phẩm cần cập nhật.
     * @param productDetails Đối tượng Product với thông tin cập nhật.
     * @return ResponseEntity chứa sản phẩm đã cập nhật hoặc status NOT_FOUND.
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        System.out.println("ProductController: updateProduct called for ID: " + id);
        Optional<Product> updatedProduct = productService.updateProduct(id, productDetails);
        return updatedProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Xóa sản phẩm theo ID.
     * Chỉ ADMIN mới có thể truy cập.
     * DELETE /api/products/{id}
     *
     * @param id ID của sản phẩm cần xóa.
     * @return ResponseEntity với status NO_CONTENT hoặc NOT_FOUND.
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        System.out.println("ProductController: deleteProduct called for ID: " + id);
        if (productService.getProductById(id).isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint kiểm tra serialization JSON cơ bản.
     * Mọi người đều có thể truy cập.
     * GET /api/products/test-json
     *
     * @return Một đối tượng Map đơn giản.
     */
    @GetMapping(value = "/test-json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("permitAll()") // Cho phép tất cả mọi người truy cập
    public Map<String, String> testJsonSerialization() {
        System.out.println("ProductController: testJsonSerialization called.");
        return Map.of("message", "Test JSON works!", "status", "success");
    }

    /**
     * Endpoint kiểm tra serialization một đối tượng Product hardcoded.
     * Mọi người đều có thể truy cập.
     * GET /api/products/test-product
     *
     * @return Một đối tượng Product hardcoded.
     */
    @GetMapping(value = "/test-product", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("permitAll()") // Cho phép tất cả mọi người truy cập
    public Product testProductSerialization() {
        System.out.println("ProductController: testProductSerialization called.");
        Product testProduct = new Product();
        testProduct.setId(100L);
        testProduct.setName("Test Product Hardcoded");
        testProduct.setPrice(new BigDecimal("123.45"));
        testProduct.setDescription("This is a hardcoded test product.");
        return testProduct;
    }
}
