package au.com.dius.resilience.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import au.com.dius.resilience.R;
import au.com.dius.resilience.model.ImpactScale;
import au.com.dius.resilience.model.Incident;
import au.com.dius.resilience.model.IncidentFactory;
import au.com.dius.resilience.model.Photo;
import au.com.dius.resilience.persistence.Repository;
import au.com.dius.resilience.persistence.RepositoryFactory;

import java.util.Date;

public class EditIncidentActivity extends Activity implements OnSeekBarChangeListener {

  private Spinner categorySpinner;
  private Spinner subCategorySpinner;
  private SeekBar impactScale;
  private EditText notes;
  
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_incident);

    categorySpinner = (Spinner) findViewById(R.id.category_spinner);
    subCategorySpinner = (Spinner) findViewById(R.id.sub_category_spinner);
    initialiseSpinners();
    
    impactScale = (SeekBar) findViewById(R.id.impact_scale);
    impactScale.setOnSeekBarChangeListener(this);
    
    notes = (EditText) findViewById(R.id.notes);
    
    // FIXME - test this (-xxx-camera args not taking effect on my emulator)
    PackageManager pm = getPackageManager();
    boolean deviceHasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    Button cameraButton = (Button) findViewById(R.id.submit_photo);
    cameraButton.setEnabled(deviceHasCamera);
  }

  private void initialiseSpinners() {
    ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
        R.array.categories, android.R.layout.simple_spinner_item);
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    categorySpinner.setAdapter(categoryAdapter);
    
    ArrayAdapter<CharSequence> subCategoryadapter = ArrayAdapter.createFromResource(this,
        R.array.subcategories, android.R.layout.simple_spinner_item);
    subCategoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    subCategorySpinner.setAdapter(subCategoryadapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_edit_incident, menu);
    updateImpactLabel(ImpactScale.LOW);
    return true;
  }

  public void onSubmitClick(View button) {
    String incidentNote = notes.getText().toString();

    String category = categorySpinner.getSelectedItem().toString();
    String subCategory = subCategorySpinner.getSelectedItem().toString();
    ImpactScale impact = ImpactScale.fromCode(impactScale.getProgress());
    
    Repository<Incident> repository = RepositoryFactory.createIncidentRepository(this);
    Incident incident = IncidentFactory.createIncident(category, Long.valueOf(new Date().getTime()), incidentNote, category, subCategory, impact);

    Log.d(getClass().getName(), "Saving incident: " + incident.toString());
    repository.save(incident);
  }
  
  public void onCameraClick(View button) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    
    // TODO - put in AsyncTask since this carries out file
    // operations.
    Uri filesystemDestination = Photo.getOutputMediaFile();
    
    intent.putExtra(MediaStore.EXTRA_OUTPUT, filesystemDestination);
    
    // TOOD - wtf is CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    ImpactScale scale = ImpactScale.fromCode(progress);
    updateImpactLabel(scale);
  }

  private void updateImpactLabel(ImpactScale scale) {
    TextView impactDescription = (TextView) findViewById(R.id.impact_scale_desc);
    impactDescription.setText(scale.name());
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    
  }
}