package com.ptitb22dccn539.quiz.Service;

import com.ptitb22dccn539.quiz.Model.DTO.CategoryDTO;
import com.ptitb22dccn539.quiz.Model.Entity.CategoryEntity;
import com.ptitb22dccn539.quiz.Model.Request.Category.CategoryRating;
import com.ptitb22dccn539.quiz.Model.Response.CategoryResponse;
import org.springframework.data.web.PagedModel;

import java.util.List;

public interface ICategoryService {
    CategoryResponse save(CategoryDTO categoryDTO);
    void deleteByIds(List<String> ids);
    CategoryEntity getCategoryEntityByCode(String id);
    CategoryResponse getCategoryResponseByCode(String id);
    PagedModel<CategoryResponse> getAllCategory(Integer page);
    List<CategoryResponse> getAllNoPagination();
    CategoryResponse rating(CategoryRating categoryRating);
}
