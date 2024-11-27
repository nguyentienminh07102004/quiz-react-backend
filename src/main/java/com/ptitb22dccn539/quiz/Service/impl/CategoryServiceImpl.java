package com.ptitb22dccn539.quiz.Service.impl;

import com.ptitb22dccn539.quiz.Convertors.CategoryConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.DTO.CategoryDTO;
import com.ptitb22dccn539.quiz.Model.Entity.CategoryEntity;
import com.ptitb22dccn539.quiz.Model.Response.CategoryResponse;
import com.ptitb22dccn539.quiz.Repositoty.ICategoryRepository;
import com.ptitb22dccn539.quiz.Service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryRepository categoryRepository;
    private final CategoryConvertor categoryConvertor;

    @Override
    @Transactional
    public CategoryResponse save(CategoryDTO categoryDTO) {
        if(categoryDTO.getId() != null) {
            categoryRepository.findById(categoryDTO.getId())
                    .orElseThrow(() -> new DataInvalidException("Category not found!"));
        }
        CategoryEntity category = categoryConvertor.dtoToEntity(categoryDTO);
        CategoryEntity savedCategory = categoryRepository.save(category);
        return categoryConvertor.entityToResponse(savedCategory);
    }

    @Override
    @Transactional
    public void deleteByIds(List<String> ids) {
        ids.forEach(id -> categoryRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("Category %s not found!".formatted(id))));
        categoryRepository.deleteAllById(ids);
    }

    @Override
    public CategoryEntity getCategoryEntityByCode(String id) {
        return categoryRepository.findByCode(id)
                .orElseThrow(() -> new DataInvalidException("Category not found!"));
    }

    @Override
    public CategoryResponse getCategoryResponseByCode(String id) {
        return categoryConvertor.entityToResponse(this.getCategoryEntityByCode(id));
    }

    @Override
    public PagedModel<CategoryResponse> getAllCategory(Integer page) {
        if(page == null) page = 1;
        Sort sort = Sort.by("rating");
        Pageable pageable = PageRequest.of(page - 1, 5, sort);
        Page<CategoryEntity> list = categoryRepository.findAll(pageable);
        return new PagedModel<>(list.map(categoryConvertor::entityToResponse));
    }

    @Override
    public List<CategoryResponse> getAllNoPagination() {
        List<CategoryEntity> list = categoryRepository.findAll();
        return list.stream()
                .map(categoryConvertor::entityToResponse)
                .toList();
    }
}
