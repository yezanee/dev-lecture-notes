package ref;

public class NullMain3 {
  public static void main(String[] args) {
    BigData bigData = new BigData();

    System.out.println("bigData.count=" + bigData.count); // 0이 출력
    System.out.println("bigData.data=" + bigData.data); // null이 출력

    //NullPointerException
    System.out.println("bigData.data.value=" + bigData.data.value); // null을 참조하므로 NullPointerException
  }
}
