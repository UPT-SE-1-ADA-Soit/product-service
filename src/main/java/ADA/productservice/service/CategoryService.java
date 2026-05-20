package ADA.productservice.service;

import ADA.productservice.dto.CategoryRequest;
import ADA.productservice.entity.Category;
import ADA.productservice.exception.ResourceNotFoundException;
import ADA.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public Category create(CategoryRequest request) {
        return repository.save(Category.builder().name(request.getName()).build());
    }

    public Category update(Integer id, CategoryRequest request) {
        Category category = findById(id);
        category.setName(request.getName());
        return repository.save(category);
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        repository.deleteById(id);
    }
}
