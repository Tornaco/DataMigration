package org.newstand.lib;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import org.newstand.lib.common.Collections;
import org.newstand.lib.common.Logger;
import org.newstand.lib.common.MoreElements;
import org.newstand.lib.common.SettingsProvider;
import org.newstand.lib.iface.Adapter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.newstand.lib.SourceFiles.writeSourceFile;

/**
 * Created by Nick@NewStand.org on 2017/4/13 16:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@SupportedAnnotationTypes("org.newstand.lib.iface.Adapter")
public class AdapterCompiler extends AbstractProcessor {

    private ErrorReporter mErrorReporter;
    private Types mTypeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mErrorReporter = new ErrorReporter(processingEnvironment);
        mTypeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Logger.debug("process Adapter~");

        Collection<? extends Element> annotatedElements =
                roundEnvironment.getElementsAnnotatedWith(Adapter.class);

        List<TypeElement> types = new ImmutableList.Builder<TypeElement>()
                .addAll(ElementFilter.typesIn(annotatedElements))
                .build();

        Logger.debug("Will process %d types of ContextConfiguration", types.size());

        for (TypeElement type : types) {
            processType(type);
        }

        // We are the only ones handling AutoParcel annotations
        return true;
    }

    private void processType(TypeElement type) {
        Adapter annotation = type.getAnnotation(Adapter.class);
        if (annotation == null) {
            Logger.report("annotation is null on Type %s", type);
            return;
        }
        if (type.getKind() != ElementKind.INTERFACE) {
            mErrorReporter.abortWithError("@" + Adapter.class.getName() + " only applies to interfaces", type);
        }

        NestingKind nestingKind = type.getNestingKind();
        if (nestingKind != NestingKind.TOP_LEVEL) {
            mErrorReporter.abortWithError("@" + Adapter.class.getName() + " only applies to top level interfaces", type);
        }

        checkModifiersIfNested(type);

        // get the fully-qualified class name
        String fqClassName = generatedSubclassName(type, 0, annotation.subFix());
        // class name
        String className = CompilerUtil.simpleNameOf(fqClassName);
        String source = generateClass(type, className, type.getSimpleName().toString(), false);
        source = Reformatter.fixup(source);
        writeSourceFile(processingEnv, fqClassName, source, type);
    }

    private String generatedSubclassName(TypeElement type, int depth, String subFix) {
        return generatedClassName(type, null, Strings.repeat("$", depth) + subFix);
    }

    private String generatedClassName(TypeElement type, String prefix, String subFix) {
        String name = type.getSimpleName().toString();
        while (type.getEnclosingElement() instanceof TypeElement) {
            type = (TypeElement) type.getEnclosingElement();
            name = type.getSimpleName() + "_" + name;
        }
        String pkg = CompilerUtil.packageNameOf(type);
        String dot = Strings.isNullOrEmpty(pkg) ? "" : ".";
        String prefixChecked = Strings.isNullOrEmpty(prefix) ? "" : prefix;
        String subFixChecked = Strings.isNullOrEmpty(subFix) ? "" : subFix;
        return pkg + dot + prefixChecked + name + subFixChecked;
    }


    private String generateClass(TypeElement type, String className, String ifaceToImpl, boolean isFinal) {
        if (type == null) {
            mErrorReporter.abortWithError("generateClass was invoked with null type", null);
            return null;
        }
        if (className == null) {
            mErrorReporter.abortWithError("generateClass was invoked with null class name", type);
            return null;
        }
        if (ifaceToImpl == null) {
            mErrorReporter.abortWithError("generateClass was invoked with null iface", type);
            return null;
        }

        String pkg = CompilerUtil.packageNameOf(type);

        TypeSpec.Builder subClass = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addMethods(createMethodSpecs(type))
                .addSuperinterface(ClassName.get(pkg, ifaceToImpl));

        // Add type params.
        List l = type.getTypeParameters();
        Collections.consumeRemaining(l, o -> {
            TypeParameterElement typeParameterElement = (TypeParameterElement) o;
            subClass.addTypeVariable(TypeVariableName.get(typeParameterElement.toString()));
        });

        if (isFinal) subClass.addModifiers(FINAL);

        JavaFile javaFile = JavaFile.builder(pkg, subClass.build())
                .addFileComment(SettingsProvider.FILE_COMMENT)
                .skipJavaLangImports(true)
                .build();
        return javaFile.toString();
    }

    private Iterable<MethodSpec> createMethodSpecs(TypeElement typeElement) {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        List<? extends Element> ex = ElementFilter.methodsIn(typeElement.getEnclosedElements());
        DeclaredType classType = (DeclaredType) typeElement.asType();
        Logger.debug("classType = %s", classType);

        for (Element e : ex) {
            Logger.debug("e = %s", e.getClass());
            ExecutableElement executableElement = MoreElements.asExecutable(e);
            MethodSpec methodSpec = MethodSpec.overriding(executableElement, classType, mTypeUtils)
                    .build();
            TypeName returnType = methodSpec.returnType;
            String returnStatement = returnStatement(returnType);
            if (returnStatement != null)
                methodSpec = methodSpec.toBuilder().addStatement(returnStatement).build();
            methodSpecs.add(methodSpec);
        }
        return methodSpecs;
    }

    private String returnStatement(TypeName returnType) {
        Logger.debug("returnType %s", returnType);
        switch (returnType.toString()) {
            case "void":
                return null;
            case "boolean":
                return "return false";
            case "char":
                return "return null";
            case "int":
            case "long":
            case "float":
            case "double":
            case "short":
                return "return 0";
        }
        return "return null";
    }

    private void checkModifiersIfNested(TypeElement type) {
        ElementKind enclosingKind = type.getEnclosingElement().getKind();
        if (enclosingKind.isClass() || enclosingKind.isInterface()) {
            if (type.getModifiers().contains(PRIVATE)) {
                mErrorReporter.abortWithError("@Adapter class must not be private", type);
            }
            if (!type.getModifiers().contains(STATIC)) {
                mErrorReporter.abortWithError("Nested @Adapter class must be static", type);
            }
        }
        // In principle type.getEnclosingElement() could be an ExecutableElement (for a class
        // declared inside a method), but since RoundEnvironment.getElementsAnnotatedWith doesn't
        // return such classes we won't see them here.
    }

    private boolean ancestorIs(TypeElement type, Class<? extends Annotation> clz) {
        while (true) {
            TypeMirror parentMirror = type.getSuperclass();
            if (parentMirror.getKind() == TypeKind.NONE) {
                return false;
            }
            TypeElement parentElement = (TypeElement) mTypeUtils.asElement(parentMirror);
            if (MoreElements.isAnnotationPresent(parentElement, clz)) {
                return true;
            }
            type = parentElement;
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
