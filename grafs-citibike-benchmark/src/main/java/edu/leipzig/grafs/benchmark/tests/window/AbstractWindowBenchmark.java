package edu.leipzig.grafs.benchmark.tests.window;

import edu.leipzig.grafs.benchmark.tests.AbstractBenchmark;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

public abstract class AbstractWindowBenchmark extends AbstractBenchmark {

  Time windowSize;
  WindowAssigner<Object, TimeWindow> window;
  boolean useTrigger;
  Trigger<Object, TimeWindow> countTrigger;

  public AbstractWindowBenchmark(String[] args) {
    super(args);
    checkArgs(args);
  }

  private void checkArgs(String[] args) {
    var parser = new DefaultParser();
    var options = buildOptions();
    var header = String.format("Benchmarking GRAFS with %s.", operatorName);
    HelpFormatter formatter = new HelpFormatter();
    try {
      var cmd = parser.parse(options, args);
      if (cmd.hasOption("windowsize") && cmd.hasOption("triggersize")) {
        throw new ParseException("Error. You cannot set windowsize and triggersize");
      }
      if (cmd.hasOption("windowsize")) {
        try {
          this.useTrigger = false;
          int windowSizeInMs = Integer.parseInt(cmd.getOptionValue("windowsize"));
          this.windowSize = Time.seconds(windowSizeInMs);
          this.window = TumblingProcessingTimeWindows.of(windowSize);
          this.operatorName += "-" + "tumblingwindow-" + windowSizeInMs;
        } catch (NumberFormatException e) {
          throw new ParseException("Error. argument after windowsize is not an integer.");
        }
      }
      if (cmd.hasOption("triggersize")) {
        int triggerSize;
        try {
          triggerSize = Integer.parseInt(cmd.getOptionValue("triggersize"));
        } catch (NumberFormatException e) {
          throw new ParseException("Error. argument after triggersize is not an integer.");
        }
        this.useTrigger = true;
        this.windowSize = Time.days(5); // obsolete, as the trigger will fire instead
        this.window = TumblingProcessingTimeWindows.of(windowSize);
        this.countTrigger = CountTrigger.of(triggerSize);
        this.operatorName += "-" + "counttrigger-" + triggerSize;
      }
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      formatter.printHelp("grafsbenchmark", header, options, "");

      System.exit(1);
    }
  }

  protected Options buildOptions() {
    var options = super.buildOptions();
    options.addOption("ws", "windowsize", true, "size of the window in ms");
    options.addOption("ts", "triggersize", true, "number of elements trigger closing window");
    return options;
  }
}
