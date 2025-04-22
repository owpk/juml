package com.owpk.drawio.model.box;

import java.util.stream.Collectors;

import org.w3c.dom.Element;

import com.owpk.model.MethodInfo;

public class ClassBoxMethod extends ClassBoxText {

    public ClassBoxMethod(int height, BoxStyle boxStyle, MethodInfo methodInfo, String id,
            String parentId, Element element, Element mxGeometry) {
        super(height, id, parentId, element, mxGeometry);
        setText(createMethod(methodInfo, boxStyle));
    }

    private String createMethod(MethodInfo methodInfo, BoxStyle style) {
        var visability = style.getVisibilityIcons().get(methodInfo.getVisibility());
        var args = methodInfo.getArgTypes().stream()
                .map(it -> it.getType()).collect(Collectors.joining(","));

        return visability + methodInfo.getName() + "(" + args + ")" + ": " + methodInfo.getReturnType();
    }

}
