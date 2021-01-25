package edu.leipzig.grafs.model.streaming;

import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.operators.interfaces.nonwindow.GraphCollectionToGraphCollectionOperatorI;
import edu.leipzig.grafs.operators.interfaces.nonwindow.GraphCollectionToGraphOperatorI;
import edu.leipzig.grafs.util.FlinkConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.windows.Window;

public class GCStream extends AbstractNonWindowStream implements GCStreamOperators {

  /**
   * Constructs a triplet stream with the given data stream and config.
   *
   * @param stream data stream that holds <tt>Triplet</tt>
   * @param config     config used for the stream
   */
  public GCStream(
      DataStream<Triplet> stream, FlinkConfig config) {
    super(stream, config);
  }

  @Override
  public GraphStream callForGraph(GraphCollectionToGraphOperatorI operator) {
    DataStream<Triplet> result = operator.execute(stream);
    return new GraphStream(result, config);
  }

  @Override
  public GCStream callForGC(GraphCollectionToGraphCollectionOperatorI operator) {
    DataStream<Triplet> result = operator.execute(stream);
    return new GCStream(result, config);
  }

  public <W extends Window> WindowedGCStream<W> window(WindowAssigner<? super Triplet, W> window) {
    return new WindowedGCStream<>(stream, config, window);
  }
}
