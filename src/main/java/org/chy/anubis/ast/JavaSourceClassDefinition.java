package org.chy.anubis.ast;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 整个 java 源文件的类描述对象.
 */
public class JavaSourceClassDefinition {

    char[] datas;

    public JavaSourceClassDefinition(char[] data) {
        this.datas = data;
        analyzing();
    }

    /**
     * 分析整个源码文件
     */
    private void analyzing() {
        readLine(linedata ->{
            System.out.println(linedata);
        });
    }

    /**
     * 按照行去读取
     */
    private void readLine(Consumer<LineData> lineCall) {
        int startIndex = 0;

        for (int i = 0; i < datas.length; i++) {
            char data = datas[i];
            if (data == '\n') {
                //说明2个换行符连续了, 继续把行指针后移
                if (startIndex == i) {
                    startIndex++;
                } else {
                    LineData lineData = new LineData(datas, startIndex, i - 1);
                    //把下一行的开始指针重新设置一下
                    startIndex = i + 1;
                    if (lineData.isHasContent()) {
                        lineCall.accept(lineData);
                    }
                }
            }
        }
    }


}
