package uchicago.src.sim.parameter;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.SimpleModel;
import uchicago.src.sim.parameter.ParameterSetter;
import uchicago.src.sim.parameter.ParameterSetterFactory;

/**
 * Tests RPLParameterSetter
 */
public class RPLParameterTest extends TestCase {

  public class TestSimModel extends SimpleModel {

    public static final int INT_ONE = 1;
    public static final int INT_FIVE = 5;

    private int intA = -1;
    private String strB = "foo";
    private boolean booleanC = false;
    private float floatD = -1;
    private double doubleE = -1;
    private int fooInt = 123;

    public TestSimModel() {
      this.params = new String[]{"IntA", "StrB", "BooleanC", "FloatD",
                                 "DoubleE", "FooInt"};
    }

    public int getIntA() {
      return intA;
    }

    public void setIntA(int intA) {
      this.intA = intA;
    }

    public String getStrB() {
      return strB;
    }

    public void setStrB(String strB) {
      this.strB = strB;
    }

    public boolean isBooleanC() {
      return booleanC;
    }

    public void setBooleanC(boolean booleanC) {
      this.booleanC = booleanC;
    }

    public float getFloatD() {
      return floatD;
    }

    public void setFloatD(float floatD) {
      this.floatD = floatD;
    }

    public double getDoubleE() {
      return doubleE;
    }

    public void setDoubleE(double doubleE) {
      this.doubleE = doubleE;
    }

    public int getFooInt() {
      return fooInt;
    }

    public void setFooInt(int fooInt) {
      this.fooInt = fooInt;
    }

    public void setup() {
      //super.setup();
      strB = "foo";
      doubleE = -1;
      booleanC = true;
      floatD = -1;
      intA = -1;
      fooInt = 123;
    }
  }

  private TestSimModel simModel;
  private ParameterSetter setter;

  public RPLParameterTest(String name) {
    super(name);
  }

  public void setUp() {
    try {
      setter = ParameterSetterFactory.createParameterSetter("test/uchicago/src/sim/parameter/test.rpl");
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    simModel = new TestSimModel();
  }

  public void testIsConstant() {
    boolean val = setter.isConstant("BooleanC");
    assertTrue(val);

    val = setter.isConstant("booleanc");
    assertTrue(val);

    val = setter.isConstant("DoubleE");
    assertTrue(!val);
  }

  public void testIsParameter() {
    boolean val = setter.isParameter("DoubleE");
    assertTrue(val);

    val = setter.isParameter("doubleE");
    assertTrue(val);

    val = setter.isParameter("booleanc");
    assertTrue(val);
  }

  public void testGetDynamicParameterNames() {
    List list = setter.getDynamicParameterNames();
    //"IntA", "StrB", "BooleanC", "FloatD", "DoubleE"
    assertTrue(list.contains("IntA"));
    assertTrue(list.contains("StrB"));
    assertTrue(list.contains("FloatD"));
    assertTrue(list.contains("DoubleE"));
    assertTrue(!list.contains("BooleanC"));
  }

  public void testSetModelParams() {
    setter.setModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(TestSimModel.INT_ONE, simModel.getIntA());
    assertEquals("Cormac", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
  }

  public void testNext() {
    boolean next = setter.hasNext();
    assertTrue(next);

    // iterates through string list
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(TestSimModel.INT_ONE, simModel.getIntA());
    assertEquals("Cormac", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(TestSimModel.INT_ONE, simModel.getIntA());
    assertEquals("Nick", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(TestSimModel.INT_ONE, simModel.getIntA());
    assertEquals("Caitrin", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    // iterates through DoubleD and strB now == foo
    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(1, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(1, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1.5, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(1, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(2.0, simModel.getDoubleE(), 0);


    // intA incremented
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(2, simModel.getIntA());
    assertEquals("Cormac", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(2, simModel.getIntA());
    assertEquals("Nick", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(2, simModel.getIntA());
    assertEquals("Caitrin", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    // iterates through DoubleD and strB now == foo
    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(2, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(2, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1.5, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(2, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(2.0, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    // floatD == 3 and run through strB everything else ignored so
    // setup values apply
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(-1, simModel.getIntA());
    assertEquals("Cormac", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(true, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(-1, simModel.getIntA());
    assertEquals("Nick", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(true, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(3, simModel.getFloatD(), 0f);
    assertEquals(-1, simModel.getIntA());
    assertEquals("Caitrin", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(true, simModel.isBooleanC());


    // floatD = -21.1 and run through block
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(TestSimModel.INT_ONE, simModel.getIntA());
    assertEquals("Cormac", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(TestSimModel.INT_ONE, simModel.getIntA());
    assertEquals("Nick", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(TestSimModel.INT_ONE, simModel.getIntA());
    assertEquals("Caitrin", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    // iterates through DoubleD and strB now == foo
    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(1, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(1, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1.5, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(1, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(2.0, simModel.getDoubleE(), 0);


    // intA incremented
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(2, simModel.getIntA());
    assertEquals("Cormac", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(2, simModel.getIntA());
    assertEquals("Nick", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(2, simModel.getIntA());
    assertEquals("Caitrin", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);

    // iterates through DoubleD and strB now == foo
    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(2, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1, simModel.getDoubleE(), 0);
    assertEquals(false, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(2, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(1.5, simModel.getDoubleE(), 0);

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(2, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(2.0, simModel.getDoubleE(), 0);

     // floatD == -21.2 and run through strB everything else ignored so
    // setup values apply
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(-1, simModel.getIntA());
    assertEquals("Cormac", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(true, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(-1, simModel.getIntA());
    assertEquals("Nick", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(true, simModel.isBooleanC());

    assertTrue(setter.hasNext());
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-21.2, simModel.getFloatD(), .1f);
    assertEquals(-1, simModel.getIntA());
    assertEquals("Caitrin", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(true, simModel.isBooleanC());

    // last one but nothing is set so setup vals should apply
    simModel.setup();
    setter.setNextModelParameters(simModel);
    assertEquals(-1, simModel.getFloatD(), .1f);
    assertEquals(-1, simModel.getIntA());
    assertEquals("foo", simModel.getStrB());
    assertEquals(-1, simModel.getDoubleE(), 0);
    assertEquals(true, simModel.isBooleanC());

    assertTrue(!setter.hasNext());
  }

  public void testGetParam() {
    simModel.setup();
    Object a = setter.getParameterValue("IntA", simModel);
    assertEquals(TestSimModel.INT_ONE, ((Integer)a).intValue());
    a = setter.getParameterValue("BooleanC", simModel);
    assertEquals(Boolean.FALSE, a);
  }

  public void testGetDefaultModelParams() {
    simModel.setup();
    Hashtable h = setter.getDefaultModelParameters(simModel);
    assertEquals(new Integer(TestSimModel.INT_ONE), h.get("IntA"));
    System.out.println(h.get("FloatD").getClass());
    assertEquals(new Float(3), h.get("FloatD"));
    assertEquals(new String("Cormac"), h.get("StrB"));
    assertEquals(new Double(1.0), h.get("DoubleE"));
    assertEquals(Boolean.FALSE, h.get("BooleanC"));
    assertTrue(h.containsKey("RngSeed"));
    assertEquals(new Integer(123), h.get("FooInt"));
  }



  public static junit.framework.Test suite() {
    return new TestSuite(RPLParameterTest.class);
  }
}

