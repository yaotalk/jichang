package com.minivision.cameraplat.service;

import java.util.List;

import com.minivision.cameraplat.domain.Scheme;
import com.minivision.cameraplat.domain.Strategy;

public interface StrategyService {
    List<Strategy> findAll();

    Strategy create(Strategy strategy);

    Strategy update(Strategy strategy);

    void delete(Strategy strategy);

    List<Strategy> findByScheme(Scheme scheme);
}
