package jpql;

import javax.persistence.*;

@Entity
@NamedQuery(name = "JMember.findByUsername", query="select m from JMember m where m.username = :username")
public class JMember {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private JTeam team;

    @Enumerated(EnumType.STRING)
    private MemberType type;

    public void changeTeam(JTeam team){
        this.team = team;
        team.getMembers().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public JTeam getTeam() {
        return team;
    }

    public void setTeam(JTeam team) {
        this.team = team;
    }

    public MemberType getType() {
        return type;
    }

    public void setType(MemberType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "JMember{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
//                ", team=" + team + 엔티티는 지우는게 낫다.
                '}';
    }
}
