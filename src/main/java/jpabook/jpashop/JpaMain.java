package jpabook.jpashop;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.AddressEntity;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Set;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        //code
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            /**
             * 테이블 형식으로 객체 연관관계를 설정하면 발생하는 문제
             */
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setName("member1");
//            member.setTeamId(team.getId()); //객체지향스럽지 않다.
            em.persist(member);

            //멤버가 어느 팀 소속인지 찾을때도 문제가 된다.
            Member findMember = em.find(Member.class, member.getId());
//            Long findTeamId = findMember.getTeamId();
//            Team findTeam = em.find(Team.class, findTeamId);

            /**
             * 객체지향 형식으로 매핑
             */
            Team team2 = new Team();
            team2.setName("TeamB");
            em.persist(team2);

            Member member2 = new Member();
            member2.setName("member2");
            member2.setTeam(team); //객체지향스럽다.
            em.persist(member2);

            Member findMember2 = em.find(Member.class, member2.getId());
            Team findTeam2 = findMember2.getTeam();
            System.out.println("findTeam2.getName() = " + findTeam2.getName());

            /**
             * 수정
             */
            Team newTeam = em.find(Team.class, 1L);
            //findMember2.setTeam(newTeam);

            /**
             * 양방향 연관관계
             */
            /*flush와 clear를 하지 않고 findMember2를 쓰면 안되는 이유는 뭘까?
              flush랑clear를 안하면 위에서 설정한 members 컬렉션이 빈상태로 영속성 컨텍스트에 반영되고,
              그것을 바로 사용했기 때문에 members 가 비어있던거였네요.
            */
//            em.flush(); //db에 값을 세팅하고
//            em.clear(); //1차캐시에 비운다.

            /**
             * 위에 flush와 clear를 하지 않아야 하고
             * JPA없이 테스트를 작성해도 돌아야하고
             * 객체지향적 관점에서 맞도록 하기 위해서는
             *
             * 양방향 메서드를 작성해야한다.
             * findTeam2.getMembers().add(member);
             *
             * 그런데 양쪽으로 추가하는건 번거롭고 실수하기 쉽다
             * 연관관계 편의 메서드를 작성하자
             *     >> changeTeam(team) 함수 안에 add(member) 넣기
             *     >> 연관관계 편의메서드를 한쪽에만 만들자
             *       : 무한 루프가 걸리거나 복잡해질 수 있다.
             */

            Member findMember3 = em.find(Member.class, member2.getId());
            // 사용하는 시점에 쿼리를 날려 연관된 내용을 가져온다.
            List<Member> members = findMember3.getTeam().getMembers();
            for (Member member1 : members) {
                System.out.println("member1.getName() = " + member1.getName());
            }

            /**
             * 값 타입 컬렉션
                - 값타입과 같기 때문에 라이프사이클이 멤버에 소속된다!
             */
            Member member3 = new Member();

            //저장 예제
            member3.setName("member1");
            member3.setHomeAddress(new Address("homeCity", "street", "10000"));

            member3.getFavoriteFoods().add("치킨");
            member3.getFavoriteFoods().add("족발");
            member3.getFavoriteFoods().add("피자");

            member3.getAddressHistory().add(new AddressEntity("old1", "street", "20000"));
            member3.getAddressHistory().add(new AddressEntity("old2", "street", "20000"));

            em.persist(member3);

            //조회 예제
            em.flush();
            em.clear();
            System.out.println("=========== START ================");
            Member findMember4 = em.find(Member.class, member3.getId());
            // 컬렉션들은 지연로딩

            List<AddressEntity> addressHistory = findMember4.getAddressHistory();
            for (AddressEntity address : addressHistory) {
                System.out.println("address = " + address.getAddress().getCity());
            }
            System.out.println("====================================");
            Set<String> favoriteFoods = findMember4.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood);
            }

            /** 수정
                //homeCity-> newCity
                findMember4.getHomeAddress().setCity("newCity"); 이렇게 하면 절대 안된다.
             */
            findMember4.setHomeAddress(new Address("newCity", findMember4.getHomeAddress().getStreet(), findMember4.getHomeAddress().getZipcode()));

            //치킨 -> 한식
//            findMember4.getFavoriteFoods().remove("치킨");
//            findMember4.getFavoriteFoods().add("한식");

            /**
             * 이부분만 실행하면 member에 해당하는 것을 전부 지운후 두개를 insert한다.
             * 실무에서 헷갈릴 여지가 많다.
             */
//            findMember4.getAddressHistory().remove(new Address("old1", "street", "20000"));
//            findMember4.getAddressHistory().add(new Address("newCity1", "street", "10000"));
            findMember4.getAddressHistory().remove(new AddressEntity("old1", "street", "20000"));
            findMember4.getAddressHistory().add(new AddressEntity("newCity1", "street", "10000"));
            tx.commit();
        }catch(Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
