package ref;

public class MethodChange {
  public static void main(String[] args) {
    Data data = createData();
    printData(data);

    changeData(data);
    printData(data);
  }

  private static Data createData() {
    Data data = new Data();
    data.value = 10;
    return data;
  }

  private static Data changeData(Data data) {
    data.value = 20;
    return data;
  }

  private static void printData(Data data) {
    System.out.println(data.value);
  }
}
