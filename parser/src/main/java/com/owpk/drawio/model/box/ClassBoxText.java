package com.owpk.drawio.model.box;

import org.w3c.dom.Element;

import com.owpk.drawio.model.ElementAttributes;
import com.owpk.drawio.model.UmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassBoxText extends UmlElement {
    private static final ElementAttributes inClassAttr = new ElementAttributes(
            "text;html=1;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;");

    protected String text;

    public ClassBoxText(String text, int height, String id, String parentId, Element element, Element mxGeometry) {
        this(height, id, parentId, element, mxGeometry);
        this.text = text;
        setElementAttribute("value", text);
    }

    public ClassBoxText(int height, String id, String parentId, Element element, Element mxGeometry) {
        super(id, parentId, element, mxGeometry);
        setStyle(inClassAttr);
        setElementAttribute("vertex", "1");
        setHeight(height);
    }

    public void setText(String text) {
        this.text = text;
        setElementAttribute("value", text);
    }
}
