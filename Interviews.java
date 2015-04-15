package com.oniox.learn;

public class Interviews {

	static class Node<V> {
		private V value;
		Node<V> next;

		Node(V v) {
			this.value = v;

		}
	}

	public static void main(String args[]) {

		// reverseNode();
		//String value = "eke";
		//isPalindrone(value);
		char found = findRepeatedChar("ssserrccccggxxxx");
		System.out.println(found);		
		reverse("nigeria");

	}

	private static boolean isPalindrone(String value) {
		char vals[] = value.toCharArray();
		int lo = 0;
		int hi = vals.length - 1;

		while (lo < hi) {
			if (vals[lo] != vals[hi]) {
				System.out.println("NOT A PALINDRONE");
				return false;
			} else {
				lo++;
				hi--;
			}
		}
		System.out.println(" A PALINDRONE");
		return true;
	}
	
	private static String reverse(String value) {
		assert value != null && value.length() > 0;
		char values [] = value.toCharArray();
		StringBuffer sbuf = new StringBuffer();
		
		for (int i=values.length-1; i >= 0; i--) {
			System.out.print(values[i]);
			sbuf.append(values[i]);
		}
		
		return sbuf.toString();
		
	}

	private static char findRepeatedChar(String value) {
		
		assert value != null && value.length() > 0;
	
		char values [] = value.toCharArray();
		char current = values[0];
		char selected = current;
		int matchCount = 0;
		int maxCount = 0;
		//ssserrccccggxxxx
		
		for (char v : values) {
			if (v == current) {
				matchCount+=1;
				if (matchCount > maxCount) {
					selected=current;
					maxCount = matchCount;
				}
			} else {
				current = v;
				matchCount = 1;
			}
		}
		return selected;

	}

	public static int fib(int n) {
		if (n <= 1) {
			return n;
		} else {
			return fib(n - 1) + fib(n - 2);
		}
	}

	public static int fibIter(int n) {
		int prev1 = 0, prev2 = 1;
		for (int i = 0; i < n; i++) {
			int savePrev1 = prev1;
			prev1 = prev2;
			prev2 = savePrev1 + prev2;
		}
		return prev1;
	}
	
	public static int max(int[] t) {
	    int maximum = t[0];   // start with the first value
	    for (int i=1; i<t.length; i++) {
	        if (t[i] > maximum) {
	            maximum = t[i];   // new maximum
	        }
	    }
	    return maximum;
	}//end method max

	public static long factorial(long n) {
		if (n <= 1)
			return 1;

		else

			return n * factorial(n - 1);
	}

	private static void reverseNode() {
		Node<String> node1 = makeNode("A");
		node1.next = makeNode("B");
		node1.next.next = makeNode("C");

		Node<String> tempNode1 = node1;
		while (tempNode1.next != null) {
			Node<String> nextNode = tempNode1.next;
			Node<String> finalNode = nextNode.next;
			// tempNode1.nex

		}
	}

	static void swap(Node<String> a) {
		Node<String> nextNode = a.next;
		Node<String> nextOfNextNode = nextNode.next;
		a.next = null;
		nextNode.next = a;
		// b.next
	}

	static Node<String> makeNode(String value) {
		return new Interviews.Node<String>(value);
	}

}
