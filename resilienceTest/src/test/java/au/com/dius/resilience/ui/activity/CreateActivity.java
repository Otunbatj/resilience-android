package au.com.dius.resilience.ui.activity;

import android.view.View;
import android.widget.ListView;
import au.com.dius.resilience.AbstractResilienceTestCase;
import au.com.dius.resilience.R;
import com.jayway.android.robotium.solo.Solo;

import java.util.ArrayList;

/**
 * @author georgepapas
 */
public class CreateActivity extends AbstractResilienceTestCase<ResilienceActivity> {

  public CreateActivity() {
    super(ResilienceActivity.class);
  }

  @Override
  protected void beforeTest() {
  }

  @Override
  protected void afterTest() {
  }

  public void testCreateIncident() {

    final int incidentsBefore = getNoOfIncidentsInList();
    solo.clickOnImage(1);
    solo.assertCurrentActivity("expected edit activity", EditIncidentActivity.class);

    solo.pressSpinnerItem(0, 1);
    solo.typeText(0, "Run for the hills");

    final View createButton = solo.getView(R.id.submit_incident);
    solo.clickOnView(createButton);

    solo.waitForFragmentById(R.id.fragment_incident_list_view);
    int incidentsAfter = getNoOfIncidentsInList();
    assertEquals(incidentsBefore + 1, incidentsAfter);
  }

  private int getNoOfIncidentsInList() {
    final ArrayList<ListView> listViews = solo.getCurrentListViews();
    return listViews.get(0) != null ? listViews.get(0).getCount() : 0;
  }

}
