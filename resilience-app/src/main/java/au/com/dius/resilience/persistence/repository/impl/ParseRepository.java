package au.com.dius.resilience.persistence.repository.impl;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import au.com.dius.resilience.Constants;
import au.com.dius.resilience.facade.CameraFacade;
import au.com.dius.resilience.model.Incident;
import au.com.dius.resilience.model.Photo;
import au.com.dius.resilience.persistence.repository.Repository;
import com.google.inject.Inject;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import roboguice.inject.ContextSingleton;

import java.util.ArrayList;
import java.util.List;

@ContextSingleton
public class ParseRepository implements Repository {

  private static final String TAG = ParseRepository.class.getName();

  final ParseIncidentAdapter incidentAdapter;

  @Inject
  public ParseRepository(ParseIncidentAdapter incidentAdapter) {
    this.incidentAdapter = incidentAdapter;
  }

  @Override
  public List<Incident> findIncidents() {
    ParseQuery query = new ParseQuery(Constants.TABLE_INCIDENT);
    query.orderByDescending(Constants.COL_INCIDENT_CREATION_DATE);

    List<ParseObject> parseObjects = loadIncidents(query);

    List<Incident> incidents = new ArrayList<Incident>();
    for (ParseObject parseIncident : parseObjects) {
      incidents.add(incidentAdapter.deserialise(parseIncident));
    }

    return incidents;
  }

  @Override
  public Photo findPhotoByIncident(final String incidentId) {
    ParseQuery parseQuery = new ParseQuery(Constants.TABLE_INCIDENT);

    Photo photo = null;
    try {
      ParseObject incident = parseQuery.get(incidentId);
      final ParseFile parseFile = (ParseFile) incident.get(Constants.COL_INCIDENT_PHOTO);
      if (parseFile != null) {
        Log.d(TAG, "Found photo for incident " + incidentId + ", retrieving data..");

        byte[] data = parseFile.getData();
        Log.d(TAG, "Retrieved " + (data == null ? 0 : data.length) + " bytes of data.");

        Bitmap bitmap = CameraFacade.decodeBytes(data);
        photo = new Photo(Uri.parse(parseFile.getUrl()), bitmap);
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return photo;
  }

  private List<ParseObject> loadIncidents(ParseQuery query) {
    try {
      return query.find();
    } catch (ParseException e) {
      Log.d(TAG, "Loading all incidents failed with: ", e);
      throw new RuntimeException(e);
    }
  }
}