package org.chy.anubis.utils;

import org.chy.anubis.enums.CaseSourceType;

import static org.chy.anubis.Constant.TESTCASE_PATH;
import static org.chy.anubis.Constant.TESTCASE_TEMPLATE_PATH;

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

}
