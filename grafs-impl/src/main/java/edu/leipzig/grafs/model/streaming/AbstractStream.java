package edu.leipzig.grafs.model.streaming;

import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.model.window.WindowingInformation;
import edu.leipzig.grafs.model.window.WindowsI;
import edu.leipzig.grafs.operators.interfaces.window.WindowedOperatorI;
import edu.leipzig.grafs.util.FlinkConfig;
import java.io.IOException;
import java.util.Iterator;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.windows.Window;

public abstract class AbstractStream<S extends AbstractStream<?>> {


  protected final FlinkConfig config;
  protected DataStream<Triplet> stream;

  /**
   * Constructs an triplet stream with the given data stream and config.
   *
   * @param stream data stream that holds <tt>Triplet</tt>
   * @param config config used for the stream
   */
  public AbstractStream(DataStream<Triplet> stream, FlinkConfig config) {
    this.stream = stream.assignTimestampsAndWatermarks(config.getWatermarkStrategy());
    this.config = config;
  }

  /**
   * Returns the underlying data stream.
   *
   * @return the underlying data stream
   */
  public DataStream<Triplet> getDataStream() {
    return stream;
  }


  public <FW extends Window, W extends WindowsI<?>> S applyWindowedOperator(
      WindowedOperatorI<W> operatorI, WindowingInformation<?> wi) {
    stream = operatorI.execute(stream, wi);
    return getThis();
  }

  protected abstract S getThis();

  /**
   * Adds a sink function to the stream, which determines what should happen with the stream at the
   * end.
   * <p>
   * Only works once!
   *
   * @param sinkFunction The object containing the sink's invoke function.
   */
  public void addSink(SinkFunction<Triplet> sinkFunction) {
    stream.addSink(sinkFunction);
  }

  /**
   * Prints the stream to stdout.
   */
  public void print() {
    stream.print();
  }

  /**
   * Collects the stream into a iterator
   *
   * @return iterator of the stream content
   * @throws IOException
   */
  public Iterator<Triplet> collect() throws IOException {
    return DataStreamUtils.collect(stream);
  }

  public static class InitialWindowBuilder<S extends AbstractStream<S>, FW extends Window, W extends WindowsI<? extends FW>> {

    private final S stream;
    private final WindowedOperatorI<W> operator;

    public InitialWindowBuilder(S stream,
        WindowedOperatorI<W> operator) {

      this.stream = stream;
      this.operator = operator;
    }

    public <Wextension extends W> WindowBuilder<S, W, Wextension> withWindow(Wextension window) {
      return new WindowBuilder<>(stream, operator, window);
    }
  }

}
