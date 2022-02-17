package org.chy.anubis.ast;

import lombok.Getter;
import org.chy.anubis.entity.Pair;

import java.util.function.Function;

/**
 * 行数据
 */
public class LineData extends CharGroup {

    private int lineStartIndex;
    private int lineEndIndex;

    /**
     * 这一行中除了空格是否有内容
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

    public LineData(char[] data, int startIndex, int endIndex) {
        super(data, startIndex, endIndex);
        lineStartIndex = startIndex;
        lineEndIndex = endIndex;
        scanBlankSpace();
    }

    /**
     * 扫描空格
     *
     * @return
     */
    private void scanBlankSpace() {
        Pair<Integer, Integer> forwardScanResult = doScanBlankSpace(true);
        //正向扫描的时候整行都扫描完了, 说明没实际性的内容
        if (forwardScanResult.getKey() == this.size() - 1) {
            hasContent = false;
            return;
        }
        //逆向扫描
        Pair<Integer, Integer> reverseScanResult = doScanBlankSpace(false);
        this.forwardBlankSpace = forwardScanResult.getValue();
        this.reverseBlankSpace = reverseScanResult.getValue();

        resetIndex(lineStartIndex + forwardScanResult.getKey(), lineEndIndex - reverseScanResult.getKey());
    }

    /**
     * 真正去扫描空格数
     *
     * @param isForward 正向扫描还是逆向扫描
     * @return key:扫描到第几个字符的时候没有空格了 value:空格的数量
     */
    private Pair<Integer, Integer> doScanBlankSpace(boolean isForward) {
        int index = 0;
        int blankSpaceNum = 0;

        for (; index < this.size(); index++) {
            Character character;
            if (isForward) {
                character = this.get(index);
            } else {
                character = this.lastGet(index);
            }
            if (character == '\n' || character == '\r') {
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
