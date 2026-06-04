# 섹션 12: 스프링 AOP - 실전 예제

---

## @Trace — 로그 자동 출력

메서드에 `@Trace`만 붙이면 호출 정보가 자동으로 로그에 찍힘.

### 애노테이션 정의

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {}
```

### Aspect 구현

```java
@Aspect
public class TraceAspect {
    @Before("@annotation(trace)")
    public void doTrace(JoinPoint joinPoint, Trace trace) {
        log.info("[trace] {} args={}", joinPoint.getSignature(), joinPoint.getArgs());
    }
}
```

### 사용

```java
@Trace
public void request(String itemId) { ... }
```

---

## @Retry — 예외 발생 시 자동 재시도

메서드에 `@Retry(4)`를 붙이면 예외 발생 시 최대 4번까지 재시도.

> **비유**: 자판기에 동전 넣었는데 안 나오면 최대 4번까지 다시 눌러보기.

### 애노테이션 정의

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int value() default 3;
}
```

### Aspect 구현

```java
@Aspect
public class RetryAspect {
    @Around("@annotation(retry)")
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        int maxRetry = retry.value();
        Exception exceptionHolder = null;

        for (int retryCount = 1; retryCount <= maxRetry; retryCount++) {
            try {
                return joinPoint.proceed(); // 성공하면 바로 반환
            } catch (Exception e) {
                exceptionHolder = e; // 실패하면 다시 시도
            }
        }
        throw exceptionHolder; // 전부 실패하면 예외 던짐
    }
}
```

### 사용

```java
@Retry(4)
public String save(String itemId) { ... }
```
