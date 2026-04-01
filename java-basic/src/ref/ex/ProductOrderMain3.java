package ref.ex;

import java.util.Scanner;

public class ProductOrderMain3 {
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);

    System.out.print("입력할 주문의 개수를 입력하세요: ");
    int n = in.nextInt();

    ProductOrder[] orders = new ProductOrder[n];

    for(int i=0; i<n; i++) {
      System.out.println(i+1 + "번째 주문 정보를 입력하세요.");
      System.out.print("상품명: ");
      String productName = in.next();
      System.out.print("가격: ");
      int price = in.nextInt();
      System.out.print("수량: ");
      int quantity = in.nextInt();

      orders[i] = createOrder(productName, price, quantity);
    }

    printOrders(orders);
    System.out.println("총 결제 금액:" + getTotalAmount(orders));
  }

  static ProductOrder createOrder(String productName, int price, int quantity) {
    ProductOrder product = new ProductOrder();
    product.productName = productName;
    product.price = price;
    product.quantity = quantity;

    return product;
  }

  static void printOrders(ProductOrder[] orders) {
    for(ProductOrder order : orders) {
      System.out.println("이름: " + order.productName + ", 가격: " + order.price + ", 주문 수량: " + order.quantity);
    }
  }

  static int getTotalAmount(ProductOrder[] orders) {
    int sum = 0;

    for(ProductOrder order : orders) {
      sum += order.price * order.quantity;
    }

    return sum;
  }
}
