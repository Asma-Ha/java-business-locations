package edu.lu.uni.serval.mbertloc.mbertlocations;

import edu.lu.uni.serval.mbertloc.output.CodePosition;
import edu.lu.uni.serval.mbertloc.output.MBertOperators;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.support.reflect.cu.position.SourcePositionImpl;

import static edu.lu.uni.serval.mbertloc.output.MBertOperators.CodeBERTInvocationMutator;

public class InvocationLocation extends MBertLocation<CtInvocation> {

    protected InvocationLocation(int firstMutantId, CtInvocation ctElement) throws UnhandledElementException {
        super(firstMutantId, ctElement);
    }

    @Override
    protected CodePosition getCodePosition(CtInvocation original) throws UnhandledElementException {
        String originalOp = getOriginalValueOfTheNode(original);

        //compute token to mutate position
        int start = original.getPosition().getSourceStart();
        if (original.getTarget()!= null && !original.getTarget().isImplicit() && original.getTarget().toString().length() > 0)
            start = original.getTarget().getPosition().getSourceStart() + original.getTarget().toString().length()+1;
        int end = start + originalOp.length()-1;
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
    protected MBertOperators getOperatorByType(CtInvocation ctElement) {
        return CodeBERTInvocationMutator;
    }


}
