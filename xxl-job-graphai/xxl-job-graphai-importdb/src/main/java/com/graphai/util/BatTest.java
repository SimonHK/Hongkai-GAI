package com.graphai.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class BatTest {
	public Set getRandom(int num, int length) {
		Set<Integer> hs = new TreeSet<Integer>();
		while (true) {
			hs.add((int) (Math.random() * (num) + 1));
			if (hs.size() >= length) {
				break;
			}
		}
		return hs;
	}
	// test method
	public static void main(String[] args) {
		BatTest bt = new BatTest();
		Set hs = new TreeSet();
		hs = (TreeSet) bt.getRandom(100000000, 9);
		Iterator it = hs.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
}
