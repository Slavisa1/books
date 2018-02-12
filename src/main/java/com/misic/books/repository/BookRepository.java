package com.misic.books.repository;

import com.misic.books.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    @Query("SELECT t FROM Book t WHERE " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND t.printYear > :printYear")
    Page<Book> findBySearchParams(
            @Param("searchTerm") String searchTerm,
            @Param("printYear") int print_year,
            Pageable pageRequest
    );

    @Query("SELECT t FROM Book t WHERE " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND t.printYear > :printYear AND t.readAlready = :readAlReady")
    Page<Book> findBySearchParamsAndReadAlready(
            @Param("searchTerm") String searchTerm,
            @Param("printYear") int print_year,
            @Param("readAlReady") boolean read_already,
            Pageable pageRequest
    );
}
