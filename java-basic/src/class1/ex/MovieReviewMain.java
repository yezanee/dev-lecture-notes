package class1.ex;

public class MovieReviewMain {
  public static void main(String[] args) {
    MovieReview movie1 = new MovieReview("인셉션", "인생은 무한 루프");
    MovieReview movie2 = new MovieReview("어바웃 타임", "인생 시간 영화!");

    MovieReview[] movies = new MovieReview[]{movie1, movie2};

    for(MovieReview movie : movies) {
      System.out.println("영화 제목: " + movie.title + ", 리뷰: " + movie.review);
    }
  }
}
