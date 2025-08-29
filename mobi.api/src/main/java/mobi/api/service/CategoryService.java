package mobi.api.service;


import mobi.api.repository.CategoryRepository;
import mobi.model.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Lớp Service để quản lý logic nghiệp vụ cho Category.
 */
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Lấy tất cả các danh mục.
     *
     * @return Danh sách tất cả danh mục.
     */
    @Cacheable(value = "categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Lấy danh mục theo ID.
     *
     * @param id ID của danh mục.
     * @return Optional chứa danh mục nếu tìm thấy.
     */
    @Cacheable(value = "categoryById", key = "#id")
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Tạo danh mục mới.
     *
     * @param category Đối tượng Category cần tạo.
     * @return Danh mục đã tạo.
     */
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "category", key = "category.id", condition = "category.id != null")
    })
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Cập nhật danh mục hiện có.
     *
     * @param id              ID của danh mục cần cập nhật.
     * @param categoryDetails Đối tượng Category với thông tin cập nhật.
     * @return Optional chứa danh mục đã cập nhật nếu tìm thấy.
     */
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "category", key = "#id")
    })
    public Optional<Category> updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id).map(existingCategory -> {
            existingCategory.setName(categoryDetails.getName());
            existingCategory.setDescription(categoryDetails.getDescription());
            existingCategory.setParentCategory(categoryDetails.getParentCategory());
            return categoryRepository.save(existingCategory);
        });
    }

    /**
     * Xóa danh mục theo ID.
     *
     * @param id ID của danh mục cần xóa.
     */
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "category", key = "#id")
    })
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Kiểm tra xem danh mục có tồn tại theo tên không.
     *
     * @param name Tên danh mục.
     * @return true nếu tồn tại, ngược lại false.
     */
    @Cacheable(value = "categoryByName", key = "name")
    public Boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}
