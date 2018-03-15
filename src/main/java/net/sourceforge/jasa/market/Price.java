package net.sourceforge.jasa.market;

import java.math.BigDecimal;
import java.lang.Math;
import java.text.DecimalFormat;

public class Price extends java.lang.Number implements Comparable<Price> {

    public static final int DEFAULT_EXPONENT = 4;

    protected long longValue;

    protected int multiplier;

	static DecimalFormat currencyFormatter = new DecimalFormat(
	    "+#########0.0000;-#########.0000");

	protected Price(int exponent) {
	    this.multiplier = 1;
        for(int i=0; i<exponent; i++) {
            multiplier *= 10;
        }
    }

    public Price(long longValue, int exponent) {
	    this(exponent);
        this.longValue = longValue;
    }

    public Price(long whole, long fractional, int exponent) {
	   this(exponent);
	   this.longValue = whole * multiplier + fractional;
    }

    public Price(long longValue) {
        this(longValue, DEFAULT_EXPONENT);
    }

    public Price(double price, int exponent) {
	    this(exponent);
	    this.longValue = (long) (price * multiplier);
    }

    public Price(double price) {
        this(price, DEFAULT_EXPONENT);
    }

    public Price(BigDecimal price, int exponent) {
        this(exponent);
        this.longValue = price.multiply(new BigDecimal(multiplier)).longValue();
    }

    public Price(BigDecimal price) {
        this(price, DEFAULT_EXPONENT);
    }

    @Override
    public int intValue() {
        return (int) longValue;
    }

    public long longValue() {
        return longValue;
    }

    @Override
    public float floatValue() {
        return (float) longValue;
    }

    public double doubleValue() {
        return ((double) longValue) / multiplier;
    }

    @Override
    public int compareTo(Price other) {
        if (this.multiplier != other.multiplier) {
            throw new UnsupportedOperationException("Comparision of prices with different tick sizes is not supported.");
        }
        return (int) (other.longValue() - this.longValue());
    }

    public boolean isPositive() {
        return longValue > 0;
    }

    public boolean isNegative() {
        return longValue < 0;
    }

    public String toPrettyString() {
        return currencyFormatter.format(doubleValue());
    }

    @Override
    public int hashCode() {
        return intValue();
    }

    @Override
    public boolean equals(Object obj) {
	    return this.longValue == ((Price) obj).longValue;
    }

//    @Override
//    public String toString() {
//        return "Price{" +
//                "whole=" + whole +
//                ", fractional=" + fractional +
//                ", multiplier=" + multiplier +
//                '}';
//    }

    public static void main(String[] args) {
        Price p = new Price(3.14);
        System.out.println(p);
        System.out.println(p.toPrettyString());

        Price p2 = new Price(3.19);
        System.out.println(p2);
        System.out.println(p2.toPrettyString());

        Price p3 = new Price(3.19, 2);
        System.out.println(p3);
        System.out.println(p3.toPrettyString());
    }
}
