package jpabook.jpashop.domain;

import javax.persistence.*;

@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    @Column(name = "user_name")
    private String name;
//    @Column(name = "team_id")
//    private Long teamId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String city;
    private String street;
    private String zipcode;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

//    public Long getTeamId() {
//        return teamId;
//    }
//
//    public void setTeamId(Long teamId) {
//        this.teamId = teamId;
//    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
