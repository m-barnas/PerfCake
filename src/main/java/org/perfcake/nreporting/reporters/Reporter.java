/*
 * Copyright 2010-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfcake.nreporting.reporters;

import java.util.Set;

import org.perfcake.RunInfo;
import org.perfcake.common.BoundPeriod;
import org.perfcake.common.Period;
import org.perfcake.nreporting.MeasurementUnit;
import org.perfcake.nreporting.ReportingException;
import org.perfcake.nreporting.destinations.Destination;

/**
 * Contract of a reporter. Reporter takes
 * multiple {@link org.perfcake.nreporting.MeasurementUnit Measurement Units} and combines
 * them into a single {@link org.perfcake.nreporting.Measurement Measurement}. The core method
 * is {@link #report(MeasurementUnit) report()} that is called each time a new measurement unit is ready.
 * Reporter should not report anything unless it has been started with the {@link #start() start()} method.
 * If it is properly started, it should regularly report to all
 * registered destinations depending on the configured reporting periods. Reporter can assume
 * that {@link org.perfcake.RunInfo RunInfo} has been set before calling
 * the {@link #start() start()} method. It is the pure responsibility of Reporter to publish measurement
 * results to destination in the configured periods. All period types must be supported.
 * For easier development, it is advised to inherit from {@link AbstractReporter} which provides
 * some common functionality including proper results publishing. One should directly implement
 * this interface only when there is a serious reason.
 * Reporter must be thread safe as it can be called from multiple threads at the same time.
 * 
 * @author Martin Večeřa <marvenec@gmail.com>
 * 
 */
public interface Reporter {

   /**
    * This method is called each time a new {@link org.perfcake.nreporting.MeasurementUnit Measurement Unit} is obtained. Each unit is reported once and only once to each of the reporters. The reporter is
    * not allowed to modify the Measurement Unit
    * 
    * @param mu
    *           Measurement Unit from a run iteration
    */
   public void report(MeasurementUnit mu) throws ReportingException;

   /**
    * Registers a destination to receive resulting {@link org.perfcake.nreporting.Measurement Measurements} in
    * a given period.
    * 
    * @param d
    *           The Destination to which the results should be published
    * 
    * @param p
    *           The period interval in which the destination should publish results
    */
   public void registerDestination(Destination d, Period p);

   /**
    * Registers a destination to receive resulting {@link org.perfcake.nreporting.Measurement Measurements} in
    * given periods. It is the goal of Reporter to make sure the
    * results are published to the registered destinations. For an easier development it is advised
    * to extend {@link AbstractReporter} which already takes care of this.
    * A destination cannot be registered with the same period type multiple times (i.e. one cannot register
    * a destination with a period of iteration type that reports every 10 iterations, and with a period of
    * iteration type that reports every 100 iterations at the same time).
    * 
    * @param d
    *           The Destination to which the results should be published
    * 
    * @param p
    *           The set of period intervals in which the destination should publish results
    */
   public void registerDestination(Destination d, Set<Period> p);

   /**
    * Removes a previously registered Destination. The method removes all occurrences of the destination
    * should it be registered with multiple periods. A Reporter should close the Destination if it is still open.
    * 
    * @param d
    *           The Destination to be unregistered (and stopped)
    */
   public void unregisterDestination(Destination d);

   /**
    * Gets an unmodifiable list of all registered destinations.
    * 
    * @return An unmodifiable list of all currently registered destinations.
    */
   public Set<Destination> getDestinations();

   /**
    * Starts the reporter. After a call to this method, reporter will report measurement results to the
    * registered destinations. All destinations are started as well.
    */
   public void start();

   /**
    * Stops the reporter. After a call to this method, no more results will be reported to the destinatons.
    * All destinations are stopped as well.
    */
   public void stop();

   /**
    * Resets the reporter statistics to the initial state. This is mainly used for clean up after a warm-up period.
    */
   public void reset();

   /**
    * Sets {@link org.perfcake.RunInfo Run Info} for the current measurement run. This must be set
    * prior to starting the reporter. Failed to do so can lead to an assertion error.
    * 
    * @param runInfo
    *           RunInfo for the current measurement run
    */
   public void setRunInfo(RunInfo runInfo);

   /**
    * Gets an unmodifiable set of registered reporting periods.
    * 
    * @return The unmodifiable set of registered reporting periods.
    */
   public Set<BoundPeriod<Destination>> getReportingPeriods();

}
