# 섹션 8: @Aspect AOP

---

## @Aspect란?

지금까지 배운 것을 **애노테이션 하나로 편하게** 쓰는 방법.

```java
@Aspect
public class LogAspect {
    @Around("execution(* hello.order..*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[로그] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}
```

이게 내부적으로는:
- `@Around`의 표현식 → **포인트컷**
- `doLog` 메서드 → **어드바이스**
- 합쳐서 → **어드바이저**

> **비유**: "이 구역(포인트컷)에 CCTV(어드바이스)를 설치해주세요"라고 한 마디(애노테이션) 하면, 스프링이 알아서 해당 구역 모든 곳에 CCTV를 달아줌.

## @Aspect를 어드바이저로 변환하는 과정

자동 프록시 생성기(`AnnotationAwareAspectJAutoProxyCreator`)가 두 가지 역할 수행:
1. `@Aspect`를 발견하여 Advisor로 변환하고 저장
2. 생성된 Advisor를 기반으로 프록시 생성

## 프록시 생성 과정

```
1. 스프링 시작
2. @Aspect 붙은 빈 찾기
3. @Aspect → Advisor로 변환 (포인트컷 + 어드바이스)
4. 빈 등록 시마다 포인트컷으로 확인
5. 해당되면 프록시 생성 → 빈으로 등록
```

상세 과정:
1. 스프링 빈 대상 객체 생성
2. 빈 후처리기에 전달
3. 스프링 컨테이너의 모든 Advisor 빈 조회 + @Aspect 어드바이저 빌더 내부 Advisor 조회
4. 포인트컷으로 프록시 적용 대상 판단
5. 적용 대상이면 프록시 생성, 아니면 원본 반환
6. 최종 객체를 스프링 빈으로 등록
