package com.owpk.drawio.model;

import static com.owpk.drawio.Utils.addAttr;

import java.util.Optional;

import org.w3c.dom.Element;

import com.owpk.core.Sorting;
import com.owpk.core.XmlIdAware;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmlElement implements XmlIdAware, Sorting {
    protected int sorting;
    protected String xmlId;
    protected Element element;
    protected Element mxGeometry;

    public UmlElement(String id, String parentXmlId, Element element, Element mxGeometry) {
        this.xmlId = id;
        this.element = element;
        addAttr(element, "id", id);
        addAttr(element, "parent", parentXmlId);
        this.mxGeometry = mxGeometry;
        element.appendChild(mxGeometry);
    }

    public UmlElement(UmlElement umlElement) {
        this.xmlId = umlElement.xmlId;
        this.element = umlElement.element;
        this.mxGeometry = umlElement.mxGeometry;
    }

    public void setStyleName(String name) {
        var style = getStyle();
        style.setName(name);
        setStyle(style);
    }

    public void setStyleAttribute(String name, String value) {
        var style = getStyle();
        style.addAttribute(name, value);
        setStyle(style);
    }

    public void setStyle(ElementAttributes style) {
        addAttr(element, "style", style.getAttributesString());
    }

    public ElementAttributes getStyle() {
        return new ElementAttributes(getElementAttribte(element, "style").orElse(""));
    }

    public int getWidth() {
        return getAttrOrElase("width", mxGeometry);
    }

    public int getHeight() {
        return getAttrOrElase("height", mxGeometry);
    }

    public int getX() {
        return getAttrOrElase("x", mxGeometry);
    }

    public int getY() {
        return getAttrOrElase("y", mxGeometry);
    }

    public void setX(int x) {
        addAttr(mxGeometry, "x", Integer.toString(x));
    }

    public void setY(int y) {
        addAttr(mxGeometry, "y", Integer.toString(y));
    }

    public void setWidth(int width) {
        addAttr(mxGeometry, "width", Integer.toString(width));
    }

    public void setHeight(int height) {
        addAttr(mxGeometry, "height", Integer.toString(height));
    }

    public void setElementAttribute(String name, String value) {
        addAttr(element, name, value);
    }

    public void setGeometryAttribute(String name, String value) {
        addAttr(mxGeometry, name, value);
    }

    public Optional<String> getElementAttribte(String attr) {
        return Optional.ofNullable(element.getAttribute(attr));
    }

    public Optional<String> getGeometryAttribte(String attr) {
        return getElementAttribte(mxGeometry, attr);
    }

    private Optional<String> getElementAttribte(Element element, String attr) {
        return Optional.ofNullable(element.getAttribute(attr));
    }

    private int getAttrOrElase(String attr, Element element) {
        return Integer.parseInt(getElementAttribte(element, attr).orElse("-1"));
    }

}
