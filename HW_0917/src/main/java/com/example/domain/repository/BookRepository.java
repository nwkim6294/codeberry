package com.example.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.domain.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    Page<Book> findByTitleContaining(String keyword, Pageable pageable);

    Page<Book> findByAuthorContaining(String keyword, Pageable pageable);
	
	@Query(value = """
            SELECT DISTINCT b.* FROM book b
            JOIN review r1 ON r1.book_id = b.id
            JOIN member m1 ON r1.member_id = m1.id
            JOIN review r2 ON r2.book_id = b.id
            JOIN member m2 ON r2.member_id = m2.id
            WHERE m1.faction <> m2.faction
            """, nativeQuery = true)
	List<Book> findControversialBooks();
}
