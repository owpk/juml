package com.owpk.drawio.model;

import org.w3c.dom.Element;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationLine extends UmlElement {
    private String source;
    private String target;

    public RelationLine(int type, String source, String target,
            String umlId, String parentId, Element element, Element mxGeometry) {
        super(umlId, parentId, element, mxGeometry);
        this.source = source;
        this.target = target;
        var value = type == 1 ? "extends" : "";
        var style = new ElementAttributes(switch (type) {
            case 1 -> "endArrow=block;endSize=16;endFill=0;html=1";
            case 0 -> "endArrow=block;";
            default -> "";
        });
        style.addAttribute("edgeStyle", "orthogonalEdgeStyle");
        style.addAttribute("jumpStyle", "arc");
        setStyle(style);
        setGeometryAttribute("width", "160");
        setGeometryAttribute("relative", "1");

        setElementAttribute("value", value);
        setElementAttribute("edge", "1");
        setElementAttribute("source", source);
        setElementAttribute("target", target);
    }
}
