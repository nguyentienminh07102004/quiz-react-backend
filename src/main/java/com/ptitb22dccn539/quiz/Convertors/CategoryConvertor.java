package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Model.DTO.CategoryDTO;
import com.ptitb22dccn539.quiz.Model.Entity.CategoryEntity;
import com.ptitb22dccn539.quiz.Model.Response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CategoryConvertor implements IConvertor<CategoryDTO, CategoryEntity, CategoryResponse> {
    private final ModelMapper modelMapper;

    @Override
    public CategoryResponse entityToResponse(CategoryEntity category) {
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public CategoryEntity dtoToEntity(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = modelMapper.map(categoryDTO, CategoryEntity.class);
        categoryEntity.setCode(this.generateCodeFromName(categoryDTO.getName()));
        categoryEntity.setRating(0.0);
        categoryEntity.setNumsOfRatings(0L);
        return categoryEntity;
    }

    public String generateCodeFromName(String name) {
        String temp = Normalizer.normalize(name , Normalizer.Form.NFD);
        // find all diacritics in string
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String[] str = pattern.matcher(temp).replaceAll("").split("\\s+");
        List<String> list = new ArrayList<>(Arrays.asList(str));
        list.add(UUID.randomUUID().toString());
        return String.join("_", list);
    }
}
