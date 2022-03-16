package org.chy.test.anubis;


import org.chy.anubis.treasury.testcase.leetcode.two_sum.template.case02.TestCase;

public class AlgorithmTemplate implements org.chy.anubis.treasury.testcase.leetcode.two_sum.Algorithm {

    @Override
    public int[] twoSum(int[] nums, int target) {
        Mytest instance = new Mytest();

        System.out.println("开始------->");
        int[] result = instance.onetest222(nums, target);

        System.out.println("结束-------> 算法执行时间为");
        return result;
    }

    public void run(){
        TestCase testCase = new TestCase();
        testCase.run(this);
    }

}
