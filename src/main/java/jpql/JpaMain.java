package jpql;


import jpabook.jpashop.domain.Team;

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
                mbr.setType(MemberType.ADMIN);
                JTeam jt = new JTeam();
                jt.setName("team"+i);
                em.persist(jt);
                mbr.changeTeam(jt);
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

            /**
             * Join
             */
            List<JMember> resultList6 = em.createQuery("select m from JMember m inner join m.team t", JMember.class)
                    .getResultList();
            System.out.println("resultList6.size() = " + resultList6.size());

            List<JMember> resultList7 = em.createQuery("select m from JMember m left join m.team t on t.name = :name", JMember.class)
                    .setParameter("name", "teamA")
                    .getResultList();
            System.out.println("resultList7.get(0).getUsername() = " + resultList7.get(0).getUsername());

            /**
             * JPQL 타입 표현
             */
            List<Object[]> resultList8 = em.createQuery("select m.username, 'HELLO', true from JMember m " +
                            "where m.type=jpql.MemberType.ADMIN").getResultList();
            //쿼리안에 하드코딩해야하면 패키지를 다 넣어야 한다 >> set Parameter을 쓰자
            //queryDSL은 자바코드를 쓰기때문에 package import가 가능하다.
            for (Object[] objects : resultList8) {
                System.out.println("object:"+ objects[0]+" "+objects[1]+" "+objects[2]);
            }

            em.flush();
            em.clear();
            String casequery ="select\n" +
                    "\tcase when m.age <= 10 then '학생요금'\n" +
                    "\t\t\t\twhen m.age >= 60 then '경로요금'\n" +
                    "\t\t\t\telse '일반요금'\n" +
                    "\tend\n" +
                    "from JMember m";
            List<String> resultList9 = em.createQuery(casequery, String.class).getResultList();
            for (String s : resultList9) {
                System.out.println("s = " + s);
            }

            /**
             * JPQL 기본함수
             */
            List<Integer> resultList10 = em.createQuery("select size(t.members) from JTeam t").getResultList();
            for (Integer integer : resultList10) {
                System.out.println("integer = " + integer);
            }

            /**
             * 사용자 정의 함수
             */
            List<String> resultList11 = em.createQuery("select function('group_concat', m.username) from JMember m", String.class).getResultList();
            for (String s : resultList11) {
                System.out.println("s = " + s);
            }

            /**
             * 경로표현식
             *    - 상태 필드
             *    - 단일 값 연관 경로 : 묵시적 내부 조인(inner join), 탐색(O)
             *    - 컬렉션 값 연관 경로 : 묵시적 내부 조인, 탐색(X), size만 확인 가능
             */
            List resultList12 = em.createQuery("select t.members from JTeam t").getResultList();
            //컬렉션 값 연관 경로는 size 제외하고 지원하지 않는다.
            for (Object o : resultList12) {
                System.out.println("o = " + o);
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