package net.sourceforge.jasa.market;

import java.text.DecimalFormat;

public class Price implements Comparable<Price> {

    public static final int DEFAULT_EXPONENT = 4;

    protected long whole;

    protected long fractional;

    protected int exponent;

    protected int multiplier;

	static DecimalFormat currencyFormatter = new DecimalFormat(
	    "+#########0.0000;-#########.0000");

    public Price(long whole, long fractional, int exponent) {
        this.whole = whole;
        this.fractional = fractional;
        this.exponent = exponent;
        this.multiplier = 1;
        for(int i=0; i<exponent; i++) {
            multiplier *= 10;
        }
    }

    public Price(long whole, long fractional) {
        this(whole, fractional, DEFAULT_EXPONENT);
    }

    public Price(long whole) {
        this(whole, 0);
    }

    public Price(double price) {
        this((long) price);
        this.fractional = (long) (Math.abs(price - whole) * multiplier);
    }

    public long longValue() {
        return whole * multiplier + fractional;
    }

    public double doubleValue() {
        return whole + ((double) fractional) / multiplier;
    }

    @Override
    public int compareTo(Price other) {
        return (int) (this.longValue() - other.longValue());
    }

    public boolean isPositive() {
        return whole > 0;
    }

    public boolean isNegative() {
        return whole < 0;
    }

    public String toPrettyString() {
        return currencyFormatter.format(doubleValue());
    }

    @Override
    public String toString() {
        return "Price{" +
                "whole=" + whole +
                ", fractional=" + fractional +
                ", exponent=" + exponent +
                ", multiplier=" + multiplier +
                '}';
    }

    public static void main(String[] args) {
        Price p = new Price(3, 14);
        System.out.println(p);
        System.out.println(p.toPrettyString());

        Price p2 = new Price(3.19);
        System.out.println(p2);
        System.out.println(p2.toPrettyString());
    }
}
