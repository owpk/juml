package com.owpk.core.javaimpl;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.InterfaceCapable;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.JavaUnit;
import org.jboss.forge.roaster.model.impl.JavaRecordImpl;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.VisibilityScopedSource;

import com.owpk.core.AbsExtractor;
import com.owpk.model.ClassId;
import com.owpk.model.ClassType;
import com.owpk.model.ClassUml;
import com.owpk.model.FieldInfo;
import com.owpk.model.MethodInfo;
import com.owpk.model.ParameterInfo;
import com.owpk.model.Visibility;

// TODO: refactor to use any java parser
public class JavaExtractorImpl extends AbsExtractor {

    public JavaExtractorImpl(Path extractFromPath) {
        super(extractFromPath, ".java");
    }

    @Override
    public Optional<ClassUml> getUmlSource(String rawData) {
        JavaUnit unit = Roaster.parseUnit(rawData);
        JavaType<?> javaType = unit.getGoverningType();

        if (javaType instanceof JavaClassSource jc) {
            var uml = populateClassInfo(jc);
            resolveParentSuperType(jc.getSuperType(), uml);
            resolveParentInterfaces(jc, uml);
            return resolve(uml);
        } else if (javaType instanceof JavaInterfaceSource ji) {
            var uml = populateInterfaceInfo(ji);
            resolveParentInterfaces(ji, uml);
            return resolve(uml);
        } else if (javaType instanceof JavaEnumSource je) {
            var uml = populateEnumInfo(je);
            return resolve(uml);
        } else if (javaType instanceof JavaRecordImpl jr) {
            var uml = populateRecordInfo(jr);
            return resolve(uml);
        } else {
            return Optional.empty();
        }
    }

    private Optional<ClassUml> resolve(ClassUml uml) {
        sourceEntries.put(uml.getClassIdentity(), uml);
        return Optional.of(uml);
    }

    private ClassUml populateRecordInfo(JavaRecordImpl jr) {
        var uml = ClassUml.builder();
        uml.classId(new ClassId(jr.getName(), jr.getPackage()));
        uml.type(ClassType.CLASS);
        uml.visibility(getVisibility(jr));
        uml.methods(getMethods(jr.getMethods()));
        return uml.build();
    }

    private ClassUml populateEnumInfo(JavaEnumSource je) {
        var uml = ClassUml.builder();
        uml.classId(new ClassId(je.getName(), je.getPackage()));
        uml.type(ClassType.ENUM);
        uml.visibility(getVisibility(je));
        // Get fields
        uml.fields(getFields(je.getFields()));
        return uml.build();
    }

    private ClassUml populateInterfaceInfo(JavaInterfaceSource interfaceSource) {
        var uml = ClassUml.builder();
        uml.classId(new ClassId(interfaceSource.getName(), interfaceSource.getPackage()));
        uml.type(ClassType.INTERFACE);
        uml.visibility(getVisibility(interfaceSource));

        // Get implemented interfaces
        uml.methods(getMethods(interfaceSource.getMethods()));
        return uml.build();
    }

    private ClassUml populateClassInfo(JavaClassSource classSource) {
        var uml = ClassUml.builder();
        var classId = new ClassId(classSource.getName(), classSource.getPackage());
        uml.visibility(getVisibility(classSource));
        uml.classId(classId);
        uml.type(getJavaType(classSource));
        // Get fields
        uml.fields(getFields(classSource.getFields()));
        // Get methods
        uml.methods(getMethods(classSource.getMethods()));
        return uml.build();
    }

    private void resolveParentInterfaces(InterfaceCapable source, ClassUml uml) {
        var ifaces = source.getInterfaces();
        for (var iface : ifaces) {
            if (iface != null && !iface.equals("java.lang.Serializable")) {
                if (sourceEntries.containsKey(iface)) {
                    uml.getParent().add(sourceEntries.get(iface));
                } else {
                    unresolwedChains.add(new ChainEntry(iface, uml));
                }
            }
        }
    }

    private void resolveParentSuperType(String superType, ClassUml uml) {
        if (superType != null && !superType.equals("java.lang.Object")) {
            if (sourceEntries.containsKey(superType)) {
                uml.getParent().add(sourceEntries.get(superType));
            } else {
                unresolwedChains.add(new ChainEntry(superType, uml));
            }
        }
    }

    private <T extends JavaSource<T>> List<FieldInfo> getFields(List<FieldSource<T>> fieldsSource) {
        return fieldsSource.stream()
                .map(it -> new FieldInfo(getVisibility(it), it.getName(), it.getType().getName(), false))
                .toList();
    }

    private <T extends JavaSource<T>> List<MethodInfo> getMethods(List<MethodSource<T>> methodsSource) {
        return methodsSource.stream()
                .filter(it -> it.isPublic() || it.isAbstract())
                .map(this::getMethod).toList();
    }

    private MethodInfo getMethod(MethodSource<?> methodSource) {
        return MethodInfo.builder()
                .visibility(getVisibility(methodSource))
                .name(methodSource.getName())
                .returnType(methodSource.getReturnType() == null ? "void" : methodSource.getReturnType().getName())
                .argTypes(methodSource.getParameters().stream()
                        .map(it -> new ParameterInfo(it.getName(), it.getType().getName())).toList())
                .build();
    }

    private Visibility getVisibility(VisibilityScopedSource<?> source) {
        return switch (source.getVisibility()) {
            case PUBLIC -> Visibility.PUBLIC;
            case PROTECTED -> Visibility.PROTECTED;
            case PRIVATE -> Visibility.PRIVATE;
            default -> Visibility.DEFAULT;
        };
    }

    private ClassType getJavaType(JavaClassSource javaType) {
        if (javaType.isInterface()) {
            return ClassType.INTERFACE;
        } else if (javaType.isEnum()) {
            return ClassType.ENUM;
        } else if (javaType.isAnnotation()) {
            return ClassType.ANNOTATION;
        } else if (javaType.isAbstract()) {
            return ClassType.ABSTRACT_CLASS;
        } else {
            return ClassType.CLASS;
        }
    }

}
