package com.LongerDude.LMS.repository;

import com.LongerDude.LMS.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Book entities.
 * This interface extends JpaRepository, providing standard CRUD (Create, Read, Update, Delete)
 * and pagination functionality for the {@link Book} model.
 * <p>
 * The type of the entity is {@link Book} and the type of the ID is {@link Long}.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * The framework (Spring Data JPA) automatically generates an implementation of this interface
     * at runtime. This mechanism is part of Spring's Inversion of Control (IoC) principle,
     * specifically implemented using dynamic proxies.
     *
     * You can add custom query methods here by simply defining the method signature
     * according to Spring Data's naming conventions, e.g., {@code findByTitle(String title)}.
     */
}