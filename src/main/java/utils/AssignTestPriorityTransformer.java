package utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import javassist.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AssignTestPriorityTransformer implements IAnnotationTransformer {

    private ClassPool classPool = ClassPool.getDefault();

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setPriority(getMethodLineNumber(testMethod));
    }

    private int getMethodLineNumber(Method testMethod) {
        try {
            CtClass cc = classPool.get(testMethod.getDeclaringClass().getCanonicalName());
            CtMethod methodX = cc.getDeclaredMethod(testMethod.getName());
            return methodX.getMethodInfo().getLineNumber(0);
        }
        catch(Exception e) {
            throw new RuntimeException("Getting of line number of method " + testMethod + "  failed", e);
        }
    }
}
