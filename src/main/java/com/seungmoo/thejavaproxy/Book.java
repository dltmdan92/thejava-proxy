package com.seungmoo.thejavaproxy;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter @Setter
public class Book {
    @Id @GeneratedValue
    private Long id;

    private String title;

    // Book은 여러개의 note를 갖는다.
    // Many로 끝나는 것들은 기본적으로 Fetch 전략이 Lazy Initialization임.
    // Lazy Initialization : Dynamic Proxy로 구현되어 있다.
    @OneToMany
    private List<Note> notes;
}
