package com.hit.dao;

public interface IDao<Long, T> {
    void save(T entity);
    void delete(T entity);
    T search(long id);
}
