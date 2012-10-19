package au.com.dius.resilience.persistence.repository.impl;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import au.com.dius.resilience.Constants;
import au.com.dius.resilience.facade.CameraFacade;
import au.com.dius.resilience.model.Incident;
import au.com.dius.resilience.model.Photo;
import au.com.dius.resilience.persistence.repository.PhotoRepository;
import au.com.dius.resilience.persistence.repository.RepositoryCommandResult;
import au.com.dius.resilience.persistence.repository.RepositoryCommandResultListener;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class ParsePhotoRepository implements PhotoRepository {

  public static final String LOG_TAG = ParsePhotoRepository.class.getName();
  
  @Override
  public void save(final RepositoryCommandResultListener<Incident> listener,
      final Photo photo, final Incident incident) {
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        byte[] bytes = CameraFacade.extractBytes(photo);
        final ParseFile parseFile = new ParseFile(Constants.PHOTO_FILENAME, bytes);
        try {
          saveIncidentWithPhoto(listener, parseFile);
        } catch (ParseException e) {
          Log.d(LOG_TAG, "Saving incident " + incident.getId() + " with photo failed.");
          listener.commandComplete(new RepositoryCommandResult<Incident>(
              false, incident));
        }
      }

      private void saveIncidentWithPhoto(
          final RepositoryCommandResultListener<Incident> listener,
          final ParseFile parseFile) throws ParseException {
        parseFile.save();
        final ParseObject parseObject = ParseObject.createWithoutData(Constants.TABLE_INCIDENT, incident.getId());
        if (parseObject.isDataAvailable()) {
          parseObject.fetchIfNeeded();
        }
        parseObject.put(Constants.COL_INCIDENT_PHOTO, parseFile);
        parseObject.saveEventually(new SaveCallback() {
          @Override
          public void done(ParseException ex) {
            Log.d(LOG_TAG, "Updated incident " + incident.getId() + " with photo " + (ex == null ? "succeeded." : "failed."));
            incident.setId(parseObject.getObjectId());
            listener.commandComplete(new RepositoryCommandResult<Incident>(
                ex == null, incident));
          }
        });
      }
    });

  }

  @Override
  public void findByIncident(final RepositoryCommandResultListener<Photo> listener,
      final Incident incident) {
    
    ParseQuery parseQuery = new ParseQuery(Constants.TABLE_INCIDENT);
    parseQuery.getInBackground(incident.getId(), new GetCallback() {
      @Override
      public void done(ParseObject parseIncident, ParseException ex) {
        ParseFile parseFile = (ParseFile) parseIncident.get(Constants.COL_INCIDENT_PHOTO);
        Photo photo = null;
        if (parseFile != null) {
          Log.d(LOG_TAG, "Found photo for incident " + incident.getId());
          photo = new Photo(Uri.parse(parseFile.getUrl()));
        }
        listener.commandComplete(new RepositoryCommandResult<Photo>(ex == null && parseFile != null, photo));        
      }
    });
  }
}