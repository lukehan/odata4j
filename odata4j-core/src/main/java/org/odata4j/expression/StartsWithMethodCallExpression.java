package org.odata4j.expression;

public interface StartsWithMethodCallExpression extends BoolMethodExpression {

  CommonExpression getTarget();

  CommonExpression getValue();
}
