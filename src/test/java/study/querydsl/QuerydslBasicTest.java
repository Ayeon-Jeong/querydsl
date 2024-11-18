package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.entity.*;

import static study.querydsl.entity.QMember.member;

@SpringBootTest(classes = QuerydslApplication.class)
@Transactional
class QuerydslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    public void setup() {
        jpaQueryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 11, teamA);
        Member member3 = new Member("member3", 12, teamB);
        Member member4 = new Member("member4", 13, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
//        member1 찾기
        Member findByJPQL = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(findByJPQL.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {

        QMember qMember = new QMember("m");

        Member member = jpaQueryFactory
                .select(qMember)
                .from(qMember)
                .where(qMember.username.eq("member1"))
                .fetchOne();

        Assertions.assertThat(member.getUsername()).isEqualTo("member1");


    }

    @Test
    public void startQuerydsl2() {
        Member member1 = jpaQueryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();

        assert member1 != null;
        Assertions.assertThat(member1.getUsername()).isEqualTo("member1");
    }
}
