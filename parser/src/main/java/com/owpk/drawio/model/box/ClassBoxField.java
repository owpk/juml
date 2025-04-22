package com.owpk.drawio.model.box;

import org.w3c.dom.Element;

import com.owpk.model.FieldInfo;

public class ClassBoxField extends ClassBoxText {

    public ClassBoxField(int height, BoxStyle boxStyle, FieldInfo fieldInfo, String id,
            String parentId, Element element, Element mxGeometry) {
        super(height, id, parentId, element, mxGeometry);
        setText(createField(fieldInfo, boxStyle));
    }

    private String createField(FieldInfo fieldInfo, BoxStyle style) {
        return style.getVisibilityIcons().get(fieldInfo.getVisibility())
                + fieldInfo.getName() + ": "
                + fieldInfo.getType();
    }

}
