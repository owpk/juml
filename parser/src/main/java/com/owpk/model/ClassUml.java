package com.owpk.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ClassUml {
    private Visibility visibility;
    private ClassType type;
    private ClassId classId;

    @Builder.Default
    private List<FieldInfo> fields = new ArrayList<>();

    @Builder.Default
    private List<MethodInfo> methods = new ArrayList<>();

    @Singular("parent")
    private List<ClassUml> parent;

    public String getClassIdentity() {
        return classId.toString();
    }

    private ClassUml(Visibility visibility, ClassType type, ClassId classId, List<FieldInfo> fields,
            List<MethodInfo> methods,
            List<ClassUml> elements) {
        this.visibility = visibility;
        this.type = type;
        this.classId = classId;
        this.fields = fields;
        this.methods = methods;
        this.parent = new ArrayList<>(elements);
    }

}