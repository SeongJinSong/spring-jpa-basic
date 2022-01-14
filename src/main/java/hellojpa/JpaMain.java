package hellojpa;

import javax.persistence.*;
import java.util.List;

public class JpaMain{
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        //code
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            //저장
            /**
             * 비영속 상태
             */
//            Member member = new Member();
//            member.setId(2L);
//            member.setName("Hello2");
            /**
             * 영속 상태
             * DB에 저장되는 상태가 아니다!
             */
            System.out.println("===BEFORE===");
//            em.persist(member); // 저장
//            em.detach(member); // 삭제
            System.out.println("===AFTER===");
            //쿼리는 이 이후에 TX.COMMIT() 시점에 나간다!
            
            //조회
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.getId() = " + findMember.getId());
//            System.out.println("findMember.getName() = " + findMember.getName());

            //수정
//            Member findMember2 = em.find(Member.class, 1L);
//            findMember2.setName("HelloB");

            //JPQL 조회
            List<Member> memberList = em.createQuery("select m from Member m", Member.class).getResultList();
            System.out.println("memberList = " + memberList.size());

            /*
                JPQL이 가지는 메리트
                1. 페이징 처리가 쉽다.
                    em.createQuery("select m from Member m", Member.class).getResultList()
                        .setFirstResult(5)
                        .setMaxResult(8)
                        .getResultList();
                2. 검색을 할때도 엔티티 객체를 대상으로 검색
                3. 결국에는 검색 조건이 포함된 SQL이 필요
             */
            tx.commit();
        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}