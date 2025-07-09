package calculator;

public class TotalCalculator {
    private int[] prices;

    public TotalCalculator(int[] prices) {
        this.prices = prices;
    }

    public int calculateTotal(int[] quantities) {
        int totalPrice = 0;
        for (int i = 0; i < prices.length; i++) {
            totalPrice += quantities[i] * prices[i];
        }
        return totalPrice;
    }
}