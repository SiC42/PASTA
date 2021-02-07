package edu.leipzig.grafs.operators.interfaces.window;

import edu.leipzig.grafs.model.BasicTriplet;
import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.model.window.WindowingInformation;
import edu.leipzig.grafs.model.window.WindowsI;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.OutputTag;

public interface WindowedOperatorI<W extends WindowsI<? extends Window>> {

  /**
   * Applies the given operator to the stream.
   *
   * @param stream stream on which the operator should be applied
   * @return the stream with the applied operator
   */
  <FW extends Window> DataStream<Triplet> execute(DataStream<Triplet> stream,
      WindowingInformation<FW> wi);

  default <FW extends Window, T extends BasicTriplet<?, ?>> WindowedStream<T, String, FW> applyOtherWindowInformation(
      WindowedStream<T, String, FW> windowedStream, WindowingInformation<FW> wi) {
    if (wi.getTrigger() != null) {
      windowedStream = windowedStream.trigger(wi.getTrigger());
    }
    if (wi.getEvictor() != null) {
      windowedStream = windowedStream.evictor(wi.getEvictor());
    }
    if (wi.getLateness() != null) {
      windowedStream = windowedStream.allowedLateness(wi.getLateness());
    }
    if (wi.getOutputTag() != null) {
      windowedStream = windowedStream.sideOutputLateData((OutputTag<T>) wi.getOutputTag());
    }
    return windowedStream;
  }

  default <FW extends Window, T extends BasicTriplet<?, ?>> AllWindowedStream<T, FW> applyOtherWindowInformation(
      AllWindowedStream<T, FW> windowedStream, WindowingInformation<FW> wi) {
    if (wi.getTrigger() != null) {
      windowedStream = windowedStream.trigger(wi.getTrigger());
    }
    if (wi.getEvictor() != null) {
      windowedStream = windowedStream.evictor(wi.getEvictor());
    }
    if (wi.getLateness() != null) {
      windowedStream = windowedStream.allowedLateness(wi.getLateness());
    }
    if (wi.getOutputTag() != null) {
      windowedStream = windowedStream.sideOutputLateData((OutputTag<T>) wi.getOutputTag());
    }
    return windowedStream;
  }

}