package utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import javassist.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AssignTestPriorityTransformer implements IAnnotationTransformer {

    private ClassPool classPool = ClassPool.getDefault();

    @Override
    public void transform(ITestAnnotation p_annotation, Class p_testClass, Constructor p_testConstructor, Method p_testMethod) {
        p_annotation.setPriority(getMethodLineNumber(p_testMethod));
    }

    private int getMethodLineNumber(Method p_testMethod) {
        try {
            CtClass cc = classPool.get(p_testMethod.getDeclaringClass().getCanonicalName());
            CtMethod methodX = cc.getDeclaredMethod(p_testMethod.getName());
            return methodX.getMethodInfo().getLineNumber(0);
        }
        catch(Exception e) {
            throw new RuntimeException("Getting of line number of method "+p_testMethod+" failed", e);
        }
    }
}
