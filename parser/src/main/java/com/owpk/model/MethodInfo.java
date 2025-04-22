package com.owpk.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MethodInfo {
    private Visibility visibility;
    private String returnType;
    private List<ParameterInfo> argTypes;
    private String name;
}
