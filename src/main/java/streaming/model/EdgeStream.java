package streaming.model;

import java.io.IOException;
import java.util.Iterator;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import streaming.operators.OperatorI;
import streaming.operators.grouping.Grouping;
import streaming.operators.grouping.model.AggregationMapping;
import streaming.operators.grouping.model.GroupingInformation;
import streaming.operators.subgraph.Subgraph;
import streaming.operators.subgraph.Subgraph.Strategy;
import streaming.operators.transform.EdgeTransformation;
import streaming.operators.transform.VertexTransformation;

public class EdgeStream {

  private DataStream<EdgeContainer> edgeStream;

  public EdgeStream(DataStream<EdgeContainer> edgeStream) {
    this.edgeStream = edgeStream.assignTimestampsAndWatermarks(
        new AscendingTimestampExtractor<EdgeContainer>() {
          @Override
          public long extractAscendingTimestamp(EdgeContainer edge) {
            return 0;
          }
        }
    );
  }

  public EdgeStream callForStream(OperatorI operator) {
    DataStream<EdgeContainer> result = operator.execute(edgeStream);
    return new EdgeStream(result);
  }

  public EdgeStream vertexInducedSubgraph(
      FilterFunction<Vertex> vertexGeiPredicate) {
    return callForStream(new Subgraph(vertexGeiPredicate, null, Strategy.VERTEX_INDUCED));
  }

  public EdgeStream edgeInducedSubgraph(FilterFunction<Edge> edgeGeiPredicate) {
    return callForStream(new Subgraph(null, edgeGeiPredicate, Strategy.EDGE_INDUCED));
  }

  public EdgeStream subgraph(FilterFunction<Vertex> vertexGeiPredicate,
      FilterFunction<Edge> edgeGeiPredicate) {
    return callForStream(new Subgraph(vertexGeiPredicate, edgeGeiPredicate, Strategy.BOTH));
  }

  public EdgeStream subgraph(FilterFunction<Vertex> vertexGeiPredicate,
      FilterFunction<Edge> edgeGeiPredicate, Strategy strategy) {
    return callForStream(new Subgraph(vertexGeiPredicate, edgeGeiPredicate, strategy));
  }

  public EdgeStream groupBy(GroupingInformation vertexEgi,
      AggregationMapping vertexAggregationFunctions,
      GroupingInformation edgeEgi, AggregationMapping edgeAggregationFunctions) {
    return callForStream(
        new Grouping(vertexEgi, vertexAggregationFunctions, edgeEgi, edgeAggregationFunctions));
  }

  public EdgeStream transformEdge(MapFunction<Edge, Edge> mapper) {
    return callForStream(new EdgeTransformation(mapper));
  }

  public EdgeStream transformVertices(MapFunction<Vertex, Vertex> mapper) {
    return callForStream(new VertexTransformation(mapper));
  }

  public void print() {
    edgeStream.print();
  }

  public Iterator<EdgeContainer> collect() throws IOException {
    return DataStreamUtils.collect(edgeStream);
  }
}