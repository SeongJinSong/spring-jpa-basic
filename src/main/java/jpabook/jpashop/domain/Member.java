package jpabook.jpashop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.*;
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
    private Address homeAddress;

    @ElementCollection
    @CollectionTable(name="favorite_foods",
                    joinColumns = @JoinColumn(name = "member_id"))
    @Column(name="food_name") //컬럼명이 하나면 이렇게도 테이블이 만들어준다. 예외적으로 되는 것
    private Set<String> favoriteFoods = new HashSet<>();

    @OrderColumn(name = "address_history_order") // 컬렉션 순서값을 넣어서 할 수 있는데 오류가 많다.
    // 그냥 이것도 쓰지 말자!
//    @ElementCollection
//    @CollectionTable(name="adderss",
//            joinColumns = @JoinColumn(name = "member_id"))
//    private List<Address> addressHistory = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private List<AddressEntity> addressHistory = new ArrayList<>();

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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

//    public List<Address> getAddressHistory() {
//        return addressHistory;
//    }
//
//    public void setAddressHistory(List<Address> addressHistory) {
//        this.addressHistory = addressHistory;
//    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }
}
