package class1.ex;

public class ProductOrderMain {
  public static void main(String[] args) {
    int sum = 0;
    ProductOrder product1 = new ProductOrder("두부", 2000, 2);
    ProductOrder product2 = new ProductOrder("김치", 5000, 1);
    ProductOrder product3 = new ProductOrder("콜라", 1500, 2);
    ProductOrder[] products = new ProductOrder[]{product1, product2, product3};

    for(ProductOrder product : products) {
      System.out.println("상품명: " + product.productName + ", 가격: " + product.price + ", 수량: " + product.quantity);
      sum += product.price * product.quantity;
    }

    System.out.println("총 결제 금액: " + sum);
  }
}
