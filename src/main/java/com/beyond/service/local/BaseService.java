package com.beyond.service.local;

import java.util.List;

public interface BaseService<T> {
    List<T> findAll();
    T findById(String id);
    void add(T t);
    void delete(T t);
    void deleteById(String id);
    void update(T t);

    void changeWorkSpace(String xmlPath);
}
