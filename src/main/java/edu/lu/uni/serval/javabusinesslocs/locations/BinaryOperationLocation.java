package edu.lu.uni.serval.javabusinesslocs.locations;

import edu.lu.uni.serval.javabusinesslocs.locator.LocsUtils;
import edu.lu.uni.serval.javabusinesslocs.output.CodePosition;
import edu.lu.uni.serval.javabusinesslocs.output.Operators;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.cu.position.SourcePositionImpl;

import static edu.lu.uni.serval.javabusinesslocs.output.Operators.BinaryOperationMutator;

public class BinaryOperationLocation extends BusinessLocation<CtBinaryOperator> {

    protected BinaryOperationLocation(int firstMutantId, CtBinaryOperator ctElement) throws UnhandledElementException {
        super(firstMutantId, ctElement);
    }

    @Override
    protected CodePosition getCodePosition(CtBinaryOperator ctElement) throws UnhandledElementException {
        SourcePosition origPosition = LocsUtils.getSourcePosition(ctElement);
        int end = origPosition.getSourceEnd();

        int start = end - (getOriginalValueOfTheNode(ctElement).length() - 1);



        CompilationUnit origUnit = origPosition.getCompilationUnit();
        SourcePosition position = new SourcePositionImpl(origUnit,start,end,origUnit.getLineSeparatorPositions());

        if (!position.isValidPosition()) return super.getCodePosition(ctElement);
        return new CodePosition(position.getSourceStart(), position.getSourceEnd());
    }

    @Override
    protected String getOriginalValueOfTheNode(CtBinaryOperator ctElement) {

        return ctElement.toString().replace("(", "").replace(")", "");
    }

    @Override
    protected Operators getOperatorByType(CtBinaryOperator ctElement) {
        return BinaryOperationMutator;
    }
}
