package class1;

public class ClassStart1 {
  public static void main(String[] args) {

    // 설계도인 클래스를 사용해서 실제 메모리에 만들어진 실체를 객체 또는 인스턴스라고 함.
    Student stu1 = new Student("학생1", 15, 90);
    Student stu2 = new Student("학생2", 16, 80);

    formatPrint(stu1.name, stu1.age, stu1.grade);
    formatPrint(stu2.name, stu2.age, stu2.grade);

    System.out.println(stu1); // stu1 객체의 참조값 반환 확인
    System.out.println(stu2); // stu2 객체의 참조값 반환 확인
  }

  public static void formatPrint(String name, int age, int grade) {
    System.out.printf("이름: %s 나이: %d 성적: %d\n", name, age, grade);
  }
}
