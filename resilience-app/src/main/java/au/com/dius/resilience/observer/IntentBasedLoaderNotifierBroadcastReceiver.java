package au.com.dius.resilience.observer;

import android.content.*;
import au.com.dius.resilience.loader.ServiceRequestLoader;

public class IntentBasedLoaderNotifierBroadcastReceiver extends BroadcastReceiver {

  private final Loader loader;

  public IntentBasedLoaderNotifierBroadcastReceiver(Loader loader, IntentFilter intentFilter, IntentFilter ... intentFilters) {
    if (intentFilter == null) {
      throw new IllegalArgumentException("intentFilter cannot be null");
    }

    this.loader = loader;

    registerFilter(loader, intentFilter);
    for (IntentFilter filter : intentFilters) {
      registerFilter(loader, filter);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    ((ServiceRequestLoader)loader).resetPage();
    loader.onContentChanged();
  }

  private void registerFilter(Loader loader, IntentFilter filter) {
    loader.getContext().registerReceiver(this, filter);
  }

}
