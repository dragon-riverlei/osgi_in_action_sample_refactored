package com.packt.felix.bookshelf.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.packt.felix.bookshelf.service.api.BookshelfService;
import com.packt.felix.bookshelf.service.api.InvalidCredentialsException;
import com.packt.felix.bookshelf.inventory.api.Book;
import com.packt.felix.bookshelf.inventory.api.BookAlreadyExistsException;
import com.packt.felix.bookshelf.inventory.api.BookInventory;
import com.packt.felix.bookshelf.inventory.api.BookInventory.SearchCriteria;
import com.packt.felix.bookshelf.inventory.api.BookNotFoundException;
import com.packt.felix.bookshelf.inventory.api.InvalidBookException;
import com.packt.felix.bookshelf.inventory.api.MutableBook;

public class BookshelfServiceImpl implements BookshelfService
{
    private String sessionId;

    private BookInventory inventory;

    public BookshelfServiceImpl() {
    }

    public String login(String username, char[] password) throws InvalidCredentialsException {
        if ("admin".equals(username) && Arrays.equals(password, "admin".toCharArray())) {
            this.sessionId = Long.toString(System.currentTimeMillis());
            return this.sessionId;
        }
        throw new InvalidCredentialsException(username);
    }

    public void logout(String sessionId) {
        checkSession(sessionId);
        this.sessionId = null;
    }
    
    public boolean sessionIsValid(String sessionId) {
        return this.sessionId!=null && this.sessionId.equals(sessionId);
    }

    protected void checkSession(String sessionId) {
        if (!sessionIsValid(sessionId)) {
            throw new SessionNotValidRuntimeException(sessionId);
        }
    }

    private BookInventory lookupBookInventory() {
    	return this.inventory;
    }

    public Book getBook(String sessionId, String isbn) throws BookNotFoundException {
        checkSession(sessionId);
        BookInventory inventory = lookupBookInventory();
        return inventory.loadBook(isbn);
    }

    public MutableBook getBookForEdit(String sessionId, String isbn) throws BookNotFoundException {
        checkSession(sessionId);
        BookInventory inv = lookupBookInventory();
        return inv.loadBookForEdit(isbn);
    }

    public void addBook(String sessionId, String isbn, String title, String author, String category,
                    int rating) throws BookAlreadyExistsException, InvalidBookException {
        checkSession(sessionId);

        BookInventory inv = lookupBookInventory();

        MutableBook book = inv.createBook(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setRating(rating);

        inv.storeBook(book);
    }

    public void modifyBookCategory(String sessionId, String isbn, String category)
                    throws BookNotFoundException, InvalidBookException {
        checkSession(sessionId);

        BookInventory inv = lookupBookInventory();

        MutableBook book = inv.loadBookForEdit(isbn);
        book.setCategory(category);

        inv.storeBook(book);
    }

    public void modifyBookRating(String sessionId, String isbn, int rating)
                    throws BookNotFoundException, InvalidBookException {
        checkSession(sessionId);

        BookInventory inv = lookupBookInventory();

        MutableBook book = inv.loadBookForEdit(isbn);
        book.setRating(rating);

        inv.storeBook(book);
    }

    public Set<String> getCategories(String sessionId) {
        checkSession(sessionId);
        BookInventory inv = lookupBookInventory();
        return inv.getCategories();
    }

    public void removeBook(String sessionId, String isbn) throws BookNotFoundException {
        checkSession(sessionId);
        BookInventory inv = lookupBookInventory();
        inv.removeBook(isbn);
    }

    public Set<String> searchBooksByAuthor(String sessionId, String authorLike) {
        checkSession(sessionId);
        BookInventory inv = lookupBookInventory();
        Map<SearchCriteria, String> criteria = new HashMap<SearchCriteria, String>();
        criteria.put(SearchCriteria.AUTHOR_LIKE, authorLike);
        return inv.searchBooks(criteria);
    }

    public Set<String> searchBooksByCategory(String sessionId, String groupLike) {
        checkSession(sessionId);
        BookInventory inv = lookupBookInventory();
        Map<SearchCriteria, String> criteria = new HashMap<SearchCriteria, String>();
        criteria.put(SearchCriteria.CATEGORY_LIKE, groupLike);
        return inv.searchBooks(criteria);
    }

    public Set<String> searchBooksByTitle(String sessionId, String titleLike) {
        checkSession(sessionId);
        BookInventory inv = lookupBookInventory();
        Map<SearchCriteria, String> criteria = new HashMap<SearchCriteria, String>();
        criteria.put(SearchCriteria.TITLE_LIKE, titleLike);
        return inv.searchBooks(criteria);
    }

    public Set<String> searchBooksByRating(String sessionId, int gradeLower, int gradeUpper) {
        checkSession(sessionId);
        BookInventory inv = lookupBookInventory();
        Map<SearchCriteria, String> criteria = new HashMap<SearchCriteria, String>();
        criteria.put(SearchCriteria.RATING_LT, Integer.toString(gradeLower));
        criteria.put(SearchCriteria.RATING_GT, Integer.toString(gradeUpper));
        return inv.searchBooks(criteria);
    }

}
