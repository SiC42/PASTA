package edu.leipzig.grafs.operators.subgraph;

import edu.leipzig.grafs.model.Edge;
import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.model.Vertex;
import edu.leipzig.grafs.operators.interfaces.nonwindow.GraphToGraphOperatorI;
import java.util.Objects;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

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
public class Subgraph implements GraphToGraphOperatorI {

  /**
   * Filter used to make a subgraph.
   */
  protected FilterFunction<Triplet<Vertex, Edge>> tripletFilter;

  private Strategy strategy;

  /**
   * Empty constructor for serialization.
   */
  protected Subgraph() {
    tripletFilter = null;
  }

  /**
   * Initializes this operator with the given parameters
   *
   * @param vertexFilter filter applied to the vertices of the stream
   * @param edgeFilter   filter applied to the edges of the stream
   * @param strategy     strategy used for the subgraph-operation
   */
  public Subgraph(
      final FilterFunction<Vertex> vertexFilter,
      final FilterFunction<Edge> edgeFilter,
      final Strategy strategy) {
    Objects.requireNonNull(strategy);
    this.strategy = strategy;

    switch (strategy) {
      case BOTH:
        if (vertexFilter == null || edgeFilter == null) {
          throw new IllegalArgumentException("No vertex or no edge filter function was given.");
        }
        tripletFilter = createSubGraphFilter(vertexFilter, edgeFilter);
        break;
      case VERTEX_INDUCED:
        if (vertexFilter == null) {
          throw new IllegalArgumentException("No vertex filter functions was given.");
        }
        tripletFilter = createVertexInducedSubgraphFilter(vertexFilter);
        break;
      case EDGE_INDUCED:
        if (edgeFilter == null) {
          throw new IllegalArgumentException("No vertex edge functions was given.");
        }
        tripletFilter = createEdgeInducedSubgraphFilter(edgeFilter);
        break;
      default:
        throw new IllegalArgumentException("Strategy " + strategy + " is not supported");
    }
  }

  /**
   * Creates a {@link FilterFunction} on Triplets for the given vertex and edge filter. The returned
   * filter is the one applied to the stream.
   *
   * @param vertexFilter filter applied to the vertices of the stream
   * @param edgeFilter   filter applied to the edges of the stream
   * @return triplet filter ready to be applied to the stream
   */
  private FilterFunction<Triplet<Vertex, Edge>> createSubGraphFilter(
      FilterFunction<Vertex> vertexFilter,
      FilterFunction<Edge> edgeFilter) {
    return triplet ->
        edgeFilter.filter(triplet.getEdge()) &&
            vertexFilter.filter(triplet.getSourceVertex()) &&
            vertexFilter.filter(triplet.getTargetVertex());
  }

  /**
   * Creates a {@link FilterFunction} on Triplets for the given vertex filter that represents a
   * vertex induced subgraph function. The returned filter is the one applied to the stream.
   *
   * @param vertexFilter filter applied to the vertices of the stream
   * @return vertex induced subgraph filter ready to be applied to the stream
   */
  private FilterFunction<Triplet<Vertex, Edge>> createVertexInducedSubgraphFilter(
      FilterFunction<Vertex> vertexFilter) {
    return triplet ->
        vertexFilter.filter(triplet.getSourceVertex()) && vertexFilter
            .filter(triplet.getTargetVertex());
  }

  /**
   * Creates a {@link FilterFunction} on Triplets for the given edge filter that represents a edge
   * induced subgraph function. The returned filter is the one applied to the stream.
   *
   * @param edgeFilter filter applied to the vertices of the stream
   * @return edge induced subgraph filter ready to be applied to the stream
   */
  private FilterFunction<Triplet<Vertex, Edge>> createEdgeInducedSubgraphFilter(
      FilterFunction<Edge> edgeFilter) {
    return triplet -> edgeFilter.filter(triplet.getEdge());
  }

  /**
   * Applies this operator on the stream and returns the stream with the operator applied.
   *
   * @param stream stream on which the operator should be applied
   * @return the stream with the subgraph operator applied
   */
  @Override
  public DataStream<Triplet<Vertex, Edge>> execute(DataStream<Triplet<Vertex, Edge>> stream) {
    return stream.filter(tripletFilter).name(strategy.name().toLowerCase() + " subgraph");
  }

  /**
   * Available execution strategies.
   */
  public enum Strategy {
    /**
     * Applies both filter functions on the input vertex and edge data set.
     */
    BOTH,
    /**
     * Only applies the vertex filter function and adds the incident edges connecting those vertices
     * via a join.
     */
    VERTEX_INDUCED,
    /**
     * Only applies the edge filter function and computes the resulting vertices via:<br> {@code (E
     * |><| V ON e.source = v.id) U (E |><| V on e.target = v.id)}
     */
    EDGE_INDUCED
  }

}
