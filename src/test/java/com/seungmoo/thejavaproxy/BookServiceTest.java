package com.seungmoo.thejavaproxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    /**
     * CGlib, ByteBuddy 모두 상속을 통한  subClass를 만드는 방법으로 프록시 객체를 생성한다.
     * final class 는 상속을 못받기 때문에 불가하다.
     * Default Constructor를 private 하게 정의했을 경우 또한 불가한다. (하위 클래스는 부모 클래스의 생성자를 호출)
     * --> 이런 이유로 웬만하면 Interface로 Proxy를 생성하는게 좋다.
     */

    @Test
    @DisplayName("CGlib을 이용한 Class 기반의 Proxy 만들기")
    void classProxy() {
        MethodInterceptor handler = new MethodInterceptor() {
            DefaultBookService bookService = new DefaultBookService();
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                if (method.getName().equals("rent")) {
                    System.out.println("aaaa");
                    Object invoke =  method.invoke(bookService, args);
                    System.out.println("bbbb");
                    return invoke;
                }
                return method.invoke(bookService, args);
            }
        };

        // Enhancer : CGlib의 핵심적인 Class
        DefaultBookService bookService = (DefaultBookService) Enhancer.create(DefaultBookService.class, handler);

        Book book = new Book();
        book.setTitle("spring");
        bookService.rent(book);
        bookService.returnBook(book);
    }

    @Test
    @DisplayName("ByteBuddy를 이용한 Class 기반의 Proxy 만들기")
    void classProxy2() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Proxy Class 정의
        Class<? extends DefaultBookService> proxyClass = new ByteBuddy().subclass(DefaultBookService.class)
                .method(named("rent")).intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("aaaa");
                        Object invoke = method.invoke(bookService, args);
                        System.out.println("bbbb");
                        return invoke;
                    }
                }))
                .make().load(DefaultBookService.class.getClassLoader()).getLoaded();
        // 객체 인스턴스 생성
        DefaultBookService bookService = proxyClass.getConstructor(null).newInstance();

        Book book = new Book();
        book.setTitle("spring");
        bookService.rent(book);
        bookService.returnBook(book);
    }

    /**
     * Mockito 는 Dynamic Proxy 방식으로 구현되어 있다.
     */
    @Test
    void mockitoTest() {
        // mockito를 이용해서 해당 interface 타입의 가짜 객체를 만들 수 있다.
        BookRepository bookRepositoryMock = mock(BookRepository.class);
        Book hibernateBook = new Book();
        hibernateBook.setTitle("Hibernate");
        when(bookRepositoryMock.save(any())).thenReturn(hibernateBook);

        DefaultBookService bookService = new DefaultBookService(bookRepositoryMock);

        Book book = new Book();
        book.setTitle("spring");
        //위에서 save메소드에 대해 rent 시 Hibernate를 return 하도록 mocking 했음
        bookService.rent(book);
        bookService.returnBook(book);
    }

}