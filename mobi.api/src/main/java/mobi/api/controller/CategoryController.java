package mobi.api.controller;

import jakarta.validation.Valid;
import mobi.api.payload.response.MessageResponse;
import mobi.api.service.CategoryService;
import mobi.model.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller để quản lý các API liên quan đến danh mục sản phẩm.
 * Chỉ ADMIN mới có quyền thực hiện các thao tác CRUD.
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(value = "*", maxAge = 3600)
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Lấy tất cả các danh mục.
     * Mọi người đều có thể truy cập.
     * GET /api/categories
     *
     * @return Danh sách tất cả danh mục.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Lấy danh mục theo ID.
     * Mọi người đều có thể truy cập.
     * GET /api/categories/{id}
     *
     * @param id ID của danh mục.
     * @return ResponseEntity chứa danh mục hoặc status NOT_FOUND.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Tạo danh mục mới.
     * Chỉ ADMIN mới có thể truy cập.
     * POST /api/categories
     *
     * @param category Đối tượng Category cần tạo.
     * @return ResponseEntity chứa danh mục đã tạo và status CREATED hoặc lỗi.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category) {
        if (categoryService.existsByName(category.getName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Category name is already in use!"));
        }
        Category newCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    /**
     * Cập nhật danh mục hiện có.
     * Chỉ ADMIN mới có thể truy cập.
     * PUT /api/categories/{id}
     *
     * @param id              ID của danh mục cần cập nhật.
     * @param categoryDetails Đối tượng Category với thông tin cập nhật.
     * @return ResponseEntity chứa danh mục đã cập nhật hoặc status NOT_FOUND hoặc lỗi.
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody Category categoryDetails) {
        Optional<Category> updatedCategory = categoryService.updateCategory(id, categoryDetails);
        return updatedCategory.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Xóa danh mục theo ID.
     * Chỉ ADMIN mới có thể truy cập.
     * DELETE /api/categories/{id}
     *
     * @param id ID của danh mục cần xóa.
     * @return ResponseEntity với status NO_CONTENT hoặc NOT_FOUND.
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable Long id) {
        if (categoryService.getCategoryById(id).isPresent()) {
            categoryService.deleteCategory(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
