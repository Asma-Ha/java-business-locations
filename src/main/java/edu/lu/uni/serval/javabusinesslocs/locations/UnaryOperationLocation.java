package edu.lu.uni.serval.javabusinesslocs.locations;

import edu.lu.uni.serval.javabusinesslocs.locator.LocsUtils;
import edu.lu.uni.serval.javabusinesslocs.output.CodePosition;
import edu.lu.uni.serval.javabusinesslocs.output.Operators;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.support.reflect.cu.position.SourcePositionImpl;

import static edu.lu.uni.serval.javabusinesslocs.output.Operators.UnaryOperationMutator;
import static edu.lu.uni.serval.javabusinesslocs.output.Operators.UnaryOperatorMutator;

public class UnaryOperationLocation extends BusinessLocation<CtUnaryOperator> {

    protected UnaryOperationLocation(int firstMutantId, CtUnaryOperator ctElement) throws UnhandledElementException {
        super(firstMutantId, ctElement);
    }

    @Override
    protected CodePosition getCodePosition(CtUnaryOperator ctElement) throws UnhandledElementException {
        SourcePosition origPosition = LocsUtils.getSourcePosition(ctElement);
        int end = origPosition.getSourceEnd();

        int start = end - (getOriginalValueOfTheNode(ctElement).length() - 1);
        CompilationUnit origUnit = origPosition.getCompilationUnit();
        SourcePosition position = new SourcePositionImpl(origUnit,start,end,origUnit.getLineSeparatorPositions());

        if (!position.isValidPosition()) return super.getCodePosition(ctElement);
        return new CodePosition(position.getSourceStart(), position.getSourceEnd());

    }


    @Override
    protected String getOriginalValueOfTheNode(CtUnaryOperator ctElement) {
        return ctElement.toString();
    }

    @Override
    protected Operators getOperatorByType(CtUnaryOperator ctElement) {
        return UnaryOperationMutator;
    }

}
