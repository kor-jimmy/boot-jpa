package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    // JPA 내장타입
    @Embedded
    private Address address;

    // order 테이블에 있는 member에 의해 맵핑
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();


}
