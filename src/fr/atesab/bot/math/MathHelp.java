package fr.atesab.bot.math;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class MathHelp {

	public static BigInteger bigIntSqRootCeil(BigInteger x) throws IllegalArgumentException {
	    if (x.compareTo(BigInteger.ZERO) < 0) {
	        throw new IllegalArgumentException("Negative argument.");
	    }
	    // square roots of 0 and 1 are trivial and
	    // y == 0 will cause a divide-by-zero exception
	    if (x == BigInteger.ZERO || x == BigInteger.ONE) {
	        return x;
	    } // end if
	    BigInteger two = BigInteger.valueOf(2L);
	    BigInteger y;
	    // starting with y = x / 2 avoids magnitude issues with x squared
	    for (y = x.divide(two);
	            y.compareTo(x.divide(y)) > 0;
	            y = ((x.divide(y)).add(y)).divide(two));
	    if (x.compareTo(y.multiply(y)) == 0) {
	        return y;
	    } else {
	        return y.add(BigInteger.ONE);
	    }
	}
	public static BigInteger bigIntSqRootFloor(BigInteger x)
	        throws IllegalArgumentException {
	    if (x.compareTo(BigInteger.ZERO) < 0) {
	        throw new IllegalArgumentException("Negative argument.");
	    }
	    // square roots of 0 and 1 are trivial and
	    // y == 0 will cause a divide-by-zero exception
	    if (x .equals(BigInteger.ZERO) || x.equals(BigInteger.ONE)) {
	        return x;
	    } // end if
	    BigInteger two = BigInteger.valueOf(2L);
	    BigInteger y;
	    // starting with y = x / 2 avoids magnitude issues with x squared
	    for (y = x.divide(two);
	            y.compareTo(x.divide(y)) > 0;
	            y = ((x.divide(y)).add(y)).divide(two));
	    return y;
	}
	public static Map<BigInteger, BigInteger> decomposition(BigInteger n) throws Exception{
		if(n.compareTo(BigInteger.ONE)!=1) throw new Exception("Not a positive integer.");
		BigInteger max = bigIntSqRootCeil(n);
		Map<BigInteger, BigInteger> map = new HashMap<BigInteger,BigInteger>();
		for (BigInteger i = BigInteger.valueOf(2L); i.compareTo(max)<1;i=i.add(BigInteger.ONE)) {
			if(!isPrime(i)) continue;
			if(n.mod(i).intValue()==0){
				map.put(i, map.getOrDefault(i, BigInteger.ZERO).add(BigInteger.ONE));
				n=n.divide(i);
				max = bigIntSqRootCeil(n);
				i=BigInteger.ONE;
			}
		}
		map.put(n, map.getOrDefault(n, BigInteger.ZERO).add(BigInteger.ONE));
		return map;
	}
	public static BigInteger fact(BigInteger n){
		BigInteger m = BigInteger.ONE;
		if(n.compareTo(BigInteger.ONE)!=1) throw new IllegalArgumentException("Not a positive integer.");
		for (BigInteger i = BigInteger.valueOf(1L); i.compareTo(n.add(BigInteger.ONE))<1;i=i.add(BigInteger.ONE))
			m=m.multiply(i);
		return m;
	}
	public static BigInteger HCF(BigInteger n, BigInteger p) {
		if((n.compareTo(BigInteger.ONE) <= 0 && n.compareTo(BigInteger.ONE) <= 0)) {
	        throw new IllegalArgumentException("Negative or(and) null argument(s).");
		}
		BigInteger a;
		BigInteger b;
		if(n.compareTo(p) < 0) {
			a = p;
			b = n;
		} else {
			a = n;
			b = p;
		}
		while (b.compareTo(BigInteger.ZERO)!=0) {
			BigInteger c = b;
			b = a.mod(b);
			a = c;
		}
		return a;
	}
	public static boolean isPrime(BigInteger n) {
		if(!n.equals(BigInteger.valueOf(2L)))
				for (BigInteger j = BigInteger.valueOf(3L); j.compareTo(bigIntSqRootCeil(n))<1; j=j.add(BigInteger.valueOf(2L))){
					if(n.mod(j).intValue()==0)return false;
				}
		return true;
	}
}
