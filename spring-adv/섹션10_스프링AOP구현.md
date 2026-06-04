# 섹션 10: 스프링 AOP 구현

---

## 기본 구현

```java
@Slf4j
@Aspect
public class AspectV1 {

    @Around("execution(* hello.aop.order..*(..))")  // 포인트컷: 어디에?
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();  // 진짜 메서드 실행
    }
}
```

## 포인트컷 분리

```java
@Aspect
public class AspectV2 {
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder() {} // 포인트컷 시그니처

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable { ... }
}
```

## 포인트컷 조합

```java
@Pointcut("execution(* hello.aop.order..*(..))")
public void allOrder() {}  // 주문 패키지 전체

@Pointcut("execution(* *..*Service.*(..))")
public void allService() {}  // *Service 클래스만

@Around("allOrder() && allService()")  // 둘 다 만족하는 것만
public Object doTransaction(...) { ... }
```

포인트컷 조합: `&&` (AND), `||` (OR), `!` (NOT)

## 어드바이스 순서

`@Order` 애노테이션으로 지정. **@Aspect 단위**로만 적용 가능 → 별도 클래스로 분리 필요.

```java
@Aspect @Order(1)
public static class TxAspect { ... }  // 먼저 실행

@Aspect @Order(2)
public static class LogAspect { ... } // 나중 실행
```

## 어드바이스 종류

| 종류 | 설명 | 비유 |
|------|------|------|
| `@Around` | 메서드 전후 모두, 가장 강력 (proceed() 호출 필수) | 전체 통제 |
| `@Before` | 조인 포인트 실행 이전 | 출입 기록 |
| `@AfterReturning` | 정상 완료 후 (returning으로 반환값 받기) | 성공 알림 |
| `@AfterThrowing` | 예외 발생 시 (throwing으로 예외 받기) | 에러 알림 |
| `@After` | 정상/예외 무관 실행 (finally) | 항상 실행 |

## 왜 @Around만 안 쓰고 나눠놨을까?

**좋은 설계는 제약이 있는 것이다.**

`@Around`를 쓰면 `proceed()`를 깜빡 호출 안 할 수 있음 → 진짜 메서드가 실행 안 되는 장애 발생.
`@Before`를 쓰면 `proceed()`를 호출할 필요 자체가 없음 → 실수가 불가능.

> **비유**: 칼(Around)은 뭐든 할 수 있지만 위험함. 가위(Before/After)는 할 수 있는 게 제한되지만 안전함. 가위로 충분하면 가위를 쓰자.
