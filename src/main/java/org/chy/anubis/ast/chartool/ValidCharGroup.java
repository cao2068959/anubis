package org.chy.anubis.ast.chartool;

import lombok.Getter;
import org.chy.anubis.ast.chartool.CharCache;
import org.chy.anubis.ast.chartool.CharGroup;
import org.chy.anubis.ast.chartool.CharGroupMatch;
import org.chy.anubis.entity.Pair;
import org.chy.anubis.enums.CharGroupMatchResult;

/**
 * 仅仅只保留有效内容的 charGroup, 会把 空格/换行 等一些占位符给去掉
 */
public class ValidCharGroup extends CharGroup {

    private int oldStartIndex;
    private int oldEndIndex;

    /**
     * 这一行中除了空格和注释之外是否有内容
     */
    @Getter
    private boolean hasContent = true;

    /**
     * 正向的空格数
     */
    int forwardBlankSpace = 0;

    /**
     * 逆向的空格数
     */
    int reverseBlankSpace = 0;

    public ValidCharGroup(char[] data, int startIndex, int endIndex) {
        super(data, startIndex, endIndex);
        oldStartIndex = startIndex;
        oldEndIndex = endIndex;
        handleValidData();
    }

    /**
     * 处理生成有效的数据,这里有效数据指的是 去除了空格以及注释后的数据
     *
     * @return
     */
    private void handleValidData() {

        Pair<Integer, Integer> forwardScanResult = forwardScan(0);
        //整段文本中只有空格和注释,其他都没有
        if (!hasContent){
            return;
        }

        //逆向扫描
        Pair<Integer, Integer> reverseScanResult = doScanBlankSpace(false,0);
        this.forwardBlankSpace = forwardScanResult.getValue();
        this.reverseBlankSpace = reverseScanResult.getValue();
        resetIndex(oldStartIndex + forwardScanResult.getKey(), oldEndIndex - reverseScanResult.getKey());
    }

    /**
     * 数据的前置扫描, 会去除空格以及去除注释
     * @param offset
     * @return
     */
    private Pair<Integer, Integer> forwardScan(int offset){
        Pair<Integer, Integer> forwardScanResult = doScanBlankSpace(true,offset);
        //正向扫描的时候整行都扫描完了, 说明没实际性的内容
        if (forwardScanResult.getKey() + offset >= this.size() - 1) {
            hasContent = false;
            return null;
        }
        return forwardScanResult;
    }



    /**
     * 真正去扫描空格数
     *
     * @param isForward 正向扫描还是逆向扫描
     * @return key:扫描到第几个字符的时候没有空格了 value:空格的数量
     */
    protected Pair<Integer, Integer> doScanBlankSpace(boolean isForward, int offset) {
        int index = offset;
        int blankSpaceNum = 0;

        for (; index < this.size(); index++) {
            Character character;
            if (isForward) {
                character = this.get(index);
            } else {
                character = this.lastGet(index);
            }
            if (character == '\n' || character == '\r') {
                blankSpaceNum = 0;
                continue;
            }
            if (character == ' ') {
                blankSpaceNum++;
                continue;
            }
            break;
        }
        return Pair.of(index, blankSpaceNum);
    }

}
