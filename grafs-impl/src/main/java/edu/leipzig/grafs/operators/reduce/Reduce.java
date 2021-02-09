package edu.leipzig.grafs.operators.reduce;

import edu.leipzig.grafs.model.Edge;
import edu.leipzig.grafs.model.GraphElement;
import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.model.Vertex;
import edu.leipzig.grafs.operators.interfaces.nonwindow.GraphCollectionToGraphOperatorI;
import java.util.function.Consumer;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.id.GradoopIdSet;

/**
 * Represents a Subgraph Operator. A subgraph is a graph, whose vertices and edges are subsets of
 * the given graph.
 * <p>
 * The operator is able to:
 * <ol>
 *   <li>extract vertex-induced subgraph</li>
 *   <li>extract edge-induced subgraph</li>
 *   <li>extract subgraph based on vertex and edge filter function</li>
 * </ol>
 */
public class Reduce implements GraphCollectionToGraphOperatorI {


  protected FilterFunction<Triplet<Vertex, Edge>> tripletFilter;
  protected GradoopId newId;


  public Reduce(final FilterFunction<GradoopIdSet> idSetFilter) {
    this.tripletFilter = triplet ->
        idSetFilter.filter(triplet.getEdge().getGraphIds()) &&
            idSetFilter.filter(triplet.getSourceVertex().getGraphIds()) &&
            idSetFilter.filter(triplet.getTargetVertex().getGraphIds());
    this.newId = GradoopId.get();
  }

  /**
   * Applies this operator on the stream and returns the stream with the operator applied.
   *
   * @param stream stream on which the operator should be applied
   * @return the stream with the subgraph operator applied
   */
  @Override
  public DataStream<Triplet<Vertex, Edge>> execute(DataStream<Triplet<Vertex, Edge>> stream) {
    Consumer<GraphElement> setGraphIds = ge -> ge.setGraphIds(GradoopIdSet.fromExisting(newId));
    return stream.filter(tripletFilter)
        .map(triplet -> {
          setGraphIds.accept(triplet.getEdge());
          setGraphIds.accept(triplet.getSourceVertex());
          setGraphIds.accept(triplet.getTargetVertex());
          return triplet;
        })
        .name("Reduce Operator");
  }

}
