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

- Entity 생성자
  - JPA는 기본 스펙으로 proceted 생성 까지 가능하다
  - 유지보수성을 위해서라도 다양하게 생성자를 만드는것을 막아 놓는다.
  - 항상 코드를 제약하는 스타일로 작성하여 유지보수가 편하게 만든다.

```java
  @Entity
  @Getter
  @Setter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public class OrderItem {
```

---

- SQL 위주의 개발과 JPA
  - 일반적으로 데이터베이스의 SQL을 직접 다루는 mybatis, jdbc template 등은 데이터의 수정이 있을 경우 수정 쿼리를 실행 한 이후에 연관된 엔티티의 데이터를 모두 변경해줘야 한다.
  - JPA를 활용할 경우 엔티티 안에 있는 데이터만 변경 할경우 JPA dirty checking이 일어나면서 변경된 내용이 감지되어 업데이트 쿼리가 자동으로 수행된다.

---

- 엔티티 속 비즈니스 로직
  - 엔티티 속에 대부분의 비즈니스 로직이 있다. 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할을 하고, 이처럼 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을 도메인 모델 패턴 이라고 한다.
    - http://martinfowler.com/eaaCatalog/domainModel.html
  - 반대로 엔티티에는 비즈니스로직이 거의 없고 서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것을 트랜잭션 스크립트 패턴이라고 한다.
    - http://martinfowler.com/eaaCatalog/transactionScript.html

---

**변경 감지와 병합(merge)**

- 준영속성 엔티티란?
  - 영속성 컨텍스트가 관리하지 않는 엔티티
- 준영속성 엔티티를 수정하는 2가지 방법
  - 변경 감지 기능 사용
  - 병합(merge) 사용
- 변경 감지 기능 사용
  - 영속성 컨텍스트에서 엔티티를 다시 조회한 후에 데이터를 수정하는 방법
  - 트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택 => 트랜잭션 커밋 시점에 변경 검지(Dirty Checking)
  - 위 동작을 통해 데이터베이스에 UPDATE SQL 요청

```java
@Transactional
//itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
void update(Item itemParam) {
  //같은 엔티티를 조회한다.
  Item findItem = em.find(Item.class, itemParam.getId());
  //데이터를 수정한다.
  findItem.setPrice(itemParam.getPrice());
}
```

- 병합 사용
  - 병합은 준영속 상태의 엔티티를 영속상태로 변경할 때 사용하는 기능이다.

```java
@Transactional
//itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
void update(Item itemParam) {
  Item mergeItem = em.merge(item);
}
```

- 병합 동작 방식

  - 준영속 엔티티의 식별자 값으로 영속 엔티티를 조회한다.
  - 영속 엔티티의 값을 준영속 엔티티의 값으로 모두 교체한다.(병합한다.)
  - 트랜잭션 커밋 시점에 변경 감지 기능이 동작해서 데이터베이스에 UPDATE SQL이 실행
  - **주의** : 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경된다. 병합시 값이 없으면 null 로 업데이트 할 위험도 있다. (병합은 모든 필드를 교체한다.)

- **엔티티를 변경할 때는 항상 변경 감지 사용**
  - 컨트롤러에서 엔티티 생성 금지
  - 트랜잭션이 있는 서비스 계층에 식별자와 변경할 데이터를 명확하게 전달(파라미터 or dto)
  - 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경
  - 트랜잭션 커밋 시점에 변경 감지가 실행

---

- 쿼리 방식 선택 권장 순서
  1. 우선 엔티티를 DTO로 변환하는 방법
  2. 필요하면 페치 조인으로 성능 최적화 -> 성능 이슈 해결
  3. 그래도 안되면 DTO로 직접 조회하는 방법(필요한 필드들만)
  4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용
