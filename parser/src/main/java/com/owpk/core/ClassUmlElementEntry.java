package com.owpk.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(of = "classId")
public class ClassUmlElementEntry {
    String classId;
    XmlIdAware umlElement;
}
