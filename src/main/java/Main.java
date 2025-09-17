import utils.PointOfSaleDB;

public class Main {

    public static void main(String[] args) {
        PointOfSaleDB.getProductsTableAsList().forEach(System.out::println);
    }
}
