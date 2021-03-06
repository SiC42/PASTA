package edu.leipzig.grafs.operators.grouping.functions;

import org.gradoop.common.model.impl.properties.PropertyValue;
import org.gradoop.common.model.impl.properties.PropertyValueUtils;

/**
 * Base aggregate function for summation functions.
 */
public interface Sum extends AggregateFunction {

  @Override
  default PropertyValue aggregate(PropertyValue aggregate, PropertyValue increment) {
    return PropertyValueUtils.Numeric.add(aggregate, increment);
  }

}
