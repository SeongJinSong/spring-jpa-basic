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

            /**
             * 프로젝션
             */
            //createQuery롤 호출한 결과는 영속성 컨텍스트에서 관리된다.
            em.flush();
            em.clear();
            List<JMember> resultList2 =
                    em.createQuery("select m from JMember m", JMember.class)
                            .getResultList();
            JMember findMember = resultList2.get(0);
            findMember.setAge(20); // update 쿼리가 나간다.

            /**
             * 엔티티 내 엔티티를 프로젝션하면 join쿼리가 나간다.
             * 이렇게 사용하는 건 추천하지 않고, 조인쿼리를 명시하는게 맞다.
             * SQL 방식으로 적어줘야 코드를 봤을때 예측이 가능하다.
             * 조인은 항상 명시적으로 하자!
             */
            //하지마라
            List<JTeam> resultList3 =
                    em.createQuery("select m.team from JMember m", JTeam.class)
                            .getResultList();
            //이렇게 하자
            List<JTeam> resultList4 =
                    em.createQuery("select t from JMember m join m.team t", JTeam.class)
                            .getResultList();

            /**
             * 임베디드 타입 프로젝션
             * from절에 임베디드 타입을 명시할 수 없는 한계가 있다.
             */
            em.createQuery("select o.address from JOrder o", Address.class)
                            .getResultList();

            /**
             * 스칼라 타입 프로젝션
                >> Object[] 로 리턴된다.
             */
            em.createQuery("select distinct m.username, m.age from JMember m")
                    .getResultList();

            /**
             * 프로젝션 new Dto로 조회
             */
            List<JMemberDto> resultList5 = em.createQuery("select new jpql.JMemberDto(m.username, m.age) from JMember m")
                    .getResultList();
            System.out.println("name = " + resultList5.get(0).getUsername());
            System.out.println("age = " + resultList5.get(0).getAge());

            /**
             * 페이징
             *  orderBy가 되어야 확실히 확인 가능하다.
             */
            for(int i=1;i<100;i++){
                JMember mbr = new JMember();
                mbr.setUsername("membber"+i);
                mbr.setAge(i);
                em.persist(mbr);
            }
            em.flush();
            em.clear();
            List<JMember> members = em.createQuery("select m from JMember m order by m.age desc", JMember.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();
            System.out.println("members.size() = " + members.size());
            for (JMember jMember : members) {
                System.out.println("jMember = " + jMember);
            }

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