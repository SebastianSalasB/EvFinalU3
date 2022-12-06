package com.msotor.implemqttv5;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivityList extends AppCompatActivity {
    private TextView tv1,tv2,tv3,tv4,tv5;

    private ListView lv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        tv1=findViewById(R.id.textView);
        tv2=findViewById(R.id.textView2);
        tv3=findViewById(R.id.textView4);
        tv4=findViewById(R.id.textView5);
        tv5=findViewById(R.id.textView6);
        lv1=findViewById(R.id.listv);


        AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos =admin .getWritableDatabase();
        Cursor file=BaseDeDatos.rawQuery("select * from articulos ",null);
        ArrayList<String> datos=new ArrayList<>();
        ArrayList<String>listD=new ArrayList<>();
        ArrayList<String>listD1=new ArrayList<>();
        ArrayList<String>listD2=new ArrayList<>();
        ArrayList<String>listD3=new ArrayList<>();
        String id ,nombre ,numero,fecha,hora;

        while (file.moveToNext()){
            tv1.setText(file.getString(0));
            id=tv1.getText().toString();
            datos.add(id);

        }
        Cursor file1=BaseDeDatos.rawQuery("select descripcion,precio,fecha,hora from articulos",null);

        while (file1.moveToNext()){

        tv2.setText(file1.getString(0));
        tv3.setText(file1.getString(1));
        tv4.setText(file1.getString(2));
        tv5.setText(file1.getString(3));
        nombre=tv2.getText().toString();
        numero=tv3.getText().toString();
        fecha=tv4.getText().toString();
        hora=tv5.getText().toString();
        listD.add(nombre);
        listD1.add(numero);
        listD2.add(fecha);
        listD3.add(hora);

        }



        ArrayAdapter adapter=new ArrayAdapter(this, R.layout.list_item_ssalas,listD);
        lv1.setAdapter(adapter);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv1.setText("el dato "+lv1.getItemAtPosition(i)+" tiene un numero de "+listD1.get(i)
                        +" esta registrado en la fecha "+listD2.get(i)+" en la hora "+listD3.get(i));
                //ssolo
            }
        });




    }
}