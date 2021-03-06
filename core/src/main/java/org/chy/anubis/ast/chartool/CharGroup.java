package org.chy.anubis.ast.chartool;

import org.chy.anubis.entity.Pair;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * 可以当做一个 char[]去使用, 为了去复用原始数据而产生的数据结构
 * <p>
 * 如: data = 123456789   这样的原始char[] CharGroup(data, 3, 7)可以用来 当做一个 345678 的char[] 去使用
 */
public class CharGroup implements Iterable<Character> {
    char[] data;
    int startIndex;
    int endIndex;
    int len;


    public CharGroup(char[] data, int startIndex, int endIndex) {
        init(data, startIndex, endIndex);
    }

    protected CharGroup() {
    }

    private void init(char[] data, int startIndex, int endIndex) {
        if (endIndex >= data.length) {
            throw new ArrayIndexOutOfBoundsException("超过数组最大限额[" + (data.length - 1) + "] 当前 [" + endIndex + "]");
        }
        this.data = data;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.len = endIndex - startIndex + 1;
        if (len < 0) {
            throw new IllegalArgumentException("数组长度设置错误[" + len + "]");
        }
    }


    @Override
    public Iterator<Character> iterator() {
        return new CharGroupIterator();
    }

    /**
     * 正向获取 从0开始
     *
     * @param index
     * @return
     */
    public Character get(int index) {
        int realIndex = startIndex + index;
        if (realIndex > endIndex) {
            throw new ArrayIndexOutOfBoundsException("超过数组最大限额[" + len + "] 当前 [" + index + "]");
        }
        return data[realIndex];
    }

    /**
     * 逆向获取
     *
     * @param index
     * @return
     */
    public Character lastGet(int index) {
        int realIndex = endIndex - index;
        if (realIndex < startIndex) {
            throw new ArrayIndexOutOfBoundsException("超过数组最小限额[" + len + "] 当前 [" + index + "]");
        }
        return data[realIndex];
    }

    /**
     * 重新设置数组指针的位置
     *
     * @param startIndex
     * @param endIndex
     */
    protected void resetIndex(int startIndex, int endIndex) {
        if (startIndex == this.startIndex && endIndex == this.endIndex) {
            return;
        }
        init(data, startIndex, endIndex);
    }

    public CharGroup substring(int start, int end) {

        if (start >= 0) {
            start = this.startIndex + start;
        } else {
            start = this.endIndex + start;
        }

        if (end >= 0) {
            end = this.startIndex + end;
        } else {
            end = this.endIndex + end;
        }

        resetIndex(start, end);
        return this;
    }


    public int size() {
        return len;
    }


    public boolean equals(char[] target) {
        if (size() != target.length) {
            return false;
        }

        for (int i = 0; i < target.length - 1; i++) {
            Character c = target[i];
            Character oc = get(i);
            if (c != oc) {
                return false;
            }

        }
        return true;
    }

    public boolean equals(String target) {
        return equals(CharCache.get(target));
    }

    @Override
    public String toString() {
        Iterator<Character> iterator = iterator();
        StringBuilder result = new StringBuilder();
        while (iterator.hasNext()) {
            result.append(iterator.next());
        }
        return result.toString();
    }

    class CharGroupIterator implements Iterator<Character> {
        int index = 0;

        @Override
        public boolean hasNext() {
            return index < len;
        }

        @Override
        public Character next() {
            return get(index++);
        }
    }

}
