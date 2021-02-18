package kosmo.project3.schlineapp;

import android.content.SharedPreferences;

import java.util.HashSet;

public class StaticUserInformation {
    public static String userID = null;

    public static HashSet<String> roomSet = new HashSet<String>();

    public static void loadData(SharedPreferences preferences) {

        kosmo.project3.schlineapp.StaticUserInformation.userID = preferences.getString("userID", null);
    }

    public static void resetDate(SharedPreferences preferences){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("userID", null );

        editor.apply();
        editor.commit();

        kosmo.project3.schlineapp.StaticUserInformation.userID =preferences.getString("userID", null);
    }
}
