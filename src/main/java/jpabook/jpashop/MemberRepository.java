package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    // spring boot는 아래 어노테이션으로 엔티티매니저를 주입해준다.
    @PersistenceContext
    private EntityManager em;

    // 커멘드와 쿼리는 분리하는게 좋다. 저장을 해도 아이디만 리턴한다.
    public Long save(Member member){
        this.em.persist(member);
        return member.getId();
    }

    public Member find(Long id){
        return this.em.find(Member.class, id);
    }
}
