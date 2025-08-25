package mobi.api.controller;

import mobi.api.service.ProductService;
import mobi.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
