package org.chy.anubis.utils;

import org.chy.anubis.enums.CaseSourceType;

import static org.chy.anubis.Constant.*;

public class WarehouseUtils {

    /**
     * 获取 测试用例在远程仓库的路径
     *
     * @param caseSourceType
     * @param algorithmName
     * @return
     */
    public static String getTestCasePath(CaseSourceType caseSourceType, String algorithmName) {
        return getPathFromAlgorithmPath(caseSourceType, algorithmName, TESTCASE_TEMPLATE_PATH);
    }


    /**
     * 获取这个算法在远程仓库对应的路径
     *
     * @return
     */
    public static String getAlgorithmPath(CaseSourceType caseSourceType, String algorithmName) {
        algorithmName = StringUtils.humpToLine(algorithmName);
        StringBuilder result = new StringBuilder();
        result.append(TESTCASE_PATH).append("/").append(caseSourceType.getName()).append("/")
                .append(algorithmName);
        return result.toString();
    }


    public static String getPathFromAlgorithmPath(CaseSourceType caseSourceType, String algorithmName, String name) {
        String algorithmPath = getAlgorithmPath(caseSourceType, algorithmName);
        return algorithmPath + "/" + name;
    }

    /**
     * 获取某个算法的包路径
     *
     * @param caseSourceType
     * @param algorithmName
     * @return
     */
    public static String getAlgorithmJavaAllPath(CaseSourceType caseSourceType, String algorithmName) {
        algorithmName = StringUtils.humpToLine(algorithmName);
        StringBuilder result = new StringBuilder();
        result.append(TREASURY_BASE_PATH).append(TESTCASE_PATH).append(".").append(caseSourceType.getName()).append(".")
                .append(algorithmName);
        return result.toString();
    }

    public static String getJavaAllPathFromAlgorithmPath(CaseSourceType caseSourceType, String algorithmName, String name) {
        String algorithmPath = getAlgorithmJavaAllPath(caseSourceType, algorithmName);
        return algorithmPath + "/" + name;
    }

    /**
     * 把 java的包全路径转成能够远程调用的文件路径
     * 如: org.chy.anubis.treasury.testcase.leetcode.two_sum.Algorithm --->  testcase/leetcode/two_sum.Algorithm.java
     */
    public static String javaPathToRemotePath(String javaPath) {
        return javaPackagePathToRemotePath(javaPath, true) + ".java";
    }

    public static String javaPackagePathToRemotePath(String javaPath, boolean isException) {
        if (!javaPath.startsWith(TREASURY_BASE_PATH)) {
            if (!isException) {
                return null;
            }
            throw new RuntimeException("类: [" + javaPath + "] 不能远程获取因为基础路径为 :[" + TREASURY_BASE_PATH + "]");
        }
        if (TREASURY_BASE_PATH.equals(javaPath)) {
            return "";
        }


        String result = javaPath.substring(TREASURY_BASE_PATH.length() + 1);
        return result.replace('.', '/');
    }

}
