package com.ptitb22dccn539.quiz.Convertors;

import com.github.slugify.Slugify;
import com.ptitb22dccn539.quiz.Model.DTO.CategoryDTO;
import com.ptitb22dccn539.quiz.Model.Entity.CategoryEntity;
import com.ptitb22dccn539.quiz.Model.Response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryConvertor implements IConvertor<CategoryDTO, CategoryEntity, CategoryResponse> {
    private final ModelMapper modelMapper;
    private final Slugify slugify;

    @Override
    public CategoryResponse entityToResponse(CategoryEntity category) {
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public CategoryEntity dtoToEntity(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = modelMapper.map(categoryDTO, CategoryEntity.class);
        if(categoryDTO.getCode() == null) {
            categoryEntity.setCode(this.generateCodeFromName(categoryDTO.getName()));
            categoryEntity.setRating(0.0);
            categoryEntity.setNumsOfRatings(0L);
        }
        return categoryEntity;
    }

    public String generateCodeFromName(String name) {
        return slugify.slugify(name);
    }
}
