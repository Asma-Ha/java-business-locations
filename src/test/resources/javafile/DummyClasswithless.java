
package org.assertj.vavr.internal;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Condition;
import org.assertj.core.internal.Conditions;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Objects;

import java.util.function.Predicate;

import static io.vavr.Predicates.not;
import static java.util.Objects.deepEquals;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.error.ElementsShouldBe.elementsShouldBe;
import static org.assertj.core.error.ShouldContain.shouldContain;
import static org.assertj.core.error.ShouldContainAnyOf.shouldContainAnyOf;
import static org.assertj.core.error.ShouldContainExactly.elementsDifferAtIndex;
import static org.assertj.core.error.ShouldContainExactly.shouldContainExactly;
import static org.assertj.core.error.ShouldContainKeys.shouldContainKeys;
import static org.assertj.core.error.ShouldContainOnly.shouldContainOnly;
import static org.assertj.core.error.ShouldContainOnlyKeys.shouldContainOnlyKeys;
import static org.assertj.core.error.ShouldContainValue.shouldContainValue;
import static org.assertj.core.error.ShouldContainValues.shouldContainValues;
import static org.assertj.core.error.ShouldNotContain.shouldNotContain;
import static org.assertj.core.error.ShouldNotContainKeys.shouldNotContainKeys;
import static org.assertj.core.error.ShouldNotContainValue.shouldNotContainValue;
import static org.assertj.core.internal.Arrays.assertIsArray;
import static org.assertj.core.internal.CommonValidations.failIfEmptySinceActualIsNotEmpty;
import static org.assertj.core.internal.CommonValidations.hasSameSizeAsCheck;
import static org.assertj.core.util.Preconditions.checkArgument;
import static org.assertj.vavr.api.ShouldNotContainValues.shouldNotContainValues;

public class DummyClasswithless {
    public void dummyMethod() {
        final Multimap<K, V> notExpected = actual.filter((entry,j) -> !expectedEntries.contains(entry,j));
    }
}