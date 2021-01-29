package edu.leipzig.grafs.benchmark.tests.nonwindow;

import edu.leipzig.grafs.benchmark.tests.AbstractBenchmark;
import edu.leipzig.grafs.model.streaming.nonwindow.AbstractNonWindowedStream;
import edu.leipzig.grafs.model.streaming.nonwindow.GraphStream;
import org.gradoop.common.model.impl.properties.PropertyValue;

public class VertexTransformationBenchmark extends AbstractBenchmark {

  public VertexTransformationBenchmark(String[] args) {
    super(args);
  }

  public static void main(String[] args) throws Exception {
    var benchmark = new VertexTransformationBenchmark(args);
    benchmark.execute();
  }

  public AbstractNonWindowedStream applyOperator(GraphStream stream) {
    return stream.transformEdges(e -> {
      e.setProperty("edgetransformed", PropertyValue.create(true));
      return e;
    });
  }

}
