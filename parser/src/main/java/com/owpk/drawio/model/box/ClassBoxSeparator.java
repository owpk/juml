package com.owpk.drawio.model.box;

import org.w3c.dom.Element;

import com.owpk.drawio.model.ElementAttributes;
import com.owpk.drawio.model.UmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassBoxSeparator extends UmlElement {
    private static final ElementAttributes classNameAttr = new ElementAttributes(
            "line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;");

    public ClassBoxSeparator(String color, String id, String parentId, Element element, Element mxGeometry) {
        super(id, parentId, element, mxGeometry);
        classNameAttr.addAttribute("strokeColor", color);
        setStyle(classNameAttr);
        setElementAttribute("vertex", "1");
        setHeight(8);
    }

}
