package com.example.authorservice.Service;

import com.example.authorservice.Data.AuthorData;
import com.example.authorservice.Entity.AuthorEntity;
import com.example.authorservice.Mapper.Mapper;
import com.example.authorservice.Repository.AuthorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final DefaultTokenService tokenService;
    private final AuthorServiceDB authorServiceDB;
    private final Mapper mapper;

    public AuthorService(AuthorRepository authorRepository, DefaultTokenService tokenService, AuthorServiceDB authorServiceDB, Mapper mapper) {
        this.authorRepository = authorRepository;
        this.tokenService = tokenService;
        this.authorServiceDB = authorServiceDB;
        this.mapper = mapper;
    }
    @Async
    public CompletableFuture<ResponseEntity<?>> getAuthorById(Long id) {
        Optional<AuthorEntity> author = authorRepository.findById(id);
        if (author.isEmpty()) {
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }else{
            AuthorData authorData = mapper.entityToData(author.get());
            return CompletableFuture.completedFuture(ResponseEntity.ok().body(authorData));
        }
    }
    //TODO - findAuthorByName

    private CompletableFuture<String> saveFile(MultipartFile file, UUID uuid, String name) {
        String destPath;
        File filePath;
        try {
            if (Objects.equals(file.getContentType(), "image/jpeg")) {
                destPath = "src/main/resources/static/Images/CoverImages";
                filePath = new File(destPath);
                name = name + ".jpg";
            } else {
                throw new IllegalArgumentException("Incorrect file type");
            }
            File dest = new File(filePath.getAbsolutePath(), uuid.toString() + "-" + name);
            file.transferTo(dest);
            return CompletableFuture.completedFuture(destPath + "/" + uuid.toString() + "-" + name);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Incorrect file type: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("I/O error: " + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<ResponseEntity<?>> createAuthor(String token, String name, MultipartFile imageFile) {
        try {
            Boolean isTokenValid = tokenService.checkToken(token);

            if (!isTokenValid) {
                System.out.println("token is not valid");
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Token is invalid"));
            }
            System.out.println("token is valid");
            BufferedImage img = ImageIO.read(imageFile.getInputStream());
            int width = img.getWidth();
            int height = img.getHeight();

            if (width != height) {
                System.out.println("Not square image");
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only square images are allowed"));
            }
            System.out.println("image ok");
            UUID uuid = UUID.randomUUID();
            String pathToImage = saveFile(imageFile, uuid, name).get();
            AuthorEntity author = new AuthorEntity();
            author.setName(name);
            author.setPathToImage(pathToImage);
            author.setUserId(tokenService.getIdFromToken(token));
            System.out.println("we are here");
            authorRepository.save(author);

            return CompletableFuture.completedFuture(ResponseEntity.ok().body("Success"));
        } catch (ExecutionException e) {
            System.out.println("EE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (RuntimeException e) {
            System.out.println("RE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (IOException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Error reading image file"));
        }
    }

    public ResponseEntity<?> getAuthorsByUserId(Long id) {
        /*Boolean isTokenValid = tokenService.checkToken(token);
        if(!isTokenValid){
            return ResponseEntity.badRequest().body("Token is invalid");
        }*/
        return authorServiceDB.findAuthorsByUserIdDB(id);
    }
}
