package edu.lu.uni.serval.javabusinesslocs.locations;

import edu.lu.uni.serval.javabusinesslocs.output.CodePosition;
import edu.lu.uni.serval.javabusinesslocs.output.Operators;

import spoon.compiler.Environment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.cu.position.SourcePositionImpl;
import spoon.support.reflect.reference.CtPackageReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import javax.swing.text.Position;

import java.util.List;

import static edu.lu.uni.serval.javabusinesslocs.output.Operators.InvocationMutator;

public class InvocationLocation extends BusinessLocation<CtInvocation> {

    protected InvocationLocation(int firstMutantId, CtInvocation ctElement) throws UnhandledElementException {
        super(firstMutantId, ctElement);
    }

    @Override
    public CodePosition getCodePosition(CtInvocation original) throws UnhandledElementException {
        String originalOp = getOriginalValueOfTheNode(original);
        int source_start = original.getPosition().getSourceStart();

        int start,end = 0;

        if (original.toString().contains("."+originalOp)) {
            if(!original.getTarget().getPosition().isValidPosition()) {
                start = source_start;
                end = start + originalOp.length() - 1;
            } else {
                start = original.getTarget().getPosition().getSourceEnd() + 2;
                end = start + originalOp.length() - 1;
            }

        } else {
            start = source_start;
            end = start + originalOp.length() - 1;
            //super is called as init
            if (originalOp.equals("<init>"))
                end--;

        }


        CompilationUnit origUnit = original.getPosition().getCompilationUnit();
        SourcePosition position = new SourcePositionImpl(origUnit,start,end,origUnit.getLineSeparatorPositions());
        if (!position.isValidPosition()) return super.getCodePosition(original);
        return new CodePosition(position.getSourceStart(), position.getSourceEnd());


    }

    @Override
    protected String getOriginalValueOfTheNode(CtInvocation ctElement) {
        return ctElement.getExecutable().getSimpleName();
    }

    @Override
    protected Operators getOperatorByType(CtInvocation ctElement) {
        return InvocationMutator;
    }


}
