package org.baylorschool.library;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * This class helps count and send telemetry about how frequently a loop iteration is executed.
 * The frequency is measured in Ticks Per Second (TPS).
 */
public class ExecutionFrequency {
    private final Telemetry telemetry;
    private final String telemetryWord;

    private long lastExecution;

    /**
     * Creates instance of ExecutionFrequency
     * @param telemetry Telemetry instance. Can be null, but some methods become unavailable if it is.
     */
    public ExecutionFrequency(Telemetry telemetry) {
        this.telemetry = telemetry;
        this.telemetryWord = "TPS";

        this.lastExecution = 0;
    }

    /**
     * Creates instance of ExecutionFrequency
     * @param telemetry Telemetry instance. Can be null, but some methods become unavailable if it is.
     * @param telemetryWord A string containing the word to be used as telemetry caption.
     */
    public ExecutionFrequency(Telemetry telemetry, String telemetryWord) {
        this.telemetry = telemetry;
        this.telemetryWord = telemetryWord;

        this.lastExecution = 0;
    }

    // TPS = Ticks Per Second

    /**
     * Measures the current time in nanoseconds, compares with the previous execution, and computes
     * how many times the loop is executed per second. It also adds the value to telemetry.
     *
     * If the program already knows a relative, continuously increasing time in nanoseconds, use
     * the overloaded method execution(long time).
     * @return TPS: How many times the loop is executed per second based on the previous execution time.
     */
    public float execution() {
        return execution(System.nanoTime());
    }

    /**
     * Measures the current time in nanoseconds, compares with the previous execution, and computes
     * how many times the loop is executed per second. It also adds the value to telemetry.
     *
     * @param time Current time in nanoseconds. This param is intended to avoid calling System.nanoTime()
     *             more than necessary.
     * @return TPS: How many times the loop is executed per second based on the previous execution time.
     */
    public float execution(long time) {
        float tps = executionNoTelemetry(time);
        telemetry.addData("TPS", tps);
        return tps;
    }

    /**
     * Measures the current time in nanoseconds, compares with the previous execution, and computes
     * how many times the loop is executed per second.
     *
     * If the program already knows a relative, continuously increasing time in nanoseconds, use
     * the overloaded method executionNoTelemetry(long time).
     * @return TPS: How many times the loop is executed per second based on the previous execution time.
     */
    public float executionNoTelemetry() {
        return executionNoTelemetry(System.nanoTime());
    }

    /**
     * Measures the current time in nanoseconds, compares with the previous execution, and computes
     * how many times the loop is executed per second.
     *
     * @param time Current time in nanoseconds. This param is intended to avoid calling System.nanoTime()
     *             more than necessary.
     * @return TPS: How many times the loop is executed per second based on the previous execution time.
     */
    public float executionNoTelemetry(long time) {
        if (lastExecution == 0 || lastExecution == time) { // lastExecution == time is checked to avoid dividing by 0.
            lastExecution = time;
            return 1f;
        } else {
            int diff = (int) (time - lastExecution);
            lastExecution = time;
            return (1_000_000f / diff) * 1000f;
        }
    }

}
