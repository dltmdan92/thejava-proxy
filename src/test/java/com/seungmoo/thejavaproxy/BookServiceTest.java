package com.seungmoo.thejavaproxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class BookServiceTest {

    // 클라이언트가 Real Subject인 DefaultBookService를 직접 호출하지 않고
    // BookServiceProxy를 직접 호출한다. (real Subject는 간접 호출)
    //BookService bookService = new BookServiceProxy(new DefaultBookService());

    // Java Proxy를 통해 Dynamic Proxy를 구현하기 (+ 리플렉션도 있음)
    // BookServiceProxy라는 별도의 Proxy 객체는 필요 없다.
    // But 이것도 많이 복잡하다.. invoke에 계속 로직이 추가될 것임...
    // 그래서 Spring AOP가 등장했다!!  SpringAOP는 Proxy 기반의 AOP로서 좀 더 편리하게 Proxy를 제공한다.

    // Class 기반의 Dynamic Proxy는 만들 수 없다.. Interface 기반으로 만들어야 한다.
    // 아래 소스에서 BookService(인터페이스)로 된 부분을 DefaultBookService(클래스)로 바꿔쓰지 못함.
    // Class 기반으로 다른 방식의 Proxy를 만드는 방법은 있음
    BookService bookService = (BookService) Proxy.newProxyInstance(
            BookService.class.getClassLoader(),
            new Class[]{BookService.class},
            new InvocationHandler() {
                // Real Subject
                // 이제는 BookServiceProxy 객체가 필요없다.
                BookService bookService = new DefaultBookService();

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("rent")) {
                        System.out.println("aaaa");
                        Object invoke =  method.invoke(bookService, args); // 원래 하던 일만 하는 메소드
                        System.out.println("bbbb");
                        return invoke;
                    }

                    // 원래 하던 일만 하는 메소드
                    return method.invoke(bookService, args);
                }
            });

    @Test
    void di() {
        Book book = new Book();
        book.setTitle("spring");
        bookService.rent(book);
    }
}