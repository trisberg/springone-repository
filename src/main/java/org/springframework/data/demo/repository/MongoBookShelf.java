package org.springframework.data.demo.repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.demo.domain.Author;
import org.springframework.data.demo.domain.Book;
import org.springframework.stereotype.Repository;

@Repository
public class MongoBookShelf implements BookShelf {

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	BookRepository bookRepository;

	@Override
	public void add(Book book) {
		save(book);
	}
	
	@Override
	public void save(Book book) {
		lookUpAuthor(book);
		bookRepository.save(book);
	}
	
	@Override
	public Book find(String isbn) {
		return bookRepository.findOne(isbn);
	}
	
	@Override
	public void remove(String isbn) {
		bookRepository.delete(isbn);
	}
	
	@Override
	public List<Book> findAll() {
		 return bookRepository.findAll();
	}

	@Override
	public List<Book> findByCategoriesOrYear(Set<String> categories, String year) {
		String[] categoriesToMatch;
		if (categories == null) {
			categoriesToMatch = new String[] {};
		}
		else {
			categoriesToMatch = categories.toArray(new String[categories.size()]);
		}
		Date startDate = null;
		if (year != null && year.length() == 4) {
			DateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
			try {
				startDate = formatter.parse(year + "-01-01");
			} catch (ParseException e) {}
		}
		
		if (startDate != null) {
			if (categoriesToMatch.length > 0) {
				return bookRepository.findByPublishedGreaterThanAndCategoriesIn(startDate, categoriesToMatch);
			}
			else {
				return bookRepository.findByPublishedGreaterThan(startDate);
			}
		}
		else {
			if (categoriesToMatch.length > 0) {
				return bookRepository.findByCategoriesIn(categoriesToMatch);
			}
			else {
				return findAll();
			}
		}
	}

	private void lookUpAuthor(Book book) {
		Author existing = authorRepository.findByName(book.getAuthor().getName());
		if (existing != null) {
			book.setAuthor(existing);
		}
		else {
			authorRepository.save(book.getAuthor());
		}
	}

}
