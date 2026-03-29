package class1;

public class ClassStart2 {
  public static void main(String[] args) {

    // 설계도인 클래스를 사용해서 실제 메모리에 만들어진 실체를 객체 또는 인스턴스라고 함.
    Student stu1 = new Student("학생1", 15, 90);
    Student stu2 = new Student("학생2", 16, 80);

    Student[] students = {stu1, stu2}; // 배열 객체 생성 (자바 축약 문법) - 배열에 참조값 대입
    // Student[] students = new Student[]{stu1, stu2};

    for(int i = 0; i < 2; i++) {
      formatPrint(students[i].name, students[i].age, students[i].grade);
    }
  }

  public static void formatPrint(String name, int age, int grade) {
    System.out.printf("이름: %s 나이: %d 성적: %d\n", name, age, grade);
  }
}
