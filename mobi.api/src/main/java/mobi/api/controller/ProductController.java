package mobi.api.controller;

import mobi.api.service.ProductService;
import mobi.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
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
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
}
