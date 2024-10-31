package br.com.erudio.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.erudio.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	
	@Query("SELECT b FROM Book b WHERE b.title LIKE LOWER(CONCAT('%',:title,'%'))")
	Page<Book> findByTitle(@Param("title") String title, Pageable pageable);
}