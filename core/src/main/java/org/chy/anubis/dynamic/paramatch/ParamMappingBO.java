package org.chy.anubis.dynamic.paramatch;

import lombok.Data;
import org.chy.anubis.entity.ParameterInfo;

import java.lang.reflect.Parameter;

@Data
public class ParamMappingBO {


    /**
     * 本地方法的参数类型
     */
    private Parameter parameter;

    /**
     *  映射到本地方法上对应的名称
     */
    private String newParamName;

    /**
     * 如果需要转换代码 那么转换表达式
     */
    private String convertExpression;


    /**
     * 远程接口上参数的类型
     */
    private ParameterInfo parameterInfo;



}
