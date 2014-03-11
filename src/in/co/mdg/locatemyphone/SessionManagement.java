package in.co.mdg.locatemyphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManagement {
	SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "filename";
    public static final String Silent = "silent";
    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    public void createKey(String key){
        editor.putString(Silent, key); 
        editor.commit();
    }   
    public void changekey(){
    	editor.clear();
    	editor.commit();
    }    
}