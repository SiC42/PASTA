package edu.leipzig.grafs.benchmark.tests.nonwindow;

import edu.leipzig.grafs.benchmark.tests.AbstractBenchmark;
import edu.leipzig.grafs.model.streaming.nonwindow.AbstractNonWindowedStream;
import edu.leipzig.grafs.model.streaming.nonwindow.GraphStream;

public class VertexInducedSubgraphBenchmark extends AbstractBenchmark {

  public VertexInducedSubgraphBenchmark(String[] args) {
    super(args);
  }

  public static void main(String[] args) throws Exception {
    var benchmark = new VertexInducedSubgraphBenchmark(args);
    benchmark.execute();
  }

  public AbstractNonWindowedStream applyOperator(GraphStream stream) {
    return stream
        .vertexInducedSubgraph(v -> !v.hasProperty("name"));
  }

}
