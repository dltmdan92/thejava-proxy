package com.seungmoo.thejavaproxy;

/**
 * 프록시 패턴를 구현한 객체를 만들어보자
 * but 프록시 패턴 코드는 복잡하고 번거롭다...
 *      매번 프록시 패턴을 만들고 추가할 때 마다, 중복되는 작업이 들어가게 된다.
 */
public class BookServiceProxy implements BookService {

    /**
     * 프록시 객체 또한 Real Subject를 갖고 있어야 한다.
     * Real Subject 주입
     */
    BookService bookService;

    public BookServiceProxy(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * real subject의 로직은 감싼 프록시 메소드
     * @param book
     */
    @Override
    public void rent(Book book) {
        System.out.println("aaaaa");
        bookService.rent(book);
        System.out.println("bbbbb");
    }

    /**
     * 프록시 패턴으로 구현할 때,
     * 비슷한 코드가 늘어난다는 문제점이 존재한다!!! --> 이러한 문제점을 해결하기 위해 동적인 솔루션이 필요 하다.
     * Dynamic Proxy를 통해서 이러한 이슈를 해결해야 한다.
     * @param book
     */
    @Override
    public void returnBook(Book book) {
        System.out.println("aaaaa");
        bookService.returnBook(book);
        System.out.println("bbbbb");
    }
}
