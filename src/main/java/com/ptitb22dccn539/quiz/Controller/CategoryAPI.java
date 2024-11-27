package com.ptitb22dccn539.quiz.Controller;

import com.ptitb22dccn539.quiz.Model.DTO.CategoryDTO;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Model.Response.CategoryResponse;
import com.ptitb22dccn539.quiz.Service.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/categories")
public class CategoryAPI {
    private final ICategoryService categoryService;

    @PostMapping(value = "/")
    @ResponseStatus(value = HttpStatus.CREATED)
    public APIResponse createCategory(@Valid @RequestBody CategoryDTO category) {
        CategoryResponse categoryResponse = categoryService.save(category);
        return APIResponse.builder()
                .message("CREATE SUCCESS")
                .response(categoryResponse)
                .build();
    }

    @PutMapping(value = "/")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse updateCategory(@Valid @RequestBody CategoryDTO category) {
        CategoryResponse categoryResponse = categoryService.save(category);
        return APIResponse.builder()
                .message("UPDATE SUCCESS")
                .response(categoryResponse)
                .build();
    }

    @DeleteMapping(value = "/{ids}")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse deleteCategory(@PathVariable(value = "ids") List<String> ids) {
        categoryService.deleteByIds(ids);
        return APIResponse.builder()
                .message("DELETE SUCCESS")
                .build();
    }

    @GetMapping(value = "/")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse getAllCategory(@RequestParam(required = false) Integer page) {
        PagedModel<CategoryResponse> list = categoryService.getAllCategory(page);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(list)
                .build();
    }

    @GetMapping(value = "/all")
    public APIResponse getAllCategoryNoPagination() {
        List<CategoryResponse> responses = categoryService.getAllNoPagination();
        return APIResponse.builder()
                .message("SUCCESS")
                .response(responses)
                .build();
    }
}
