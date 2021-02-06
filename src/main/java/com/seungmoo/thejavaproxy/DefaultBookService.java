package com.seungmoo.thejavaproxy;

public class DefaultBookService implements BookService {

    private BookRepository bookRepository;

    public DefaultBookService() {
    }

    public DefaultBookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void rent(Book book) {
        Book save = bookRepository.save(book);
        System.out.println("rent: " + save.getTitle());
    }

    @Override
    public void returnBook(Book book) {
        System.out.println("return: " + book.getTitle());
    }
}
