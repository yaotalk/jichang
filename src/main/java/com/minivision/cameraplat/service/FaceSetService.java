package com.minivision.cameraplat.service;

import org.springframework.data.domain.Page;

import com.minivision.cameraplat.domain.FaceSet;
import com.minivision.cameraplat.mvc.ex.ServiceException;

import java.util.List;
import java.util.Set;

public interface FaceSetService {
    List<FaceSet> findAll();

    List<FaceSet> findByFaceplat();

    FaceSet update(FaceSet faceSet) throws ServiceException;

    FaceSet create(FaceSet faceSet);

    FaceSet find(String token);

    void delete (String token) throws ServiceException;

    Set<FaceSet> findAll(String ids);

    Page<FaceSet> findAll(int page,int size);

}
