package org.chy.anubis.warehouse;

import org.chy.anubis.enums.CaseSourceType;

import java.util.List;

/**
 * 用于获取 测试用例的接口
 */
public interface Warehouse {

    /**
     * 获取一个算法下面的所有 测试用例的目录
     *
     * @param caseSourceType 案例来源
     * @param algorithmName 算法的名称
     */
    public void getCaseCatalog(CaseSourceType caseSourceType, String algorithmName);

}
