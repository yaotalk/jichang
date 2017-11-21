package com.minivision.cameraplat.service;

import com.minivision.cameraplat.domain.Scheme;

public interface PeriodService {
    void delete(Long period);
    void save(Scheme.Period period);
}
