# 섹션 1: JDBC 이해

## 1. JDBC가 등장한 이유

### 문제 상황: 데이터베이스 변경의 고통

애플리케이션이 데이터베이스에 접근하려면 다음 3가지 작업이 필요하다:

1. **커넥션 연결**: 주로 TCP/IP를 사용해 DB와 연결
2. **SQL 전달**: 커넥션을 통해 SQL을 DB에 전달
3. **결과 응답**: DB가 SQL 실행 후 결과를 응답

**문제**: 각 데이터베이스(MySQL, Oracle, PostgreSQL 등)마다 커넥션 연결 방법, SQL 전달 방법, 결과 응답 방법이 **모두 다르다**.

→ DB를 변경하면 애플리케이션의 **데이터 접근 코드를 전부 수정**해야 한다.
→ 개발자는 각 DB마다 **사용법을 새로 학습**해야 한다.

### 해결: JDBC 표준 인터페이스

**JDBC(Java Database Connectivity)**는 자바에서 데이터베이스에 접근할 수 있도록 만든 **표준 인터페이스**다.

```
[애플리케이션 코드] → [JDBC 표준 인터페이스] → [각 DB 벤더의 JDBC 드라이버]
                      ├── Connection           ├── MySQL JDBC Driver
                      ├── Statement            ├── Oracle JDBC Driver
                      └── ResultSet            └── H2 JDBC Driver
```

JDBC 표준 인터페이스가 정의하는 3가지:
- **`java.sql.Connection`** — 연결
- **`java.sql.Statement`** — SQL 전달
- **`java.sql.ResultSet`** — 결과 응답

### JDBC 드라이버

각 DB 벤더(MySQL, Oracle 등)에서 자신의 DB에 맞도록 JDBC 인터페이스를 **구현한 라이브러리**. 예를 들어:
- MySQL DB → MySQL JDBC 드라이버
- Oracle DB → Oracle JDBC 드라이버

### JDBC의 장점

1. **DB 변경 시 코드 유지**: 드라이버만 교체하면 됨 (완전히 동일한 것은 아님 — SQL 문법 차이는 존재)
2. **학습 비용 감소**: JDBC 표준 인터페이스만 학습하면 됨

## 2. JDBC와 최신 데이터 접근 기술

JDBC를 직접 사용하면 코드가 매우 복잡하고 반복적이다. 그래서 JDBC를 편리하게 사용하는 기술들이 등장했다:

### SQL Mapper

```
[애플리케이션] → [SQL Mapper] → [JDBC]
```

- 개발자가 **SQL을 직접 작성**하고, 그 결과를 객체로 편리하게 매핑해줌
- JDBC의 반복 코드를 대신 처리해줌
- 대표 기술: **스프링 JdbcTemplate**, **MyBatis**

### ORM (Object-Relational Mapping)

```
[애플리케이션] → [ORM (JPA)] → [JDBC]
```

- 객체를 관계형 데이터베이스 테이블과 **자동으로 매핑**
- **SQL을 직접 작성하지 않아도** ORM 기술이 자동으로 SQL을 생성
- 대표 기술: **JPA** (인터페이스), **Hibernate** (구현체)

> **핵심**: SQL Mapper든 ORM이든 **내부적으로 모두 JDBC를 사용**한다. 따라서 JDBC의 기본 동작을 이해하는 것이 필수적이다.

## 3. 데이터베이스 연결

### 프로젝트 설정

```groovy
// build.gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    runtimeOnly 'com.h2database:h2'
    // ...
}
```

### H2 데이터베이스 준비

H2는 개발, 테스트에 많이 사용하는 가벼운 데이터베이스:

```sql
-- 테이블 생성
drop table member if exists cascade;
create table member (
    member_id varchar(10),
    money integer not null default 0,
    primary key (member_id)
);
```

### 커넥션 상수 정의

```java
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
```

### 커넥션 획득 유틸리티

```java
@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            Connection connection = 
                DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
```

### DriverManager 연결 원리

`DriverManager.getConnection()` 호출 시 내부 동작:

