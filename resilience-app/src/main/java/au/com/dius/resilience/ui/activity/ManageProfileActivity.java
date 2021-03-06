package au.com.dius.resilience.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import au.com.dius.resilience.R;
import au.com.dius.resilience.model.Profile;
import au.com.dius.resilience.persistence.repository.impl.PreferenceAdapter;
import au.com.dius.resilience.persistence.repository.impl.ProfileRepository;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.LinkedHashSet;
import java.util.Set;

import static au.com.dius.resilience.observer.PreferenceChangeBroadcastReceiver.PREFERENCES_UPDATED_FILTER;

@ContentView(R.layout.activity_manage_profile)
public class ManageProfileActivity extends RoboActivity {

  @InjectView(R.id.profile_name)
  private EditText profileName;

  PreferenceAdapter preferenceAdapter;
  ProfileRepository repository;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    preferenceAdapter = new PreferenceAdapter(this);
    repository = new ProfileRepository(this);
  }

  public void onSaveProfileClick(View button) {
    String profileName = this.profileName.getText().toString();

    Profile newProfile = new Profile(profileName);
    Set<Profile> profileEntries = repository.findAll();

    if (newProfile.getName().length() == 0 || profileEntries.contains(newProfile)) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setPositiveButton(android.R.string.ok, null);
      builder.setMessage(R.string.profile_exists_error);
      builder.show();
    } else {
      profileEntries.add(newProfile);

      Set<String> names = new LinkedHashSet<String>();
      for (Profile profile : profileEntries) {
        names.add(profile.getName());
      }
      preferenceAdapter.save(preferenceAdapter.getCommonPreferences(), R.string.profile_entries, names);
      preferenceAdapter.save(preferenceAdapter.getCommonPreferences(), R.string.current_profile_key, newProfile.getId());

      sendBroadcast(new Intent(PREFERENCES_UPDATED_FILTER));
      Intent preferencesIntent = new Intent(this, PreferenceActivity.class);
      startActivity(preferencesIntent);
    }
  }
}