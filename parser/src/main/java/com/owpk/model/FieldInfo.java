package com.owpk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FieldInfo {
    private Visibility visibility;
    private String name;
    private String type;
    private boolean isStatic;
}
