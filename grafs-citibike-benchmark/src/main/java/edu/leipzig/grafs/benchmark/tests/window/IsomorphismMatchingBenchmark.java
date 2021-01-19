package edu.leipzig.grafs.benchmark.tests.window;

import edu.leipzig.grafs.model.streaming.AbstractStream;
import edu.leipzig.grafs.model.streaming.WindowedGraphStream;
import org.apache.flink.streaming.api.windowing.windows.Window;

public class IsomorphismMatchingBenchmark extends AbstractWindowBenchmark {

  public IsomorphismMatchingBenchmark(String[] args) {
    super(args);
  }

  public static void main(String[] args) throws Exception {
    var benchmark = new IsomorphismMatchingBenchmark(args);
    benchmark.execute();
  }

  public <W extends Window> AbstractStream applyOperator(WindowedGraphStream<W> stream) {
    var query = "(v1)-[]->(v2)-[]->(v1)";
    return stream.isomorphismMatching(query);
  }

}
