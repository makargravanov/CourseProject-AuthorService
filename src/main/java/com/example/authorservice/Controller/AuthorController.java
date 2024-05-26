package com.example.authorservice.Controller;


import com.example.authorservice.Data.Input;
import com.example.authorservice.Service.AuthorService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/author")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Async
    @PostMapping(value ="/createAuthor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<?>> createAuthor(@ModelAttribute Input input,
                                                             @RequestBody MultipartFile imageFile) {
        System.out.println("token: " + input.getToken());
        System.out.println("name: " + input.getName());
        if (!Objects.equals(imageFile.getContentType(), "image/jpeg")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only jpg files are allowed"));
        }
        return authorService.createAuthor(input.getToken(), input.getName(), imageFile);
    }

    @PostMapping("/getAuthorById")
    public ResponseEntity<?> getAuthorById(Long id) {
        try {
            System.out.println("id: "+id);
            return authorService.getAuthorById(id).get();
        } catch (InterruptedException | ExecutionException e) {
            return new ResponseEntity<>("Not Found", HttpStatusCode.valueOf(404));
        }
    }
    @PostMapping("/getAuthorsByUserId")
    public ResponseEntity<?> getAuthorsByUserId(Long id) {
        System.out.println("id: " + id);
        return authorService.getAuthorsByUserId(id);
    }
}
