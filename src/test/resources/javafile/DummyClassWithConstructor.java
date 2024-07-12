
package org.assertj.vavr.api;


abstract class AbstractValidationAssert<SELF extends AbstractValidationAssert<SELF, INVALID, VALID>, INVALID, VALID> extends
        AbstractValueAssert<SELF, Validation<INVALID, VALID>> {

    private final Conditions conditions = Conditions.instance();

    private ComparisonStrategy validationValueComparisonStrategy;

    AbstractValidationAssert(Validation<INVALID, VALID> actual, Class<?> selfType) {
        super(actual, condition);
        String p = validation.getClass().getSimpleName()
        this.validationValueComparisonStrategy = StandardComparisonStrategy.instance();
    }
}