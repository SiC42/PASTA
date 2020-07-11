package edu.leipzig.grafs.operators.grouping.logic;

import edu.leipzig.grafs.model.Edge;
import edu.leipzig.grafs.model.EdgeContainer;
import edu.leipzig.grafs.operators.grouping.model.AggregationMapping;
import edu.leipzig.grafs.operators.grouping.model.GroupingInformation;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;

public class EdgeAggregation<W extends Window> extends GraphElementAggregationProcess<W> {


  private final GroupingInformation edgeGroupInfo;
  private final AggregationMapping edgeAggregationMapping;

  public EdgeAggregation(GroupingInformation edgeGroupInfo,
      AggregationMapping edgeAggregationMapping) {
    if (edgeAggregationMapping != null && edgeGroupInfo != null) {
      checkAggregationAndGroupingKeyIntersection(edgeAggregationMapping, edgeGroupInfo);
    }
    this.edgeGroupInfo = edgeGroupInfo;
    this.edgeAggregationMapping = edgeAggregationMapping;
  }

  @Override
  public void process(String s, Context context, Iterable<EdgeContainer> ecIterable,
      Collector<EdgeContainer> out) {
    var aggregatedEdge = new Edge();

    int count = 0;
    EdgeContainer lastEc = null;

    for (var ec : ecIterable) {
      aggregatedEdge = (Edge) aggregateGraphElement(edgeAggregationMapping, aggregatedEdge,
          ec.getEdge());
      count++;
      lastEc = ec;
    }
    EdgeContainer aggregatedEContainer;

    // No need for aggregation when only one edge was "aggregated"
    if (count > 1) {
      // we have not set the grouped properties yet
      var source = lastEc.getSourceVertex();
      var target = lastEc.getTargetVertex();
      aggregatedEdge = (Edge) setGroupedProperties(edgeGroupInfo,
          aggregatedEdge,
          lastEc.getEdge());
      aggregatedEdge.setSourceId(source.getId());
      aggregatedEdge.setTargetId(target.getId());
      aggregatedEContainer = new EdgeContainer(aggregatedEdge, source,
          target);
    } else {
      aggregatedEContainer = lastEc;
    }

    out.collect(aggregatedEContainer);

  }

}
