package com.example.authorservice.Repository;

import com.example.authorservice.Entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    Optional<ArrayList<AuthorEntity>> findAllByUserId(Long userId);

}
