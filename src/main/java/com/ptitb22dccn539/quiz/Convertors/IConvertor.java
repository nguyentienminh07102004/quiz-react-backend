package com.ptitb22dccn539.quiz.Convertors;

public interface IConvertor<D, E, R> {
    E dtoToEntity(D dto);
    R entityToResponse(E entity);
}
