package ref.ex;

public class ProductOrderMain2 {
  public static void main(String[] args) {
    ProductOrder product1 = createOrder("두부", 5000, 2);
    ProductOrder product2 = createOrder("김치", 3000, 1);
    ProductOrder product3 = createOrder("오이", 6000, 2);

    ProductOrder[] orders = new ProductOrder[]{product1, product2, product3};

    printOrders(orders);

    System.out.println("총 결제 금액: " + getTotalAmount(orders));
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
