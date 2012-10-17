package au.com.dius.resilience.test.util;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Instrumentation;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import au.com.dius.resilience.Constants;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseTestUtils {

  public static void setUp(Context context) {
    Parse.initialize(context, Constants.TEST_APP_KEY, Constants.TEST_CLIENT_KEY);
    Parse.setLogLevel(Parse.LOG_LEVEL_INFO);
  }

  public static void dropAll(Instrumentation instrumentation) throws InterruptedException {
    instrumentation.runOnMainSync(new DropTables());
    instrumentation.waitForIdleSync();
  }

  private static class DropTables implements Runnable {

    @Override
    public void run() {
      try {
        ParseQuery query = new ParseQuery(Constants.TABLE_INCIDENT);
        Log.d(ParseTestUtils.class.getName(), "Finding list of objects..");
        query.findInBackground(new FindCallback() {
          @Override
          public void done(List<ParseObject> incidents, ParseException ex) {
            if (ex != null || incidents.size() == 0) {
              return;
            }

            try {
              deleteObjects(incidents);
            }
            catch(Exception e) {
              Log.e(DropTables.class.getName(), "Failed to delete objects!");
            }
          }

          private void deleteObjects(List<ParseObject> parseObjects) throws InterruptedException {
            final CountDownLatch deleteLatch = new CountDownLatch(parseObjects.size());
            for (final ParseObject i : parseObjects) {
              AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                  try {
                    i.delete();
                    Log.d(ParseTestUtils.class.getName(), "Done deleting object");
                    deleteLatch.countDown();
                  } catch (ParseException e) {
                    throw new RuntimeException(e);
                  }
                }
              });
            }

            Log.d(ParseTestUtils.class.getName(), "Clearing " + parseObjects.size() + " data objects..");
            deleteLatch.await();
          }
        });

      } catch (Exception e) {
        throw new RuntimeException("Test setup failed: ", e);
      }
    }
  }
}
