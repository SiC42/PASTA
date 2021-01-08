package edu.leipzig.grafs.operators.matching.logic;

import edu.leipzig.grafs.model.Triplet;
import edu.leipzig.grafs.model.Graph;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * Filters vertices based on query graph.
 */
public class VertexQueryFilter implements FilterFunction<Triplet> {

  /**
   * Query graph which is used to filter edges
   */
  private final Graph queryGraph;

  /**
   * Initializes the filter.
   *
   * @param queryGraph query graph which is used to filter edges
   */
  public VertexQueryFilter(final Graph queryGraph) {
    this.queryGraph = queryGraph;
  }

  /**
   * Returns <tt>true</tt> if for the given triplet there are matching vertices in the query
   * graph.
   *
   * @param triplet Triplet which is tested for filtering
   * @return <tt>true</tt> if for the given triplet there are matching vertices in the query
   * graph
   */
  @Override
  public boolean filter(Triplet triplet) throws Exception {
    var source = triplet.getSourceVertex();
    var target = triplet.getTargetVertex();
    boolean sourceInQuery = false;
    boolean targetInQuery = false;
    for (var queryVertex : queryGraph.getVertices()) {
      sourceInQuery = sourceInQuery || ElementMatcher.matchesQueryElem(queryVertex, source);
      targetInQuery = targetInQuery || ElementMatcher.matchesQueryElem(queryVertex, target);
      if (sourceInQuery && targetInQuery) {
        return true;
      }
    }
    return false;
  }
}
