package jpql;


import javax.persistence.*;
import java.util.List;

public class JpaMain{
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try{
            JMember member = new JMember();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

//            Query query = em.createQuery("select m.username, m.age from JMember m");
            TypedQuery<JMember> query = em.createQuery("select m from JMember m", JMember.class);
            //결과가 없으면 빈 리스트 반환
            List<JMember> resultList = query.getResultList();
            // 없거나 둘이상이면 Exception 반환
            JMember singleResult = query.getSingleResult();

            /**
             * 파라미터 바인딩
             */
            TypedQuery<JMember> query1 = em.createQuery("select m from JMember m where m.username = :username", JMember.class);
            query1.setParameter("username", "member1");
            JMember singleResult2 = query1.getSingleResult();
            System.out.println("singleResult = " + singleResult2.getUsername());

            /**
             * chaining 기반으로 처리 가능
             */
            List<JMember> resultList1 =
                    em.createQuery("select m from JMember m where m.username = :username", JMember.class)
                                    .setParameter("username", "member1").getResultList();

            tx.commit();
        }catch(Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }
        emf.close();
    }
}