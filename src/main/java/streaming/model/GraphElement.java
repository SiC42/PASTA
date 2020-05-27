package streaming.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.id.GradoopIdSet;
import org.gradoop.common.model.impl.properties.Properties;

public class GraphElement extends Element {

  private GradoopIdSet graphIds;

  /**
   * Default constructor.
   */
  protected GraphElement() {
    super();
  }

  protected GraphElement(GraphElement graphElement){
    super(graphElement);
  }

  /**
   * Creates an EPGM graph element using the given arguments.
   *  @param id         element id
   * @param label      element label
   * @param properties element properties
   * @param graphIds     graphIds that element is contained in
   */
  protected GraphElement(GradoopId id, String label,
      Map<String, String> properties, GradoopIdSet graphIds) {
    super(id, label, properties);
    this.graphIds = graphIds;
  }


  public GradoopIdSet getGraphIds() {
    return graphIds;
  }


  public void addGraphId(GradoopId graphId) {
    if (graphIds == null) {
      graphIds = new GradoopIdSet();
    }
    graphIds.add(graphId);
  }


  public void setGraphIds(GradoopIdSet graphIds) {
    this.graphIds = graphIds;
  }


  public void resetGraphIds() {
    if (graphIds != null) {
      graphIds.clear();
    }
  }


  public int getGraphCount() {
    return (graphIds != null) ? graphIds.size() : 0;
  }
}