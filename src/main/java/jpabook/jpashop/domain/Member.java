package jpabook.jpashop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Member { // extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    @Column(name = "user_name")
    private String name;
//    @Column(name = "team_id")
//    private Long teamId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>(); //관례상 이게 더 좋다.

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="locker_id")
    private Locker locker;

    @ManyToMany
    @JoinTable(name="member_product")
    private List<Product> products = new ArrayList<>();

    /**
     * 연관관계 편의 메서드로 만들자
     *   > setter에 쓰기엔 관례상 오해의 소지가 있다.
     */
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

    //기간
    @Embedded
    private Period workPeriod;

    //주소
    @Embedded
    private Address address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
