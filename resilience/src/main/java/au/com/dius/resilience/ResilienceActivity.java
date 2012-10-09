package au.com.dius.resilience;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ResilienceActivity extends Activity {

  private static final String LOG_TAG = "ResilienceActivity";

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.action_bar, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.all_issues:
        Log.d(LOG_TAG, "All issues selected");
        break;
      case R.id.tracked_issues:
        Log.d(LOG_TAG, "Tracked issues selected");
        break;
      case R.id.raise_incident:
        Log.d(LOG_TAG, "Raise incident selected");
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }
}