```
1. DriverManager.getConnection(url, username, password) 호출
        ↓
2. 등록된 드라이버 목록을 순회
        ↓
3. 각 드라이버에게 URL을 전달하며 "이 URL 처리할 수 있어?" 확인
   - org.h2.Driver → "jdbc:h2:" 로 시작? → YES!
   - com.mysql.jdbc.Driver → "jdbc:mysql:" 로 시작? → NO
        ↓
4. 처리 가능한 드라이버가 실제 커넥션을 생성하여 반환
   (H2의 경우 JdbcConnection 객체 반환)
```

## 4. JDBC 개발 — CRUD

### Member 도메인

```java
@Data
public class Member {
    private String memberId;
    private int money;

    public Member() {}

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
```

### 등록 (Create)

```java
public Member save(Member member) throws SQLException {
    String sql = "insert into member(member_id, money) values (?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
        con = getConnection();                    // 1. 커넥션 획득
        pstmt = con.prepareStatement(sql);        // 2. SQL 준비
        pstmt.setString(1, member.getMemberId()); // 3. 파라미터 바인딩
        pstmt.setInt(2, member.getMoney());
        pstmt.executeUpdate();                    // 4. SQL 실행
        return member;
    } catch (SQLException e) {
        log.error("db error", e);
        throw e;
    } finally {
        close(con, pstmt, null);                  // 5. 리소스 정리
    }
}
```

**핵심 포인트:**

- **`PreparedStatement`**: `?`를 통한 **파라미터 바인딩** 방식. SQL Injection 공격을 방지한다. 절대 문자열 연결(`"... values('" + memberId + "'...")`)을 사용하면 안 된다!
- **`executeUpdate()`**: INSERT, UPDATE, DELETE처럼 데이터를 **변경**하는 SQL에 사용. 영향받은 행 수를 반환
- **`finally`에서 리소스 정리**: 커넥션, Statement는 외부 리소스이므로 **반드시 닫아야** 한다. 닫지 않으면 커넥션이 끊어지지 않고 계속 유지되는 **리소스 누수** 발생

### 조회 (Read)

```java
public Member findById(String memberId) throws SQLException {
    String sql = "select * from member where member_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, memberId);

        rs = pstmt.executeQuery();         // SELECT는 executeQuery() 사용!
        if (rs.next()) {                   // 커서를 다음 행으로 이동
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        } else {
            throw new NoSuchElementException(
                "member not found memberId=" + memberId);
        }
    } catch (SQLException e) {
        log.error("db error", e);
        throw e;
    } finally {
        close(con, pstmt, rs);
    }
}
```

**ResultSet 이해:**

```
          member_id    money
커서 →    (데이터 없음)        ← 최초 커서 위치
          "hi1"        10000   ← rs.next() → true
          "hi2"        20000   ← rs.next() → true
          (끝)                 ← rs.next() → false
```

- `rs.next()`: 커서를 다음 행으로 이동. 데이터가 있으면 `true`, 없으면 `false`
- **최초 커서는 데이터를 가리키고 있지 않으므로** 반드시 `rs.next()`를 한 번 호출해야 데이터를 읽을 수 있다
- `rs.getString("컬럼명")`, `rs.getInt("컬럼명")`: 현재 커서가 가리키는 행의 데이터를 읽음

### 수정 (Update) / 삭제 (Delete)

```java
// 수정
public void update(String memberId, int money) throws SQLException {
    String sql = "update member set money=? where member_id=?";
    // ... executeUpdate() 사용 (등록과 동일한 구조)
}

// 삭제
public void delete(String memberId) throws SQLException {
    String sql = "delete from member where member_id=?";
    // ... executeUpdate() 사용 (등록과 동일한 구조)
}
```

## 5. JDBC 코드의 공통 패턴

모든 JDBC 코드는 동일한 패턴을 따른다:

```
1. 커넥션 획득
2. SQL 준비 (PreparedStatement)
3. 파라미터 바인딩
4. SQL 실행 (executeUpdate 또는 executeQuery)
5. 결과 처리 (ResultSet)
6. 리소스 정리 (finally에서 close)
```

> 이 반복적인 패턴이 바로 이후 섹션에서 해결할 문제다. 스프링의 `JdbcTemplate`이 이 반복을 제거해준다.
