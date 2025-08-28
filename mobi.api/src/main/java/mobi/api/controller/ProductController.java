package mobi.api.controller;

import mobi.api.service.ProductService;
import mobi.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
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
}
