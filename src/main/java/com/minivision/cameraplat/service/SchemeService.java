package com.minivision.cameraplat.service;

import java.util.List;

import com.minivision.cameraplat.domain.Scheme;

public interface SchemeService {
    List<Scheme> findAll();

    Scheme findOne(long id);

    Scheme create(Scheme scheme);

    Scheme update(Scheme scheme);

    void delete(long id);
}
