package uchicago.src.sim.engine;

import uchicago.src.sim.engine.Schedule;

public class ScheduleTestAgent {

  int id;
  String fired = "";
  Schedule sch;

  public ScheduleTestAgent(int id, Schedule s) {
    sch = s;
    this.id = id;
  }

  public void printId() {
    fired += (sch.getCurrentTime()) + " ";
    //System.out.print(id + " ");
  }

  public int getId() {
    return id;
  }

  public String getTicks() {
    return fired;
  }
}
