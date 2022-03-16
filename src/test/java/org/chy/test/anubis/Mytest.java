package org.chy.test.anubis;

import org.chy.anubis.annotation.Trial;

public class Mytest {

    @Trial("xxxx")
    public void onetest(){
        System.out.println("-------->");
    }


    @Trial("two_sum")
    public int[] onetest222(int[] nums, Integer target){
        System.out.println("-------->");
        return null;
    }

    @Trial("twoSum")
    public void onetest3333(){
        System.out.println("-------->");
    }

}
