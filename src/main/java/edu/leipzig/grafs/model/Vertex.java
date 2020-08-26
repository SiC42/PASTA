package edu.leipzig.grafs.model;

import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.id.GradoopIdSet;
import org.gradoop.common.model.impl.properties.Properties;

public class Vertex extends GraphElement {


  public Vertex() {
    super();
  }


  /**
   * Creates a vertex based on the given parameters.
   *
   * @param id         vertex identifier
   * @param label      vertex label
   * @param properties vertex properties
   * @param graphIds   graphs that vertex is contained in
   */
  public Vertex(final GradoopId id, final String label,
      final Properties properties, final GradoopIdSet graphIds) {
    super(id, label, properties, graphIds);
  }


  @Override
  public String toString() {
    return String.format("(%s)", super.toString());
  }
}
