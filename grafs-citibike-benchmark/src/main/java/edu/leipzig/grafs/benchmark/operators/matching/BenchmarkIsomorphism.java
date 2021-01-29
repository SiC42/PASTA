package edu.leipzig.grafs.benchmark.operators.matching;

import edu.leipzig.grafs.benchmark.operators.functions.SimpleMeter;
import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.model.streaming.window.AbstractWindowedStream.WindowInformation;
import edu.leipzig.grafs.operators.matching.Isomorphism;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.windows.Window;

public class BenchmarkIsomorphism extends Isomorphism {

  private final String meterName;

  public BenchmarkIsomorphism(String query) {
    this(query, "isomorphismMeter");
  }

  public BenchmarkIsomorphism(String query, String meterName) {
    super(query);
    this.meterName = meterName;
  }

  @Override
  public <W extends Window> DataStream<Triplet> execute(DataStream<Triplet> stream,
      WindowInformation<W> wi) {
    return super.execute(stream, wi).map(new SimpleMeter<>(meterName));
  }
}
