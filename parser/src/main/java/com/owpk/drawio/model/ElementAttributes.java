package com.owpk.drawio.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class ElementAttributes {
    private String name = "";
    private Map<String, String> attributes;

    // attrs is "name;key=value;..." string
    public ElementAttributes(String attrs) {
        var name = attrs.substring(0, attrs.indexOf(";"));
        if (name != null && !name.isBlank() && name.contains("=")) {
            this.attributes = parseAttributes(attrs);
        } else {
            this.name = name;
            this.attributes = parseAttributes(attrs.substring(attrs.indexOf(";") + 1));
        }
    }

    private Map<String, String> parseAttributes(String attrs) {
        return Arrays.stream((attrs).split(";"))
                .map(attr -> attr.split("="))
                .collect(Collectors.toMap(
                        attr -> attr[0],
                        attr -> attr[1]));
    }

    public ElementAttributes() {
        this.name = "";
        this.attributes = new HashMap<>();
    }

    public String getAttributesString() {
        return (name == null || name.isBlank() ? ""
                : name + ";") + this.attributes.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining(";"));
    }

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }
}
