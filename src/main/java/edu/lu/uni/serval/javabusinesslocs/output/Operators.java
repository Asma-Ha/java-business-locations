package edu.lu.uni.serval.javabusinesslocs.output;

/**
 * @see {https://github.com/rdegiovanni/mBERT}
 * @see {CodeBERTOperatorMutator#process(spoon.reflect.declaration.CtElement)}
 * @see {mBERT/src/main/java/operators}
 */
public enum Operators {
    LiteralMutator,
    ArrayMutator,
    AssignmentMutator,
    BinaryOperatorMutator,
    BinaryOperationMutator,
    FieldReferenceMutator,
    InvocationMutator,
    TypeReferenceMutator,
    UnaryOperatorMutator,
    UnaryOperationMutator,
    IfConditionReferenceLocation,
    LoopConditionLocation,
    ForConditionLocation,
    WhileConditionLocation,
    DoConditionLocation,
}
