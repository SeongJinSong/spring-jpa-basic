package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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
            findMember2.setTeam(newTeam);

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
