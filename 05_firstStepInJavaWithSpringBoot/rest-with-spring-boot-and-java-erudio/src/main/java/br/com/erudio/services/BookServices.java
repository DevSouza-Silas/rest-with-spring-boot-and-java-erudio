package br.com.erudio.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;

@Service
public class BookServices {
	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	BookRepository repository;
	
	@Autowired
	PagedResourcesAssembler<BookVO> assembler;

	//********* MÉTODO COM PAGINAÇÃO *********
	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {

		logger.info("Finding all book!");

		var booksPage = repository.findAll(pageable);
		
		var booksVosPage = booksPage.map(b -> DozerMapper.parseObject(b, BookVO.class));
	
		booksVosPage.map(
				b -> b.add(
						linkTo(methodOn(BookController.class)
								.findById(b.getKey())).withSelfRel()));
		
		Link link = linkTo(
				methodOn(BookController.class)
					.findAll(pageable.getPageNumber(), 
						pageable.getPageSize(), 
						"asc")).withSelfRel(); 
		
		return assembler.toModel(booksVosPage, link);
	}
	
	public PagedModel<EntityModel<BookVO>> findByTitle(String title, Pageable pageable) {

		logger.info("Find by name book!");

		var booksPage = repository.findByTitle(title, pageable);
		
		var booksVosPage = booksPage.map(b -> DozerMapper.parseObject(b, BookVO.class));
	
		booksVosPage.map(
				b -> b.add(
						linkTo(methodOn(BookController.class)
								.findById(b.getKey())).withSelfRel()));
		
		Link link = linkTo(
				methodOn(BookController.class)
					.findAll(pageable.getPageNumber(), 
						pageable.getPageSize(), 
						"asc")).withSelfRel(); 
		
		return assembler.toModel(booksVosPage, link);
	}
	/* ******** MÉTODO SEM PAGINAÇÃO *********
	public List<BookVO> findAll() {

		logger.info("Finding all book!");

		var books = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
		books
			.stream()
			.forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		return books;
	}*/

	public BookVO findById(Long id) {
		
		logger.info("Finding one book!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO book) {

		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		var entity = DozerMapper.parseObject(book, Book.class);
		var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) {

		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");
		
		var entity = repository.findById(book.getKey())
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}
