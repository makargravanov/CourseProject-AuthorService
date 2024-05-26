package com.example.authorservice.Service;

import com.example.authorservice.Entity.AuthorEntity;
import com.example.authorservice.Repository.AuthorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AuthorServiceDB {

    private final AuthorRepository authorRepository;

    public AuthorServiceDB(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }


    public ResponseEntity<?> findAuthorsByUserIdDB(Long id) {
        Optional<ArrayList<AuthorEntity>> opt = authorRepository.findAllByUserId(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ArrayList<AuthorEntity> result = opt.get();
        if (!result.isEmpty()) {
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
