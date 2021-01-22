package edu.leipzig.grafs.model.streaming;

import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.util.FlinkConfig;
import java.io.IOException;
import java.util.Iterator;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public abstract class AbstractStream implements StreamI{


  protected final DataStream<Triplet> edgeStream;
  protected final FlinkConfig config;

  /**
   * Constructs an triplet stream with the given data stream and config.
   *
   * @param edgeStream data stream that holds <tt>Triplet</tt>
   * @param config     config used for the stream
   */
  public AbstractStream(DataStream<Triplet> edgeStream, FlinkConfig config) {
    this.edgeStream = edgeStream.assignTimestampsAndWatermarks(config.getWatermarkStrategy());
    this.config = config;
  }

  /**
   * Constructs an triplet stream using the given kafka consumer and stream config.
   *
   * @param fkConsumer kafka consumer from which the information are fetched
   * @param config     config used for the stream
   * @return
   */
  public static GraphStream fromSource(FlinkKafkaConsumer<Triplet> fkConsumer,
      FlinkConfig config, int parallelism) {
    DataStreamSource<Triplet> stream;
    if(parallelism > 0) {
      stream = config.getExecutionEnvironment().addSource(fkConsumer).setParallelism(parallelism);
    } else{
      stream = config.getExecutionEnvironment().addSource(fkConsumer);
    }

    return new GraphStream(stream, config);
  }

  /**
   * Adds a sink function to the stream, which determines what should happen with the stream at the
   * end.
   * <p>
   * Only works once!
   *
   * @param sinkFunction The object containing the sink's invoke function.
   */
  public void addSink(SinkFunction<Triplet> sinkFunction) {
    edgeStream.addSink(sinkFunction);
  }

  /**
   * Returns the underlying data stream.
   *
   * @return the underlying data stream
   */
  public DataStream<Triplet> getDataStream() {
    return edgeStream;
  }

  /**
   * Prints the stream to stdout.
   */
  public void print() {
    edgeStream.print();
  }

  /**
   * Collects the stream into a iterator
   *
   * @return iterator of the stream content
   * @throws IOException
   */
  public Iterator<Triplet> collect() throws IOException {
    return DataStreamUtils.collect(edgeStream);
  }

}
