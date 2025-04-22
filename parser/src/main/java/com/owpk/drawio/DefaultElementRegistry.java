package com.owpk.drawio;

import static com.owpk.drawio.Utils.addAttr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.owpk.drawio.model.RelationLine;
import com.owpk.drawio.model.UmlElement;
import com.owpk.drawio.model.box.BoxStyle;
import com.owpk.drawio.model.box.ClassBox;
import com.owpk.drawio.model.box.ClassBoxField;
import com.owpk.drawio.model.box.ClassBoxMethod;
import com.owpk.drawio.model.box.ClassBoxSeparator;
import com.owpk.model.ClassType;
import com.owpk.model.ClassUml;
import com.owpk.model.FieldInfo;
import com.owpk.model.MethodInfo;

public class DefaultElementRegistry implements XmlElementRegistry {

    private Map<ClassType, BoxStyle> STYLE_MAP = Map.of(
            ClassType.CLASS, BoxStyle.getDefault().fontStyle(1).build(),
            ClassType.INTERFACE, BoxStyle.getDefault().bgColor("#d5e8d4").borderColor("#82b366").fontStyle(3).build(),
            ClassType.ABSTRACT_CLASS,
            BoxStyle.getDefault().bgColor("#dae8fc").borderColor("#6c8ebf").fontStyle(3).build(),
            ClassType.ENUM, BoxStyle.getDefault().bgColor("yellow").fontStyle(1).build());

    private Map<String, UmlElement> elements = new HashMap<>();

    private Document document;
    private Element root;
    private String nodeId;

    private int index;

    public DefaultElementRegistry(Element root, Document document, String nodeId) {
        this.document = document;
        this.root = root;
        this.nodeId = nodeId;
    }

    public DefaultElementRegistry(Element root, Document document, String nodeId,
            Map<ClassType, BoxStyle> classBoxStyles) {
        this(root, document, nodeId);
        this.STYLE_MAP = classBoxStyles;
    }

    public BoxStyle getStyle(ClassType type) {
        return STYLE_MAP.get(type);
    }

    public BoxStyle getStyle(ClassType type, BoxStyle style) {
        return STYLE_MAP.getOrDefault(type, style);
    }

    public void setStyle(ClassType type, BoxStyle style) {
        STYLE_MAP.put(type, style);
    }

    @Override
    public UmlElement createClassBox(ClassUml classUml) {
        var style = STYLE_MAP.get(classUml.getType());
        var classBoxId = inc();

        var textHeight = 20;
        var boxFields = registerFields(textHeight, style, classBoxId, classUml.getFields());
        var boxMethods = registerMethods(textHeight, style, classBoxId, classUml.getMethods());
        var separator = registerClassBoxSeparator(inc(), classBoxId, style.getBorderColor());

        var classBox = new ClassBox(classBoxId, nodeId, createMxCell(), createMxGeometry(),
                classUml, style, boxFields, separator, boxMethods);
        root.appendChild(classBox.getElement());

        elements.put(classBox.getXmlId(), classBox);

        var headerSize = classBox.getStyle().getAttribute("startSize");
        if (headerSize == null) {
            headerSize = "30";
            classBox.setStyleAttribute("startSize", headerSize);
        }
        var inBoxY = Integer.parseInt(headerSize);

        for (var field : classBox.getFields()) {
            field.setY(inBoxY);
            inBoxY += field.getHeight();
        }

        separator.setY(inBoxY);
        inBoxY += separator.getHeight();

        for (var method : classBox.getMethods()) {
            method.setY(inBoxY);
            inBoxY += method.getHeight();
        }

        classBox.setHeight(inBoxY);
        return classBox;
    }

    private List<ClassBoxField> registerFields(int height, BoxStyle style, String parentId, List<FieldInfo> fields) {
        return fields.stream()
                .map(it -> new ClassBoxField(height, style, it, inc(), parentId, createMxCell(), createMxGeometry()))
                .peek(it -> elements.put(it.getXmlId(), it))
                .peek(it -> root.appendChild(it.getElement()))
                .toList();
    }

    private List<ClassBoxMethod> registerMethods(int height, BoxStyle style, String parentId,
            List<MethodInfo> methods) {
        return methods.stream()
                .map(it -> new ClassBoxMethod(height, style, it, inc(), parentId, createMxCell(), createMxGeometry()))
                .peek(it -> elements.put(it.getXmlId(), it))
                .peek(it -> root.appendChild(it.getElement()))
                .toList();
    }

    private ClassBoxSeparator registerClassBoxSeparator(String id, String parentId, String color) {
        var classBoxSeparator = new ClassBoxSeparator(color, id, parentId, createMxCell(), createMxGeometry());
        elements.put(classBoxSeparator.getXmlId(), classBoxSeparator);
        root.appendChild(classBoxSeparator.getElement());
        return classBoxSeparator;
    }

    private Element createMxCell() {
        return document.createElement("mxCell");
    }

    private Element createMxGeometry() {
        var geometry = document.createElement("mxGeometry");
        addAttr(geometry, "as", "geometry");
        return geometry;
    }

    private String inc() {
        return String.valueOf(++index);
    }

    @Override
    public UmlElement createLine(int value, String sourceId, String targetId) {
        var line = new RelationLine(value, sourceId, targetId, inc(), this.nodeId, createMxCell(), createMxGeometry());
        root.appendChild(line.getElement());
        return line;
    }

    @Override
    public UmlElement getByUmlId(String parentId) {
        return elements.get(parentId);
    }
}
