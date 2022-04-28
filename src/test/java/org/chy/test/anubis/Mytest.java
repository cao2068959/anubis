package org.chy.test.anubis;

import org.chy.anubis.annotation.Trial;
import org.chy.test.anubis.dto.ListNode;

public class Mytest {

    @Trial("xxxx")
    public void onetest() {
        System.out.println("-------->");
    }

    @Trial("two_sum")
    public int[] onetest222(int[] nums, Integer target) {
        System.out.println("-------->");
        return null;
    }

    @Trial("twoSum")
    public void onetest3333() {
        System.out.println("-------->");
    }

    @Trial("addTwoNumbers")
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        System.out.println(l1);
        return null;
    }

}
