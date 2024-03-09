package com.project.shop.services;

import com.project.shop.dtos.CategoryDTO;
import com.project.shop.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(long id);
    List<Category> getAllCategory();
    Category updateCategory(long id, CategoryDTO categoryDTO);
    void deleteCategory(long id);
}
