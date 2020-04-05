package streaming.operators;

import org.apache.flink.api.java.functions.KeySelector;
import streaming.model.Edge;
import streaming.model.grouping.ElementGroupingInformation;
import streaming.model.GraphElementInformation;

import java.util.Set;

public class EdgeKeySelector implements KeySelector<Set<Edge>, String> {

    private ElementGroupingInformation vertexEgi;
    private ElementGroupingInformation edgeEgi;
    private AggregateMode makeKeyFor;

    public EdgeKeySelector(ElementGroupingInformation vertexEgi, ElementGroupingInformation edgeEgi, AggregateMode makeKeyFor) {
        this.vertexEgi = vertexEgi;
        this.edgeEgi = edgeEgi;
        this.makeKeyFor = makeKeyFor;
    }

    @Override
    public String getKey(Set<Edge> edgeSet) {
        Edge edge = edgeSet.iterator().next();
        switch (makeKeyFor) {

            case SOURCE:
                return generateKey(edge.getSource().getGei(), edge.getGei(), vertexEgi, edgeEgi);
            case TARGET:
                return generateKey(edge.getTarget().getGei(), edge.getGei(), vertexEgi, edgeEgi);
            case EDGE:
                return null;
        }
        return null;
    }

    private String generateKey(GraphElementInformation vertexGei, GraphElementInformation
            edgeGei, ElementGroupingInformation vertexEgi, ElementGroupingInformation edgeEgi) {
        StringBuilder sb = new StringBuilder();

        // Build Key based on Vertex Information
        sb = keyStringBuilder(sb, vertexGei, vertexEgi, "Vertex");

        // Build key based on Edge-Information
        if (edgeEgi != null) {
            sb = keyStringBuilder(sb, edgeGei, edgeEgi, "Edge");
        }

        return sb.toString();
    }

    private StringBuilder keyStringBuilder(StringBuilder sb, GraphElementInformation
            gei, ElementGroupingInformation egi, String elementStr) {
        sb.append(elementStr).append("-Grouping-Information:(");
        if (egi.shouldGroupByLabel) {
            sb.append(String.format("label:%s ", gei.getLabel()));
        }
        if (egi.shouldGroupByMembership) {
            sb.append(String.format("membership:%s ", gei.getMemberships().toString()));
        }
        if (!egi.groupingKeys.isEmpty()) {
            sb.append("properties:{");
            for (String key : egi.groupingKeys) {
                sb.append(String.format("(%s:%s) ", key, gei.getProperty(key)));
            }
            sb.append("}");
        }
        sb.append(")");
        return sb;
    }
}
