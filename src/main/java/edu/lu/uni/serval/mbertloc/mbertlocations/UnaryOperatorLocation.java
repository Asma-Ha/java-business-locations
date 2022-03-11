package edu.lu.uni.serval.mbertloc.mbertlocations;

import edu.lu.uni.serval.mbertloc.output.CodePosition;
import edu.lu.uni.serval.mbertloc.output.MBertOperators;

import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.support.reflect.cu.position.SourcePositionImpl;

import static edu.lu.uni.serval.mbertloc.output.MBertOperators.CodeBERTUnaryOperatorMutator;

public class UnaryOperatorLocation extends MBertLocation<CtUnaryOperator> {

    protected UnaryOperatorLocation(int firstMutantId, CtUnaryOperator ctElement) throws UnhandledElementException {
        super(firstMutantId, ctElement);
    }

    @Override
    protected CodePosition getCodePosition(CtUnaryOperator original) throws UnhandledElementException {
        int start = original.getPosition().getSourceStart();
        int end = original.getPosition().getSourceEnd();
        if (isPre(original.getKind()))
            end = original.getOperand().getPosition().getSourceStart() - 1;
        else
            start = original.getOperand().getPosition().getSourceEnd() + 1;

        CompilationUnit origUnit = original.getPosition().getCompilationUnit();
        SourcePosition position = new SourcePositionImpl(origUnit, start, end, origUnit.getLineSeparatorPositions());
        if (!position.isValidPosition()) return super.getCodePosition(original);
        return new CodePosition(position.getSourceStart(), position.getSourceEnd());
    }

    /**
     * @see {mbert.spoon.reflect.code.UnaryOperatorKind}
     * @param op
     * @return
     */
    private boolean isPre(UnaryOperatorKind op) {
        return op != UnaryOperatorKind.POSTDEC && op != UnaryOperatorKind.POSTINC;
    }

    @Override
    protected String getOriginalValueOfTheNode(CtUnaryOperator ctElement) {
        return ctElement.getKind().toString();
    }

    @Override
    protected MBertOperators getOperatorByType(CtUnaryOperator ctElement) {
        return CodeBERTUnaryOperatorMutator;
    }

}
