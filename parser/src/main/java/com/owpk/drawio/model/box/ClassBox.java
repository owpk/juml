package com.owpk.drawio.model.box;

import java.util.List;
import java.util.stream.Stream;

import org.w3c.dom.Element;

import com.owpk.drawio.model.ElementAttributes;
import com.owpk.drawio.model.UmlElement;
import com.owpk.model.ClassUml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassBox extends UmlElement {
	private static final ElementAttributes swimLineAttr = new ElementAttributes(
			"swimlane;align=center;verticalAlign=top;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=0;marginBottom=0;html=1;fillStyle=auto;shadow=0;rounded=1;fillOpacity=100;strokeOpacity=100;separatorColor=none;noLabel=0;");

	private String classId;
	private String headerName;
	private BoxStyle boxStyle;

	private List<ClassBoxField> fields;
	private ClassBoxSeparator classBoxSeparator;
	private List<ClassBoxMethod> methods;

	public ClassBox(String umlId, String parentXmlId, Element element, Element mxGeometry, ClassUml uml, BoxStyle style,
			List<ClassBoxField> fields, ClassBoxSeparator classBoxSeparator, List<ClassBoxMethod> methods) {
		super(umlId, parentXmlId, element, mxGeometry);

		setElementAttribute("vertex", "1");

		this.classId = uml.getClassIdentity();
		this.headerName = style.getClassTypeIcons().get(uml.getType()) + " " + uml.getClassId().getName();
		this.boxStyle = style;
		this.fields = fields;
		this.methods = methods;
		this.classBoxSeparator = classBoxSeparator;

		setElementAttribute("value", getHeaderName());

		swimLineAttr.addAttribute("fillColor", style.getBgColor());
		swimLineAttr.addAttribute("strokeColor", style.getBorderColor());
		swimLineAttr.addAttribute("rounded", style.isBorderRounded() ? "1" : "0");
		swimLineAttr.addAttribute("glass", "0");
		swimLineAttr.addAttribute("swimlaneFillColor", "default");

		setStyle(swimLineAttr);
		if (uml.getClassId().getName().equals("ClassUmlElementEntry")) {
			System.out.println("");
		}
		var headerNameWidth = headerName.length() + 5;
		var dataWidth = Stream.concat(fields.stream().map(it -> it.getText().length()),
				methods.stream().map(it -> it.getText().length()))
				.max(Integer::compare).orElse(0);

		var maxWidht = (int) (Math.max(headerNameWidth, dataWidth) * 5.7) + style.getMargin();

		this.setWidth(maxWidht);
		Stream.concat(this.fields.stream(), this.methods.stream())
				.forEach(it -> it.setWidth(getWidth()));
		this.classBoxSeparator.setWidth(maxWidht);
	}

}
