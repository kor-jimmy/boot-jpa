package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    // 값 타입은 변경 불가능하게 설계해야 한다.

    private String city;
    private String street;
    private String zipcode;

    // jpa 스펙상 만드는 생성자
    // jpa가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플렉션 같은 기술을 사용 할 수 있도록 지원해야하기 떄문
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
