package in.co.mdg.locatemyphone;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class service extends BroadcastReceiver implements LocationListener {
	Location location;
	double latitude = 0.0;
	double longitude = 0.0;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	protected LocationManager locationManager;

	@Override
	public void onReceive(final Context context, Intent intent) {
		Bundle myBundle = intent.getExtras();
		if (myBundle != null) {
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			SmsMessage shortMessage = SmsMessage
					.createFromPdu((byte[]) pdus[0]);
			String number = shortMessage.getOriginatingAddress();
			String body = shortMessage.getDisplayMessageBody();
			SharedPreferences pref = context.getSharedPreferences(
					home.PREF_NAME, 0);
			String silent = pref.getString("silent", null);
			String location1 = pref.getString("location", null);
			MediaPlayer player;
			AssetFileDescriptor afd1 = null;
			try {
				afd1 = context.getAssets().openFd("Audio.mp3");
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			if (body.equals(silent)) {
				player = new MediaPlayer();
				try {
					AudioManager audioManager = (AudioManager) context
							.getSystemService(Context.AUDIO_SERVICE);
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					audioManager
							.setStreamVolume(
									AudioManager.STREAM_MUSIC,
									audioManager
											.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
									0);
					player.setDataSource(afd1.getFileDescriptor(),
							afd1.getStartOffset(), afd1.getLength());
					player.prepare();
				} catch (IllegalArgumentException e) {

					e.printStackTrace();
				} catch (IllegalStateException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
				player.start();
			} else if (body.equals(location1)) {
				// setMobileDataEnabled(context, true);
				getLocation(context);
				while (latitude == 0.0 && longitude == 0.0) {

					CountDownTimer timer = new CountDownTimer(5 * 1000, 1000) {

						public void onTick(long millisUntilFinished) {
							Toast.makeText(
									context,
									Double.toString(latitude) + "+"
											+ Double.toString(longitude), 1000)
									.show();
						}

						public void onFinish() {
							getLocation(context);
						}
					};
				}
				// turngpson(context);
				Toast.makeText(
						context,
						Double.toString(latitude) + "+"
								+ Double.toString(longitude),
						Toast.LENGTH_SHORT).show();

				// setMobileDataEnabled(context, false);
				// turnGPSOff(context);
				sendmessage(context, location1, number, latitude, longitude);
				latitude = 0.0;
				longitude = 0.0;
			} else {
				String[] values = body.split(" ");
				Toast.makeText(context, values[1], Toast.LENGTH_SHORT).show();
				if (values[0].equals(location1)) {
					getLocation(context);
					String uri = "http://maps.google.com/maps?saddr="
							+ latitude + "," + longitude + "&daddr="
							+ values[1] + "," + values[2];
					Intent intent1 = new Intent(
							android.content.Intent.ACTION_VIEW, Uri.parse(uri));
					intent1.setClassName("com.google.android.apps.maps",
							"com.google.android.maps.MapsActivity");
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent1);
				}
			}
		}
	}

	private void turngpson(Context context) {

		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		context.sendBroadcast(intent);

		@SuppressWarnings("deprecation")
		String provider = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) {
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);

		}
	}

	public void turnGPSOff(Context context) {
		@SuppressWarnings("deprecation")
		String provider = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("gps")) {
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);
		}
	}

	private void setMobileDataEnabled(Context context, boolean enabled) {
		final ConnectivityManager conman = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("rawtypes")
		Class conmanClass;
		try {
			conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass
					.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField
					.get(conman);
			@SuppressWarnings("rawtypes")
			final Class iConnectivityManagerClass = Class
					.forName(iConnectivityManager.getClass().getName());
			@SuppressWarnings("unchecked")
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);

			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (NoSuchFieldException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}

	}

	private void sendmessage(final Context context, String location1,
			String number, Double latitude, Double longitude) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(number, null, location1 + " " + latitude + " "
				+ longitude, null, null);
	}

	private void getLocation(Context context) {
		try {
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
			Log.d("Network", "Network");

			if (locationManager != null) {
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

				latitude = location.getLatitude();
				longitude = location.getLongitude();
				Toast.makeText(
						context,
						Double.toString(latitude) + "+"
								+ Double.toString(longitude),
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}