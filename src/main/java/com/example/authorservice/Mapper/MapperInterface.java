package com.example.authorservice.Mapper;

import com.example.authorservice.Data.AuthorData;
import com.example.authorservice.Entity.AuthorEntity;

import org.springframework.stereotype.Component;


import java.util.concurrent.CompletableFuture;

@Component
public interface MapperInterface {

    public default AuthorData entityToData(AuthorEntity entity) {
        return new AuthorData(entity.getId(), entity.getName());
    }
}
