package com.example.majkel.myapplication;
import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;
public class MainActivity extends AppCompatActivity {
    ArrayList<DataModel> dataModels;
    Dialog customDialog;
    RadioButton rad1;
    RadioButton rad2;
    ListView listView;
    private static CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        listView=(ListView)findViewById(R.id.list);
        dataModels= new ArrayList<>();

        final RealmResults<DataModel> data = realm.where(DataModel.class).findAll();
        Toast.makeText(this, realm.isEmpty()+"",
                Toast.LENGTH_LONG).show();
      if(realm.isEmpty()){
          realm.beginTransaction();
        DataModel tr= new DataModel();

        for(int i=0;i<100;i++){
            dataModels.add(new DataModel(i+".12.2018", "profesjonalny zapis do bazy "+i, i%2==0,"1"));
            tr= realm.copyToRealm(dataModels.get(i));

        }
          realm.commitTransaction();
      }
        else
      {
          for(int i=0;i<data.size();i++){
              dataModels.add(data.get(i));
          }
      }
        adapter= new CustomAdapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customDialog = new Dialog(MainActivity.this);
                customDialog.setContentView(R.layout.item_edit_add);
                customDialog.show();
                DataModel temp = dataModels.get(position);
                Button button = (Button)customDialog.findViewById(R.id.agreeAddButton);
                EditText editName = (EditText)customDialog.findViewById(R.id.editName);
                RadioButton prio1 = (RadioButton)customDialog.findViewById(R.id.prio1);
                RadioButton prio2 = (RadioButton)customDialog.findViewById(R.id.prio2);
                RadioButton prio3 = (RadioButton)customDialog.findViewById(R.id.prio3);
                editName.setText(temp.item);
                switch (temp.priority){
                    case "1":prio1.setChecked(true);break;
                    case "2":prio2.setChecked(true);break;
                    case "3":prio3.setChecked(true);break;
                        default:break;
                }
                button.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                 if(editName.getText()!=null&&!(editName.getText().equals(""))&&editName.getText().length()!=0){
                                                     Realm realm = Realm.getDefaultInstance();
                                                  realm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          RealmResults<DataModel> test = realm.where(DataModel.class).equalTo("date", temp.date).equalTo("item", temp.item).equalTo("tick", temp.tick).equalTo("priority", temp.priority).findAll();
                                                          String a="";
                                                          if(prio1.isChecked()) a="1";
                                                          if(prio2.isChecked()) a="2";
                                                          if(prio3.isChecked()) a="3";
                                                          temp.tick=true;
                                                          temp.item=editName.getText().toString();
                                                          temp.priority=a;
                                                       // test.get(0).priority=temp.priority;
                                                         // test.get(0).item=temp.item;
                                                          //test.get(0).tick=temp.tick;
                                                      }
                                                  });
                                                  adapter.notifyDataSetChanged();
                                                  customDialog.cancel();
                                              }}
                                          }
                );

            }}
                );
                Button add = (Button) findViewById(R.id.add);
                EditText text = (EditText) findViewById(R.id.text);
                add.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               String newItem = text.getText().toString();
                                              if(newItem!=null&&!(newItem.equalsIgnoreCase(""))){ Date today = new Date();
                                               SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd,ha");

                                               today.setHours(0);


                                                   DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
                                                   Date todaysDate = new Date();
                                                   String str2 = df2.format(todaysDate);

                                               DataModel temp = new DataModel(str2, newItem, true, "1");
                                               dataModels.add(temp);
                                               realm.beginTransaction();
                                               realm.copyToRealm(temp);
                                               realm.commitTransaction();
                                               adapter.notifyDataSetChanged();
                                           }}
                                       }
                );
        Button txt = (Button)findViewById(R.id.txtButton);
        txt.setOnClickListener(new View.OnClickListener(){
                                   @Override
                                   public void onClick(View v) {
                                           writer();
                                   }
                               }
        );
        Button sortB = (Button)findViewById(R.id.sortButton);
        sortB.setOnClickListener(new View.OnClickListener(){
                                   @Override
                                   public void onClick(View v) {
                                       customDialog = new Dialog(MainActivity.this);
                                       customDialog.setContentView(R.layout.custom_dialog);
                                       customDialog.show();
                                       rad1 = (RadioButton)customDialog.findViewById(R.id.alfRadio1);
                                       rad2 = (RadioButton)customDialog.findViewById(R.id.alfRadio2);

Button button = (Button)customDialog.findViewById(R.id.agreeButton);
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       if(rad1.isChecked()) sort1(dataModels,1);
        if(rad2.isChecked()) sort1(dataModels,2);
        adapter.notifyDataSetChanged();
customDialog.cancel();
    }
}
);
                                   }
                               }
        );
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            dataModels.remove(position);
            realm.beginTransaction();
            data.deleteFromRealm(position);
            realm.commitTransaction();
            adapter.notifyDataSetChanged();
            return true;
        });
        Button addB = (Button)findViewById(R.id.addButton);
        addB.setOnClickListener(new View.OnClickListener(){
                                     @Override
                                     public void onClick(View v) {
                                         customDialog = new Dialog(MainActivity.this);
                                         customDialog.setContentView(R.layout.item_edit_add);
                                         customDialog.show();
                                         Button button = (Button)customDialog.findViewById(R.id.agreeAddButton);
                                         EditText editName1 = (EditText)customDialog.findViewById(R.id.editName);
                                         RadioButton prio1 = (RadioButton)customDialog.findViewById(R.id.prio1);
                                         RadioButton prio2 = (RadioButton)customDialog.findViewById(R.id.prio2);
                                         RadioButton prio3 = (RadioButton)customDialog.findViewById(R.id.prio3);
                                         button.setOnClickListener(new View.OnClickListener() {
                                                                       @Override
                                                                       public void onClick(View v) {
if(editName1.getText()!=null&&!(editName1.getText().equals(""))&&editName1.getText().length()!=0){
                                                                           String a="";
                                                                           if(prio1.isChecked()) a="1";
                                                                           if(prio2.isChecked()) a="2";
                                                                           if(prio3.isChecked()) a="3";
                                                                           DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
                                                                           Date todaysDate = new Date();
                                                                           String str2 = df2.format(todaysDate);
                                                                           DataModel temp = new DataModel(str2, editName1.getText().toString(), false,a);
                                                                            dataModels.add(temp);
                                                                           realm.beginTransaction();
                                                                           realm.copyToRealm(temp);
                                                                           realm.commitTransaction();
                                                                           adapter.notifyDataSetChanged();
                                                                           customDialog.cancel();
                                                                       }
                                                                       }
                                                                   }
                                         );
                                     }
                                 }
        );
        }

    public void writer(){
requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
    File folder = new File(Environment.getExternalStorageDirectory(),"DataBase");
    if(!folder.exists())folder.mkdir();
        File file = new File(folder,"plik.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
                Toast.makeText(MainActivity.this,"cos robie ale nie robie",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this,e.toString(),
                        Toast.LENGTH_SHORT).show();}
        }
    try{

    DataModel a;
   // FileWriter writer = new FileWriter(file);
        FileOutputStream fos = new FileOutputStream(file);
    for(int i=0;i<dataModels.size();i++){
       a = dataModels.get(i);
     //   writer.append(i+" "+a.date+" "+a.item+" tick is "+a.tick+"\n");
        fos.write((i+" "+a.date+" "+a.item+" tick is "+a.tick+"\n").getBytes());
    }
   // writer.flush();
    fos.close();
   // writer.close();
   }
   catch (IOException e){
       Toast.makeText(MainActivity.this,e.toString(),
               Toast.LENGTH_SHORT).show();
   }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void sort1(List<DataModel> playerList, int i) {
        Collections.sort(playerList, new Comparator<DataModel>() {
            public int compare(DataModel p1, DataModel p2) {
               switch (i){
               case 1:return p1.getItem().compareToIgnoreCase(p2.getItem());
                   case 2:return p1.getPriority().compareToIgnoreCase(p2.getPriority());
               default:return 0;}

            }

        });
        if(i==2) Collections.reverse(playerList);

    }
}
