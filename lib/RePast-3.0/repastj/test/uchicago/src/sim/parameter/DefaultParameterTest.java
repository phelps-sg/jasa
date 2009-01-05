package uchicago.src.sim.parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.SimpleModel;
import uchicago.src.sim.parameter.ParameterSetter;
import uchicago.src.sim.parameter.ParameterSetterFactory;

/**
 * Tests DefaultParameter setter.
 */
public class DefaultParameterTest extends TestCase {

  public class TestSimModel extends SimpleModel {

    private int intA = 0;
    private String strB = "foo";
    private boolean booleanC = false;
    private float floatD = 3.21f;

    public TestSimModel() {
      this.params = new String[]{"IntA", "StrB", "BooleanC", "FloatD"};
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
  }

  private TestSimModel simModel;
  private ParameterSetter setter;

  public DefaultParameterTest(String name) {
    super(name);
  }

  public void setUp() {
    try {
      setter = ParameterSetterFactory.createParameterSetter("test/uchicago/src/sim/parameter/TestParams.txt");
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    simModel = new TestSimModel();
  }

  public void testSetParams() {
    setter.setModelParameters(simModel);
    assertEquals(10, simModel.getIntA());
    assertEquals("bob", simModel.getStrB());
    assertEquals(true, simModel.isBooleanC());
    assertEquals(2342.23423f, simModel.getFloatD(), 0);
  }

  public void testNextParams() {
    int runs = 0;
    int count = 1;
    int intExp = 10;
    boolean boolExp = true;
    String strExp = "bob";
    while (setter.hasNext()) {
      //System.out.println("count = " + count);
      setter.setNextModelParameters(simModel);

      if (!setter.hasNext()) break;
      assertEquals(2342.23423f, simModel.getFloatD(), 0);
      if (count % 2 == 0) {
        boolExp = false;
      } else
        boolExp = true;

      if (count == 3) strExp = "bill";
      if (count == 5) strExp = "cormac";

      if (count == 7) {
        intExp += 10;
        strExp = "bob";
        boolExp = true;
        count = 1;
      }

      assertEquals("count = " + count, intExp, simModel.getIntA());
      assertEquals("count = " + count, strExp, simModel.getStrB());
      assertEquals("count = " + count, boolExp, simModel.isBooleanC());

      count++;
      runs++;
    }

    assertEquals(18, runs);
  }

  public void testGetDynNames() {
    ArrayList list = setter.getDynamicParameterNames();
    assertEquals(4, list.size());
    assertTrue(list.contains("IntA"));
    assertTrue(list.contains("StrB"));
    assertTrue(list.contains("BooleanC"));
    assertTrue(list.contains("RngSeed"));
  }

  public void testIsParameter() {
    assertTrue(setter.isParameter("inta"));
    assertTrue(setter.isParameter("IntA"));

    assertTrue(!setter.isParameter("foo"));
  }

  public void testDefaultModelParameters() {
    Map m = setter.getDefaultModelParameters(simModel);
    assertEquals(5, m.size());

    // parameters return their value's as Strings.
    assertEquals(m.get("StrB"), "bob");
    assertEquals(m.get("IntA"), "10.0");
    assertEquals(m.get("BooleanC"), "true");
    assertEquals(m.get("FloatD"), "2342.23423");
  }

  public static junit.framework.Test suite() {
    return new TestSuite(DefaultParameterTest.class);
  }
}

