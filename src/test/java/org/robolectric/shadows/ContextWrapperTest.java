package org.robolectric.shadows;

import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;
import org.robolectric.util.Transcript;

import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static junit.framework.Assert.assertEquals;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(TestRunners.WithDefaults.class)
public class ContextWrapperTest {
    public Transcript transcript;
    private ContextWrapper contextWrapper;

    @Before public void setUp() throws Exception {
        transcript = new Transcript();
        contextWrapper = new ContextWrapper(new Activity());
    }

    @Test
    public void registerReceiver_shouldRegisterForAllIntentFilterActions() throws Exception {
        BroadcastReceiver receiver = broadcastReceiver("Larry");
        contextWrapper.registerReceiver(receiver, intentFilter("foo", "baz"));

        contextWrapper.sendBroadcast(new Intent("foo"));
        transcript.assertEventsSoFar("Larry notified of foo");

        contextWrapper.sendBroadcast(new Intent("womp"));
        transcript.assertNoEventsSoFar();

        contextWrapper.sendBroadcast(new Intent("baz"));
        transcript.assertEventsSoFar("Larry notified of baz");
    }

    @Test
    public void sendBroadcast_shouldSendIntentToEveryInterestedReceiver() throws Exception {
        BroadcastReceiver larryReceiver = broadcastReceiver("Larry");
        contextWrapper.registerReceiver(larryReceiver, intentFilter("foo", "baz"));

        BroadcastReceiver bobReceiver = broadcastReceiver("Bob");
        contextWrapper.registerReceiver(bobReceiver, intentFilter("foo"));

        contextWrapper.sendBroadcast(new Intent("foo"));
        transcript.assertEventsSoFar("Larry notified of foo", "Bob notified of foo");

        contextWrapper.sendBroadcast(new Intent("womp"));
        transcript.assertNoEventsSoFar();

        contextWrapper.sendBroadcast(new Intent("baz"));
        transcript.assertEventsSoFar("Larry notified of baz");
    }

    @Test
    public void unregisterReceiver_shouldUnregisterReceiver() throws Exception {
        BroadcastReceiver receiver = broadcastReceiver("Larry");

        contextWrapper.registerReceiver(receiver, intentFilter("foo", "baz"));
        contextWrapper.unregisterReceiver(receiver);

        contextWrapper.sendBroadcast(new Intent("foo"));
        transcript.assertNoEventsSoFar();
    }

    @Test(expected = IllegalArgumentException.class)
    public void unregisterReceiver_shouldThrowExceptionWhenReceiverIsNotRegistered() throws Exception {
        contextWrapper.unregisterReceiver(new AppWidgetProvider());
    }

    @Test
    public void broadcastReceivers_shouldBeSharedAcrossContextsPerApplicationContext() throws Exception {
        BroadcastReceiver receiver = broadcastReceiver("Larry");

        new ContextWrapper(Robolectric.application).registerReceiver(receiver, intentFilter("foo", "baz"));
        new ContextWrapper(Robolectric.application).sendBroadcast(new Intent("foo"));
        Robolectric.application.sendBroadcast(new Intent("baz"));
        transcript.assertEventsSoFar("Larry notified of foo", "Larry notified of baz");

        new ContextWrapper(Robolectric.application).unregisterReceiver(receiver);
    }
	
	@Test
	public void broadcasts_shouldBeLogged() {
		Intent broadcastIntent = new Intent("foo");
		contextWrapper.sendBroadcast(broadcastIntent);
		
		List<Intent> broadcastIntents = shadowOf(contextWrapper).getBroadcastIntents();
		assertTrue(broadcastIntents.size() == 1);
		assertEquals(broadcastIntent, broadcastIntents.get(0));
	}
	

    @Test
    public void shouldReturnSameApplicationEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getApplication()).isSameAs(activity.getApplication());

        assertThat(activity.getApplication()).isSameAs(new Activity().getApplication());
    }

    @Test
    public void shouldReturnSameApplicationContextEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getApplicationContext()).isSameAs(activity.getApplicationContext());

        assertThat(activity.getApplicationContext()).isSameAs(new Activity().getApplicationContext());
    }

    @Test
    public void shouldReturnSameContentResolverEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getContentResolver()).isSameAs(activity.getContentResolver());

        assertThat(activity.getContentResolver()).isSameAs(new Activity().getContentResolver());
    }

    @Test
    public void shouldReturnSameLocationManagerEveryTime() throws Exception {
        assertSameInstanceEveryTime(Context.LOCATION_SERVICE);
    }

    @Test
    public void shouldReturnSameWifiManagerEveryTime() throws Exception {
        assertSameInstanceEveryTime(Context.WIFI_SERVICE);
    }

    @Test
    public void shouldReturnSameAlarmServiceEveryTime() throws Exception {
        assertSameInstanceEveryTime(Context.ALARM_SERVICE);
    }

    @Test
    public void checkPermissionsShouldReturnPermissionGrantedToAddedPermissions() throws Exception {
        shadowOf(contextWrapper).grantPermissions("foo", "bar");
        assertThat(contextWrapper.checkPermission("foo", 0, 0)).isEqualTo(PERMISSION_GRANTED);
        assertThat(contextWrapper.checkPermission("bar", 0, 0)).isEqualTo(PERMISSION_GRANTED);
        assertThat(contextWrapper.checkPermission("baz", 0, 0)).isEqualTo(PERMISSION_DENIED);
    }

    @Test
    public void shouldReturnAContext() {
        assertThat(contextWrapper.getBaseContext()).isNotNull();
    	ShadowContextWrapper shContextWrapper = Robolectric.shadowOf(contextWrapper);
    	shContextWrapper.attachBaseContext(null);
        assertThat(contextWrapper.getBaseContext()).isNull();

    	Activity baseContext = new Activity();
    	shContextWrapper.attachBaseContext(baseContext);
        assertThat(contextWrapper.getBaseContext()).isSameAs((Context) baseContext);
    }

    private void assertSameInstanceEveryTime(String serviceName) {
        Activity activity = new Activity();
        assertThat(activity.getSystemService(serviceName)).isSameAs(activity.getSystemService(serviceName));

        assertThat(activity.getSystemService(serviceName)).isSameAs(new Activity().getSystemService(serviceName));
    }

    @Test
    public void bindServiceDelegatesToShadowApplication() {
        contextWrapper.bindService(new Intent("foo"), new TestService(), Context.BIND_AUTO_CREATE);
        assertEquals("foo", shadowOf(Robolectric.application).getNextStartedService().getAction());
    }

    private BroadcastReceiver broadcastReceiver(final String name) {
        return new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                transcript.add(name + " notified of " + intent.getAction());
            }
        };
    }

    private IntentFilter intentFilter(String... actions) {
        IntentFilter larryIntentFilter = new IntentFilter();
        for (String action : actions) {
            larryIntentFilter.addAction(action);
        }
        return larryIntentFilter;
    }

    @Test
    public void packageManagerShouldNotBeNullWhenWrappingAnApplication() {
        assertThat(new Application().getPackageManager()).isNotNull();
    }

    @Test
    public void checkCallingPermissionShouldGrantPermissionByDefault() throws Exception {
        assertThat(contextWrapper.checkCallingPermission("")).isEqualTo(PERMISSION_GRANTED);
    }
}
