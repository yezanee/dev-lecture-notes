# 섹션 11: 스프링 AOP - 포인트컷

---

## 포인트컷 지시자 종류

| 지시자 | 설명 | 예시 |
|--------|------|------|
| `execution` | 메서드 실행 매칭 **(가장 많이 사용)** | `execution(* save(..))` |
| `within` | 특정 타입 내 조인 포인트 | `within(hello.OrderService)` |
| `args` | 인자 타입으로 매칭 (부모 타입 허용) | `args(String)` |
| `this` | 프록시 객체 대상 | - |
| `target` | 실제 대상 객체 대상 | - |
| `@target` | 클래스에 애노테이션이 있는 경우 (인스턴스 기준, 상속 포함) | - |
| `@within` | 해당 타입에 애노테이션이 있는 경우 (선언 타입만) | - |
| `@annotation` | 메서드에 애노테이션이 있는 경우 | `@annotation(MyLog)` |
| `bean` | 스프링 빈 이름으로 지정 (스프링 전용) | `bean(orderService)` |

## execution 문법

```
execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
```

- `*`: 아무 값 하나
- `..`: 파라미터 타입/수 무관, 또는 하위 패키지 포함
- `.`: 정확히 해당 위치의 패키지

```java
// 가장 넓은 표현: 모든 메서드
execution(* *(..))

// 패키지 + 하위 패키지
execution(* hello.aop.member..*(..))

// 반환타입 String, 메서드명이 hello로 시작
execution(String hello*(..))
```

## 파라미터 매칭

| 표현 | 의미 |
|------|------|
| `()` | 파라미터 없음 |
| `(String)` | 정확히 String 1개 |
| `(*)` | 아무 타입 1개 |
| `(..)` | 아무 타입 0개 이상 (가장 많이 씀) |
| `(String, ..)` | 첫 번째는 String, 나머지는 아무거나 |

## execution vs args 차이

- `execution`: 클래스 선언 정보 기반 — 정확한 타입 매칭
- `args`: 실제 파라미터 인스턴스 기반 — 부모 타입 허용

```java
execution(* *(Object))  // 선언된 타입이 정확히 Object여야 함
args(Object)            // 실제 넘어온 값이 Object의 자식이면 OK
```

> **비유**:
> - `execution` = 이력서에 "Object 전공"이라 적혀있어야 함
> - `args` = 실제로 Object 계열이면 다 OK (String도 Object의 자식이니까 통과)

## this vs target 차이

- `this`: 프록시 객체 기준 매칭
- `target`: 실제 대상 객체 기준 매칭

> **비유**:
> - `this` = "매니저(프록시)한테 물어보기" → 매니저가 아는 범위까지만
> - `target` = "연예인(실제 객체)한테 물어보기" → 연예인의 모든 것

JDK 동적 프록시에서 구체 클래스 지정 시:
- `this(MemberServiceImpl)` → AOP 미적용 (프록시는 인터페이스 기반)
- `target(MemberServiceImpl)` → AOP 적용 (target은 해당 타입)

## 매개변수 전달

```java
@Before("allMember() && args(arg, ..)")
public void logArgs3(String arg) { ... }

@Before("allMember() && @annotation(annotation)")
public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
    log.info("annotationValue={}", annotation.value());
}
```
