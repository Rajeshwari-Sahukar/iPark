package ucsd.cse110fa16.group14.ipark;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class iLink {
    private static String usersNode = "https://ipark-e243b.firebaseio.com/Users/";
    private static String parkingLot = "https://ipark-e243b.firebaseio.com/ParkingLot/";
    private static User user;
    private static FirebaseAuth auth;
    protected static ArrayList<String> personalInfoData;

    public static Task changePassword(String username, String newPassword) {
        auth = FirebaseAuth.getInstance();
        Task task = auth.getCurrentUser().updatePassword(newPassword);
        if (task.isSuccessful()) {
            Firebase passwordRef = new Firebase(usersNode + username + "/password");
            passwordRef.setValue(newPassword);
        }
        return task;
    }

    public static Task changeEmail(String username, String newEmail) {
        auth = FirebaseAuth.getInstance();
        Task task = auth.getCurrentUser().updatePassword(newEmail);
        if (task.isSuccessful()) {
            Firebase emailRef = new Firebase(usersNode + username + "/email");
            emailRef.setValue(newEmail);
            DriverRegistration.uMapEmail.put(username, newEmail);
        }
        return task;
    }

    public static void changeStartTime(String spot, String newStartTime) {
        Firebase startTimeRef = new Firebase(usersNode + spot + "/StartTime");
        startTimeRef.setValue(newStartTime);
    }

    public static void changeEndTime(String spot, String newEndTime) {
        Firebase endTimeRef = new Firebase(parkingLot + spot + "/EndTime");
        endTimeRef.setValue(newEndTime);
    }

    public static void changePrice(String spot, String newPrice) {
        Firebase priceRef = new Firebase(parkingLot + spot + "/Price");
        priceRef.setValue(newPrice);
    }

    public static void changeLegalStatus(String spot, boolean newStatus) {
        Firebase legalRef = new Firebase(parkingLot + spot + "/Illegal");
        legalRef.setValue(newStatus);
    }

    public static void changeReserveStatus(String spot, boolean newStatus) {
        Firebase reserveRef = new Firebase(parkingLot + spot + "/Reserved");
        reserveRef.setValue(newStatus);
    }

    /**
     * getDataFromFirebase returns a HashMap with the name of the first child node as the key
     * and the value of the inner child as the value.
     *
     * @param mainKey  "Folder" in the firebase you want to access. For example, Users or ParkingLot
     * @param innerKey specific data you want to access. For example, username or the end time of a parking spot.
     * @return HashMap with the name of the data we are accessing to as key and the value of what we want as a value.
     */
    protected HashMap getDataMapFromFirebase(String mainKey, final String innerKey) {

        //Getting all the usernames
        Firebase fReference = new Firebase("https://ipark-e243b.firebaseio.com/" + mainKey);
        final HashMap<String, String> map = new HashMap<>();

        fReference.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                Iterable<com.firebase.client.DataSnapshot> mainNodeData = dataSnapshot.getChildren();
                Iterator<com.firebase.client.DataSnapshot> iterator = mainNodeData.iterator();

                //Getting mainNode keys
                while (iterator.hasNext()) {
                    com.firebase.client.DataSnapshot node = iterator.next();
                    String mainNodeKey = node.getKey();

                    Iterable<com.firebase.client.DataSnapshot> innerNodeData = node.getChildren();
                    Iterator<com.firebase.client.DataSnapshot> iterator1 = innerNodeData.iterator();

                    //Getting innerNodeKey's value
                    while (iterator1.hasNext()) {
                        com.firebase.client.DataSnapshot innerNode = iterator1.next();
                        String currentKey = innerNode.getKey();
                        if (currentKey.equals(innerKey)) {
                            String innerNodeValue = innerNode.getValue(String.class);
                            map.put(mainNodeKey, innerNodeValue);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("NO ACCESS ERROR", "Could not connect to Firebase");
            }
        });
        return map;
    }

    /**
     * Takes the root, firstChild, and secondChild and returns the String value of it.
     * @param root Users, ParkingLot, or History
     * @param firstChild username, spot, or the whole date/time thing
     * @param secondChild example: email, username, EndTime, or Clockin
     * @return the string value of the things. Example Users->admin->email = www123@gmail.com.
     */
    protected static HashMap<String,String> getPersonalInfoFromFirebase(String root, final String userName ) {

        String ref = "https://ipark-e243b.firebaseio.com/" + root+
                "/"+userName+"/";
        Firebase fReference = new Firebase(ref);
        final HashMap<String,String> map = new HashMap<>();
        fReference.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                Iterable<com.firebase.client.DataSnapshot> firstChildData = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = firstChildData.iterator();

                while(iterator.hasNext()){
                    DataSnapshot data = iterator.next();
                    String key = data.getKey();
                    if(!key.equals("owner") && !key.equals("password")){
                        String val = data.getValue(String.class);
                        map.put(key,val);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("NO ACCESS ERROR", "Could not connect to Firebase");
            }
        });
        return map;
    }
}
/*
    public static User getCurrentUser(){
        user = new User();
        auth = FirebaseAuth.getInstance();
        String name = auth.getCurrentUser().getDisplayName();
        Firebase userRef = new Firebase(usersNode + name);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> info = dataSnapshot.getChildren();
                Iterator<DataSnapshot> infoIterator = info.iterator();
                while(infoIterator.hasNext()){
                    DataSnapshot node = infoIterator.next();
                    String vals = node.getKey();

                    switch (vals){
                        case "email":
                            user.setStringEmail(node.getValue(String.class));
                            break;
                        case "license":
                            user.setStringLicense(node.getValue(String.class));
                            break;
                        case "name":
                            break;
                        case "username":
                            user.setStringUsername(node.getValue(String.class));
                            break;
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("","Error retrieving data");
            }
        });


        return user;
    }
    */
