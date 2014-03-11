package in.co.mdg.locatemyphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class home extends Activity implements OnClickListener, LocationListener {

	TextView tv, tv1;
	Button ed, btn;
	SharedPreferences pref;
	String keyword1, keyword2;
	static boolean done = false;
	public static String PREF_NAME = "filename";
	double latitude, longitude;
	Location location;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	protected LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homenew);
		tv = (TextView) findViewById(R.id.tVkey);
		tv1 = (TextView) findViewById(R.id.textView4);
		ed = (Button) findViewById(R.id.btkey);
		pref = getSharedPreferences(PREF_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		String result = pref.getString("silent", "No Key");
		String result1 = pref.getString("location", "No Key");
		tv.setText(result);
		tv1.setText(result1);
		editor.putInt("start", 0);
		editor.commit();
		int startresult = pref.getInt("start", 0);
		if (startresult == 0 && done == false) {
			AlertDialog.Builder alrt = new AlertDialog.Builder(this);
			alrt.setTitle("How to use..");
			alrt.setMessage("Set the keywords for differnt prposes and whenever phone recevies any of the keyword in text message from any number it sets off the corresponding function and hence helps in locating your phone..");
			alrt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			done = true;
			alrt.show();
		}
		ed.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {

		LayoutInflater li = LayoutInflater.from(this);
		View dialogview = li.inflate(R.layout.dialognew, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(dialogview);
		final EditText inputalarm = (EditText) dialogview
				.findViewById(R.id.editText1);
		final EditText inputlocation = (EditText) dialogview
				.findViewById(R.id.editText2);
		final SharedPreferences.Editor editor = pref.edit();
		String result = pref.getString("silent", "No Key");
		String result1 = pref.getString("location", "No Key");
		inputalarm.setText(result);
		inputlocation.setText(result1);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						keyword1 = inputalarm.getText().toString();
						keyword2 = inputlocation.getText().toString();
						if (keyword2.equals(keyword1)) {
							Toast.makeText(
									home.this,
									"Both Keys are same please change any one of them atleast",
									Toast.LENGTH_SHORT).show();
						} else {
							editor.putString("silent", keyword1);
							editor.putString("location", keyword2);
							editor.commit();
							String result = pref.getString("silent", "No Key");
							String result1 = pref.getString("location",
									"No Key");
							tv.setText(result);
							tv1.setText(result1);
							dialog.dismiss();
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
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