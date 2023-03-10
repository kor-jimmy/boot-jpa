## 스프링 부트와 JPA 활용

- Spring MVC
- Spring ORM
- JPA, Hibernate
- Spring Data JPA
- H2
- thymeleaf
- slf4j

### 기능 목록

- 회원 기능
  - 회원 등록
  - 회원 조회
- 상품 기능
  - 상품 등록
  - 상품 수정
  - 상품 조회
- 주문 기능
  - 상품 주문
  - 주문 내역 조회
  - 주문 취소
- 기타 요구사항
  - 상품은 제고 관리가 필요하다.
  - 상품의 종류는 도서, 음반, 영화가 있다. 상품을 카테고리로 구분할 수 있다.
  - 상품 주문시 배송 정보를 입력할 수 있다.

### 중요 정리 항목

- 실무에서는 `@ManyToMany` 대다다 매핑을 사용하지 말아야 한다.

  - `@ManyToMany`는 편리한 것 같지만, 중간 테이블에 컬럼을 추가할 수 없고, 세밀하게 쿼리를 실행하기 어렵기 때문에 실무에서 사용하기에는 한계가 있다. 중간 엔티티를 만들고 `@ManyToOne`, `@OneToMany`로 매핑해서 사용하고, 다대다 매핑을 일대다, 다대일 매핑으로 풀어 사용해야 한다.

---

```java
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
```

- 값 타입은 변경 불가능하게 설계해야 한다.
  - `@Setter`를 사용하지 않고, 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스로 만든다. JPA 스펙상 엔티티나 임베디드타입(`@Embeddable`)은 자바 기본 생성자를 `public` 또는 `protected`로 설정해야 한다. `public`으로 두는 것 보다는 `protected`로 설정하는 것이 더 안전하다. JPA가 이런 제약을 두는 이유는 JPA구현 라이브러리 객체를 생성할 때 리플렉션 같은 기술을 사용 할 수 있도록 지원해야 하기 때문이다.

---

- 엔티티 설계시 주의점

  1. 엔티티에는 가급적 Setter를 사용하지 말자

     - Setter가 모두 열려있으면 변경 포인트가 너무 많아 유지보수가 어려워진다.

  2. 모든 연관관계는 지연로딩으로 설정

     - 즉시 로딩(`EAGER`)은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행 할 때 N+1 문제가 자주 발생한다.
     - 실무에서 모든 연관관계는 지연로딩(`LAZY`)으로 설정해야 한다.
     - 연관된 엔티티를 함께 DB에서 조회해야 하며, fetch join 또는 엔티티 그래프 기능을 사용한다.
     - `@XToOne(OneToOne, ManyToOne)` 관계는 기본이 즉시로딩이므로 직접 지연로딩으로 설정해야 한다.

  3. 컬렉션은 필드에서 초기화하자.

     - 컬렉션은 필드에서 바로 초기화 하는것이 안전
     - `null` 문제에서 안전
     - 하이버네이트는 엔티티를 영속화 할 때, 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한다. 만약 `getOrders()` 처럼 임의의 메서드에서 컬렉션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 발생할 수 있다. 따라서 필드레벨에서 새성하는 것이 가장 안전하고 코드도 간결하다.

---

-
