package streaming.operators.grouping.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import streaming.model.EdgeContainer;
import streaming.operators.grouping.model.AggregateMode;
import streaming.operators.grouping.model.GroupingInformation;
import streaming.util.AsciiGraphLoader;

class EdgeKeySelectorTest {

  private EdgeContainer ec;

  public EdgeKeySelectorTest() {
    AsciiGraphLoader loader = AsciiGraphLoader.fromString(
        "(a18:v {n : \"A\", a : 18})," +
            "(b17:v {n : \"B\", a : 17})," +
            "(a18)-[e1:e {t: 5}]->(b17)"
    );
    Collection<EdgeContainer> edgeSet = loader.createEdgeContainers();
    ec = edgeSet.iterator().next();

  }

  @Test
  void getKey_forSourceVertex() {
    GroupingInformation gi = new GroupingInformation();
    gi.addKey("n");
    EdgeKeySelector eks = new EdgeKeySelector(gi, null, AggregateMode.SOURCE);
    assertThat(eks.getKey(ec), equalTo("({n:A})-[]->()"));
  }

  @Test
  void getKey_testNumberProperty() {
    GroupingInformation gi = new GroupingInformation();
    gi.addKey("a");
    EdgeKeySelector eks = new EdgeKeySelector(gi, null, AggregateMode.SOURCE);
    assertThat(eks.getKey(ec), equalTo("({a:18})-[]->()"));
  }

  @Test
  void getKey_testLabelForVertex() {
    GroupingInformation gi = new GroupingInformation();
    gi.useLabel(true);
    EdgeKeySelector eks = new EdgeKeySelector(gi, null, AggregateMode.SOURCE);
    assertThat(eks.getKey(ec), equalTo("(:v)-[]->()"));
  }

  @Test
  void getKey_forTargetVertex() {
    GroupingInformation gi = new GroupingInformation();
    gi.addKey("n");
    EdgeKeySelector eks = new EdgeKeySelector(gi, null, AggregateMode.TARGET);
    assertThat(eks.getKey(ec), equalTo("()-[]->({n:B})"));
  }

  @Test
  void getKey_forEdgeWithVertexGroupingInformation() {
    GroupingInformation vgi = new GroupingInformation();
    vgi.addKey("n");
    GroupingInformation egi = new GroupingInformation();
    egi.addKey("t");
    EdgeKeySelector eks = new EdgeKeySelector(vgi, egi, AggregateMode.EDGE);
    assertThat(eks.getKey(ec), equalTo("({n:A})-[{t:5}]->({n:B})"));
  }

  @Test
  void getKey_testLabelForEdge() {
    GroupingInformation vgi = new GroupingInformation();
    GroupingInformation egi = new GroupingInformation();
    egi.useLabel(true);
    EdgeKeySelector eks = new EdgeKeySelector(vgi, egi, AggregateMode.EDGE);
    assertThat(eks.getKey(ec), equalTo("()-[:e]->()"));
  }
}