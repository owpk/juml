package com.owpk.drawio.model.box;

import java.util.Map;

import com.owpk.model.ClassType;
import com.owpk.model.Visibility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoxStyle {

        public static BoxStyle.BoxStyleBuilder getDefault() {
                return BoxStyle.builder()
                                .bgColor("white")
                                .borderColor("black")
                                .isBorderRounded(true)
                                .borderWidth("1px")
                                .fontSize("12px")
                                .fontFamily("Arial")
                                .fontColor("black")
                                .borderRadius("0px")
                                .margin(10)
                                .visibilityIcons(Map.of(Visibility.PRIVATE, "- ", Visibility.PROTECTED, "# ",
                                                Visibility.PUBLIC, "+ ",
                                                Visibility.DEFAULT, "  "))
                                .classTypeIcons(Map.of(ClassType.ABSTRACT_CLASS, "(A) ", ClassType.CLASS, "(C) ",
                                                ClassType.ENUM,
                                                "(E) ", ClassType.INTERFACE, "(I) "));
        }

        private String bgColor;
        private String fontColor;
        private String fontSize;
        private String fontFamily;
        private Integer fontStyle;
        private String borderColor;
        private String borderWidth;
        private boolean isBorderRounded;
        private String borderRadius;
        private Map<Visibility, String> visibilityIcons;
        private Map<ClassType, String> classTypeIcons;
        public int margin;
}
