package org.chy.anubis.warehouse;

import org.chy.anubis.entity.CaseBriefInfo;
import org.chy.anubis.entity.FileBaseInfo;
import org.chy.anubis.entity.FileInfo;
import org.chy.anubis.enums.CaseSourceType;

import java.util.List;
import java.util.Optional;

/**
 * 用于获取 测试用例的接口
 */
public interface Warehouse {

    /**
     * 获取一个算法下面的所有 测试用例的目录
     *  @param caseSourceType 案例来源
     * @param algorithmName 算法的名称
     * @return
     */
    public List<CaseBriefInfo> getCaseCatalog(CaseSourceType caseSourceType, String algorithmName);


    /**
     * 查询对应目录下面所有的 文件夹/文件
     * @param path 如果查询的路径为: org/chy/anubis/treasury/aa/bb 那么 就传 aa/bb
     * @return
     */
    List<FileBaseInfo> getFileBaseInfoList(String path);

    /**
     * 根据文件路径获取文件内容
     * @param path
     * @return
     */
    Optional<FileInfo> getFileInfo(String path);
}
