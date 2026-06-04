# 섹션 13: 스프링 AOP - 실무 주의사항

---

## 프록시와 내부 호출 문제

**문제**: 대상 객체 내부에서 메서드를 호출하면 `this.internal()`이 되어 프록시를 거치지 않음 → AOP 미적용

> **왜?** AOP는 프록시를 통해 호출될 때만 작동함. 같은 객체 안에서 `this.메서드()`를 호출하면 프록시를 안 거치고 바로 실행됨.

```java
@Component
public class CallService {
    public void external() {
        internal(); // this.internal() → 프록시 미경유, AOP 미적용!
    }

    public void internal() { ... } // AOP 적용 안 됨!
}
```

> **비유**: 매니저(프록시)를 통해 연예인에게 연락하면 기록이 남지만, 연예인이 자기 안에서 혼잣말하는 건 매니저가 알 수 없음.

### 해결 방법

| 방법 | 설명 |
|------|------|
| 자기 자신 주입 | setter로 자신(프록시)을 주입받아 호출 (순환 참조 주의) |
| 지연 조회 | `ObjectProvider`로 사용 시점에 빈 조회 |
| **구조 변경 (권장)** | **내부 호출이 발생하지 않도록 별도 클래스로 분리** |

**가장 깔끔한 해결**: 내부 호출 자체를 없애는 것.

```java
// Before: 한 클래스에 external + internal
// After: InternalService로 분리
@Component
public class CallService {
    private final InternalService internalService; // 별도 빈 주입
    
    public void external() {
        internalService.internal(); // 외부 호출 → 프록시 경유 → AOP 적용!
    }
}
```

---

## 프록시 기술과 한계 — 타입 캐스팅

- **JDK 동적 프록시**: 인터페이스 기반 → 구체 클래스로 캐스팅 불가 (ClassCastException)
- **CGLIB**: 구체 클래스 상속 기반 → 구체 클래스/인터페이스 모두 캐스팅 가능

```java
// JDK 프록시인 경우
MemberService proxy = ...;
MemberServiceImpl target = (MemberServiceImpl) proxy; // ClassCastException!
```

---

## CGLIB의 문제점과 스프링의 해결

| 문제 | 해결 |
|------|------|
| 기본 생성자 필수 | 스프링 4.0: objenesis 라이브러리로 생성자 없이 객체 생성 |
| 생성자 2번 호출 | 스프링 4.0: objenesis로 1번만 호출 |
| final 클래스/메서드 불가 | 일반 웹 애플리케이션에서는 거의 문제 없음 |

| 버전 | 해결 |
|------|------|
| 스프링 3.2 | CGLIB를 스프링 내부에 포함 (별도 라이브러리 불필요) |
| 스프링 4.0 | objenesis로 기본 생성자 없이도 프록시 생성, 생성자 1번만 호출 |
| **스프링 부트 2.0** | **CGLIB를 기본으로 사용** (인터페이스 있어도 CGLIB) |

### 스프링 부트 2.0부터의 기본 설정

**CGLIB를 기본으로 사용** (`proxyTargetClass=true`). 인터페이스가 있어도 항상 CGLIB로 구체 클래스 기반 프록시를 생성하여, 구체 클래스 타입으로의 의존관계 주입 문제를 해결.

---

## 전체 흐름 요약

```
"원본 코드를 안 건드리고 부가 기능을 적용하는 방법"을 단계적으로 발전시킨 이야기

로그 추적기 (파라미터 전달) — 불편
    ↓ 문제: 모든 메서드 파라미터 수정
쓰레드 로컬 (ThreadLocal) — 동시성 해결
    ↓ 문제: 원본 코드에 부가 기능 코드 침투
템플릿 메서드 → 전략 → 콜백 패턴 — 코드 중복 제거
    ↓ 문제: 여전히 원본 코드 수정 필요
프록시 패턴 / 데코레이터 패턴 — 원본 코드 수정 불필요!
    ↓ 문제: 대상 클래스마다 프록시 클래스 직접 생성
동적 프록시 (JDK / CGLIB) — 프록시 자동 생성
    ↓ 문제: 두 기술 통합 어려움
프록시 팩토리 + Advisor — 기술 통합
    ↓ 문제: 설정 지옥, 컴포넌트 스캔 미지원
빈 후처리기 (BeanPostProcessor) — 자동화
    ↓ 개선: 자동 프록시 생성기
@Aspect AOP (최종 완성형)
```

**최종 결론**: `@Aspect` + `@Around`/`@Before` 등으로 **원본 코드 한 줄 안 고치고** 로그, 트랜잭션, 재시도 같은 부가 기능을 깔끔하게 적용할 수 있다.
