package com.seungmoo.thejavaproxy;

import org.junit.jupiter.api.Test;

class BookServiceTest {

    // 클라이언트가 Real Subject인 DefaultBookService를 직접 호출하지 않고
    // BookServiceProxy를 직접 호출한다. (real Subject는 간접 호출)
    BookService bookService = new BookServiceProxy(new DefaultBookService());

    @Test
    void di() {
        Book book = new Book();
        book.setTitle("spring");
        bookService.rent(book);
    }
}