package com.lzh.bi.constants;

import java.util.Arrays;
import java.util.List;

/**
 * 通用常量
 *
 * @author lzh
 */
public interface CommonConst {

    /**
     * 文件大小限制为1MB
     */
    long FILE_MAX_SIZE = 1024 * 1024L;

    /**
     * 合法的文件类型
     */
    List<String> FILE_TYPE_LIST = Arrays.asList("xlsx", "xls");
}
