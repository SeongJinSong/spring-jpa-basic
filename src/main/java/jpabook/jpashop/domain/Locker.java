package jpabook.jpashop.domain;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
public class Locker {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(mappedBy = "locker", fetch = LAZY)
    private Member member;
}
