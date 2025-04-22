package com.owpk.drawio;

import com.owpk.drawio.model.UmlElement;
import com.owpk.drawio.model.box.BoxStyle;
import com.owpk.model.ClassType;
import com.owpk.model.ClassUml;

public interface XmlElementRegistry {
    BoxStyle getStyle(ClassType type);

    BoxStyle getStyle(ClassType type, BoxStyle style);

    void setStyle(ClassType type, BoxStyle style);

    UmlElement createClassBox(ClassUml classUml);

    UmlElement createLine(int value, String sourceId, String targetId);

    UmlElement getByUmlId(String parentId);
}
